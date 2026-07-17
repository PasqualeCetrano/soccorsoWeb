package it.univaq.soccorsoweb.data.dao.impl;

import it.univaq.framework.data.DAO;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataLayer;
import it.univaq.framework.data.DataItemProxy;
import it.univaq.soccorsoweb.data.dao.MissioneDAO;
import it.univaq.soccorsoweb.data.model.Missione;
import it.univaq.soccorsoweb.data.model.Materiale;
import it.univaq.soccorsoweb.data.model.Mezzo;
import it.univaq.soccorsoweb.data.model.Utente;
import it.univaq.soccorsoweb.data.model.impl.proxy.MissioneProxy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MissioneDAO_MySQL extends DAO implements MissioneDAO {

    private PreparedStatement insertMissione;
    private PreparedStatement updateMissione;
    private PreparedStatement selectIdMissione;
    private PreparedStatement selectMissioneById;
    private PreparedStatement selectMissioniByMateriale;
    private PreparedStatement selectMissioniByMezzo;
    private PreparedStatement selectMissioniChiuseByUtente;
    private PreparedStatement selectMissioniPartecipateByUtente;
    private PreparedStatement selectMissioniInCorso;
    private PreparedStatement insertImpiegaMateriale; // aggiorna tabella impiegaMateriale
    private PreparedStatement insertImpiegaMezzo; // aggiorna tabella impiegaMezzo

    public MissioneDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            insertMissione = connection.prepareStatement(
                    "INSERT INTO Missione (posizione, obiettivo, inizio, fk_id_utente, fk_id_richiesta_soccorso) VALUES (?, ?, ?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS);

            updateMissione = connection.prepareStatement(
                    "UPDATE Missione SET posizione = ?, obiettivo = ?, livello_successo = ?, commenti = ?, inizio = ?, fine = ?, fk_id_utente = ?, fk_id_richiesta_soccorso = ? WHERE id_missione = ?");

            selectMissioniByMateriale = connection.prepareStatement(
                    "SELECT Missione.* FROM Missione INNER JOIN Impiega_Materiale ON Missione.id_missione = Impiega_Materiale.fk_id_missione WHERE Impiega_Materiale.fk_id_materiale = ?;");

            selectMissioniByMezzo = connection.prepareStatement(
                    "SELECT Missione.* FROM Missione INNER JOIN Impiega_Mezzo ON Missione.id_missione = Impiega_Mezzo.fk_id_missione WHERE Impiega_Mezzo.fk_id_mezzo = ?;");

            selectMissioniChiuseByUtente = connection.prepareStatement(
                    "SELECT * FROM Missione WHERE fk_id_utente = ? AND fine IS NOT NULL;");

            selectMissioniPartecipateByUtente = connection.prepareStatement(
                    "SELECT Missione.* FROM Missione INNER JOIN Squadra ON Missione.id_missione = Squadra.fk_id_missione INNER JOIN Partecipa ON Squadra.id_squadra = Partecipa.fk_id_squadra WHERE Partecipa.fk_id_utente = ?;");

            selectIdMissione = connection.prepareStatement(
                    "SELECT * FROM Missione WHERE id_missione = ?");

            selectMissioniInCorso = connection.prepareStatement(
                    "SELECT * FROM Missione WHERE inizio <= NOW() AND fine IS NULL;");

            insertImpiegaMateriale = connection
                    .prepareStatement("INSERT INTO Impiega_Materiale (fk_id_missione, fk_id_materiale) VALUES (?, ?);");

            insertImpiegaMezzo = connection
                    .prepareStatement("INSERT INTO Impiega_Mezzo (fk_id_missione, fk_id_mezzo) VALUES (?, ?);");

        } catch (SQLException ex) {
            throw new DataException("Error initializing Missione data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (insertMissione != null)
                insertMissione.close();
            if (updateMissione != null)
                updateMissione.close();
            if (selectMissioneById != null)
                selectMissioneById.close();
            if (selectMissioniByMateriale != null)
                selectMissioniByMateriale.close();
            if (selectMissioniByMezzo != null)
                selectMissioniByMezzo.close();
            if (selectMissioniChiuseByUtente != null)
                selectMissioniChiuseByUtente.close();
            if (selectMissioniPartecipateByUtente != null)
                selectMissioniPartecipateByUtente.close();
            if (selectMissioniInCorso != null)
                selectMissioniInCorso.close();
        } catch (SQLException ex) {
            // ignore
        }
        super.destroy();
    }

    @Override
    public Missione createMissione() {
        return new MissioneProxy(getDataLayer());
    }

    private MissioneProxy createMissione(ResultSet rs) throws DataException {
        MissioneProxy m = (MissioneProxy) createMissione();
        try {
            m.setKey(rs.getInt("id_missione"));
            m.setPosizione(rs.getString("posizione"));
            m.setObiettivo(rs.getString("obiettivo"));

            int livello = rs.getInt("livello_successo");
            if (rs.wasNull()) {
                m.setLivelloSuccesso(null);
            } else {
                m.setLivelloSuccesso(livello);
            }

            m.setCommenti(rs.getString("commenti"));

            java.sql.Timestamp inizioTs = rs.getTimestamp("inizio");
            if (inizioTs != null) {
                m.setInizio(inizioTs.toLocalDateTime());
            }

            java.sql.Timestamp fineTs = rs.getTimestamp("fine");
            if (fineTs != null) {
                m.setFine(fineTs.toLocalDateTime());
            }

            int adminId = rs.getInt("fk_id_utente");
            if (rs.wasNull()) {
                m.setAmministratoreKey(0);
            } else {
                m.setAmministratoreKey(adminId);
            }

            m.setRichiestaKey(rs.getInt("fk_id_richiesta_soccorso"));

            int squadraId = rs.getInt("id_squadra");
            if (rs.wasNull()) {
                m.setSquadraKey(0);
            } else {
                m.setSquadraKey(squadraId);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to create Missione object from ResultSet", ex);
        }
        return m;
    }

    @Override
    public void storeMissione(Missione missione) throws DataException {
        try {
            if (missione.getKey() != null && missione.getKey() > 0) {
                // UPDATE
                updateMissione.setString(1, missione.getPosizione());
                updateMissione.setString(2, missione.getObiettivo());

                if (missione.getLivelloSuccesso() != null) {
                    updateMissione.setInt(3, missione.getLivelloSuccesso());
                } else {
                    updateMissione.setNull(3, java.sql.Types.TINYINT);
                }

                updateMissione.setString(4, missione.getCommenti());

                if (missione.getInizio() != null) {
                    updateMissione.setTimestamp(5, java.sql.Timestamp.valueOf(missione.getInizio()));
                } else {
                    updateMissione.setNull(5, java.sql.Types.TIMESTAMP);
                }

                if (missione.getFine() != null) {
                    updateMissione.setTimestamp(6, java.sql.Timestamp.valueOf(missione.getFine()));
                } else {
                    updateMissione.setNull(6, java.sql.Types.TIMESTAMP);
                }

                if (missione.getAmministratore() != null) {
                    updateMissione.setInt(7, missione.getAmministratore().getKey());
                } else {
                    updateMissione.setNull(7, java.sql.Types.INTEGER);
                }

                if (missione.getRichiestaSoccorso() != null) {
                    updateMissione.setInt(8, missione.getRichiestaSoccorso().getKey());
                } else {
                    throw new DataException("Missione must be associated with a RichiestaSoccorso");
                }

                updateMissione.setInt(9, missione.getKey());

                updateMissione.executeUpdate();
            } else {
                // INSERT
                insertMissione.setString(1, missione.getPosizione());
                insertMissione.setString(2, missione.getObiettivo());

                if (missione.getLivelloSuccesso() != null) {
                    insertMissione.setInt(3, missione.getLivelloSuccesso());
                } else {
                    insertMissione.setNull(3, java.sql.Types.TINYINT);
                }

                insertMissione.setString(4, missione.getCommenti());

                if (missione.getInizio() != null) {
                    insertMissione.setTimestamp(5, java.sql.Timestamp.valueOf(missione.getInizio()));
                } else {
                    insertMissione.setNull(5, java.sql.Types.TIMESTAMP);
                }

                if (missione.getFine() != null) {
                    insertMissione.setTimestamp(6, java.sql.Timestamp.valueOf(missione.getFine()));
                } else {
                    insertMissione.setNull(6, java.sql.Types.TIMESTAMP);
                }

                if (missione.getAmministratore() != null) {
                    insertMissione.setInt(7, missione.getAmministratore().getKey());
                } else {
                    insertMissione.setNull(7, java.sql.Types.INTEGER);
                }

                if (missione.getRichiestaSoccorso() != null) {
                    insertMissione.setInt(8, missione.getRichiestaSoccorso().getKey());
                } else {
                    throw new DataException("Missione must be associated with a RichiestaSoccorso");
                }

                if (insertMissione.executeUpdate() == 1) {
                    try (ResultSet keys = insertMissione.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            missione.setKey(key);
                            // add to cache
                            dataLayer.getCache().add(Missione.class, missione);
                        }
                    }
                }
            }
            if (missione instanceof DataItemProxy) {
                ((DataItemProxy) missione).setModified(false);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to store Missione", ex);
        }
    }

    @Override
    public List<Missione> getMissioniByMateriale(Materiale materiale) throws DataException {
        List<Missione> result = new ArrayList<>();
        try {
            selectMissioniByMateriale.setInt(1, materiale.getKey());
            try (ResultSet rs = selectMissioniByMateriale.executeQuery()) {
                while (rs.next()) {
                    Missione m = createMissione(rs);
                    dataLayer.getCache().add(Missione.class, m);
                    result.add(m);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load missioni by materiale", ex);
        }
        return result;
    }

    @Override
    public List<Missione> getMissioniByMezzo(Mezzo mezzo) throws DataException {
        List<Missione> result = new ArrayList<>();
        try {
            selectMissioniByMezzo.setInt(1, mezzo.getKey());
            try (ResultSet rs = selectMissioniByMezzo.executeQuery()) {
                while (rs.next()) {
                    Missione m = createMissione(rs);
                    dataLayer.getCache().add(Missione.class, m);
                    result.add(m);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load missioni by mezzo", ex);
        }
        return result;
    }

    @Override
    public List<Missione> getMissioniChiuseByUtente(Utente utente) throws DataException {
        List<Missione> result = new ArrayList<>();
        try {
            selectMissioniChiuseByUtente.setInt(1, utente.getKey());
            try (ResultSet rs = selectMissioniChiuseByUtente.executeQuery()) {
                while (rs.next()) {
                    Missione m = createMissione(rs);
                    dataLayer.getCache().add(Missione.class, m);
                    result.add(m);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load closed missioni by utente", ex);
        }
        return result;
    }

    @Override
    public List<Missione> getMissioniPartecipateByUtente(Utente utente) throws DataException {
        List<Missione> result = new ArrayList<>();
        try {
            selectMissioniPartecipateByUtente.setInt(1, utente.getKey());
            try (ResultSet rs = selectMissioniPartecipateByUtente.executeQuery()) {
                while (rs.next()) {
                    Missione m = createMissione(rs);
                    dataLayer.getCache().add(Missione.class, m);
                    result.add(m);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load participated missioni by utente", ex);
        }
        return result;
    }

    @Override
    public Missione getMissione(int id_missione) throws DataException {
        try {
            selectMissioneById.setInt(1, id_missione);
            try (ResultSet rs = selectMissioneById.executeQuery()) {
                if (rs.next()) {
                    Missione m = createMissione(rs);
                    dataLayer.getCache().add(Missione.class, m);
                    return m;
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load missione by ID", ex);
        }
        return null;
    }

    @Override
    public List<Missione> getMissioniInCorso() throws DataException {
        List<Missione> result = new ArrayList<>();
        try (ResultSet rs = selectMissioniInCorso.executeQuery()) {
            while (rs.next()) {
                Missione m = createMissione(rs);
                dataLayer.getCache().add(Missione.class, m);
                result.add(m);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load missioni in corso", ex);
        }
        return result;
    }
}
