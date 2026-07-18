package it.univaq.soccorsoweb.data.dao.impl;

import it.univaq.framework.data.DAO;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataItemProxy;
import it.univaq.framework.data.DataLayer;
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
    private PreparedStatement selectMissioneById;
    private PreparedStatement selectMissioniByMateriale;
    private PreparedStatement selectMissioniByMezzo;
    private PreparedStatement selectMissioniChiuseByUtente;
    private PreparedStatement selectMissioniPartecipateByUtente;
    private PreparedStatement selectMissioniInCorso;
    private PreparedStatement insertImpiegaMateriale;
    private PreparedStatement insertImpiegaMezzo;

    public MissioneDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();
            // query per aprire la missione dalla richiesta di soccorso prende in automatico
            // fk_id_richiesta
            insertMissione = connection.prepareStatement(
                    "INSERT INTO Missione (posizione, obiettivo, inizio, fk_id_richiesta_soccorso) VALUES (?, ?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS);
            // query per chiudere la missione (aggiunto AND fine IS NULL per evitare modifiche a missioni già chiuse)
            updateMissione = connection.prepareStatement(
                    "UPDATE missione SET livello_successo = ?, fine = ?, fk_id_utente = ? ,commenti = ? WHERE id_missione = ? AND fine IS NULL;");

            selectMissioneById = connection.prepareStatement(
                    "SELECT * FROM Missione WHERE id_missione = ?");

            selectMissioniByMateriale = connection.prepareStatement(
                    "SELECT Missione.* FROM Missione INNER JOIN Impiega_Materiale ON Missione.id_missione = Impiega_Materiale.fk_id_missione WHERE Impiega_Materiale.fk_id_materiale = ?;");

            selectMissioniByMezzo = connection.prepareStatement(
                    "SELECT Missione.* FROM Missione INNER JOIN Impiega_Mezzo ON Missione.id_missione = Impiega_Mezzo.fk_id_missione WHERE Impiega_Mezzo.fk_id_mezzo = ?;");

            selectMissioniChiuseByUtente = connection.prepareStatement(
                    "SELECT * FROM Missione WHERE fk_id_utente = ? AND fine IS NOT NULL;");

            selectMissioniPartecipateByUtente = connection.prepareStatement(
                    "SELECT Missione.* FROM Missione INNER JOIN Squadra ON Missione.id_missione = Squadra.fk_id_missione INNER JOIN Partecipa ON Squadra.id_squadra = Partecipa.fk_id_squadra WHERE Partecipa.fk_id_utente = ?;");

            selectMissioniInCorso = connection.prepareStatement(
                    "SELECT * FROM Missione WHERE inizio <= NOW() AND fine IS NULL;");

            insertImpiegaMateriale = connection.prepareStatement(
                    "INSERT INTO Impiega_Materiale (fk_id_missione, fk_id_materiale) VALUES (?, ?);");

            insertImpiegaMezzo = connection.prepareStatement(
                    "INSERT INTO Impiega_Mezzo (fk_id_missione, fk_id_mezzo) VALUES (?, ?);");

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
            if (insertImpiegaMateriale != null)
                insertImpiegaMateriale.close();
            if (insertImpiegaMezzo != null)
                insertImpiegaMezzo.close();
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

        } catch (SQLException ex) {
            throw new DataException("Unable to create Missione object from ResultSet", ex);
        }
        return m;
    }

    @Override
    public void storeMissione(Missione missione) throws DataException {
        try {
            if (missione.getKey() == null) {
                // CASO 1: NUOVA MISSIONE — data e ora di inizio prese in automatico
                if (missione.getRichiestaSoccorso() == null) {
                    throw new DataException("La missione deve essere collegata a una RichiestaSoccorso");
                }
                java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());

                insertMissione.setString(1, missione.getPosizione());
                insertMissione.setString(2, missione.getObiettivo());
                insertMissione.setTimestamp(3, now); // inizio automatico
                insertMissione.setInt(4, missione.getRichiestaSoccorso().getKey());

                if (insertMissione.executeUpdate() == 1) {
                    try (ResultSet keys = insertMissione.getGeneratedKeys()) {
                        if (keys.next()) {
                            missione.setKey(keys.getInt(1));
                            missione.setInizio(now.toLocalDateTime()); // aggiorna oggetto in memoria
                            dataLayer.getCache().add(Missione.class, missione);
                        }
                    }
                }
            } else {
                // CASO 2: CHIUSURA MISSIONE
                // Query: UPDATE missione SET livello_successo = ?, fine = ?, fk_id_utente = ? ,commenti = ? WHERE id_missione = ?;
                if (missione.getLivelloSuccesso() != null) {
                    updateMissione.setInt(1, missione.getLivelloSuccesso());
                } else {
                    updateMissione.setNull(1, java.sql.Types.TINYINT);
                }

                if (missione.getFine() != null) {
                    updateMissione.setTimestamp(2, java.sql.Timestamp.valueOf(missione.getFine()));
                } else {
                    updateMissione.setNull(2, java.sql.Types.TIMESTAMP);
                }

                if (missione.getAmministratore() != null) {
                    updateMissione.setInt(3, missione.getAmministratore().getKey());
                } else {
                    updateMissione.setNull(3, java.sql.Types.INTEGER);
                }

                updateMissione.setString(4, missione.getCommenti());
                updateMissione.setInt(5, missione.getKey());

                if (updateMissione.executeUpdate() == 0) {
                    throw new DataException("Impossibile aggiornare la missione: la missione non esiste oppure è già stata chiusa.");
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
        throw new UnsupportedOperationException("Unimplemented method 'getMissioniByMateriale'");
    }

    @Override
    public List<Missione> getMissioniByMezzo(Mezzo mezzo) throws DataException {
        throw new UnsupportedOperationException("Unimplemented method 'getMissioniByMezzo'");
    }

    @Override
    public List<Missione> getMissioniChiuseByUtente(Utente utente) throws DataException {
        throw new UnsupportedOperationException("Unimplemented method 'getMissioniChiuseByUtente'");
    }

    @Override
    public List<Missione> getMissioniPartecipateByUtente(Utente utente) throws DataException {
        throw new UnsupportedOperationException("Unimplemented method 'getMissioniPartecipateByUtente'");
    }

    @Override
    public Missione getMissione(int id_missione) throws DataException {
        throw new UnsupportedOperationException("Unimplemented method 'getMissione'");
    }

    @Override
    public List<Missione> getMissioniInCorso() throws DataException {
        throw new UnsupportedOperationException("Unimplemented method 'getMissioniInCorso'");
    }

    @Override
    public void storeImpiegaMateriale(Missione missione, Materiale materiale) throws DataException {
        try {
            insertImpiegaMateriale.setInt(1, missione.getKey());
            insertImpiegaMateriale.setInt(2, materiale.getKey());
            insertImpiegaMateriale.executeUpdate();
        } catch (SQLException ex) {
            throw new DataException("Errore durante l'assegnazione del materiale alla missione", ex);
        }
    }

    @Override
    public void storeImpiegaMezzo(Missione missione, Mezzo mezzo) throws DataException {
        try {
            insertImpiegaMezzo.setInt(1, missione.getKey());
            insertImpiegaMezzo.setInt(2, mezzo.getKey());
            insertImpiegaMezzo.executeUpdate();
        } catch (SQLException ex) {
            throw new DataException("Errore durante l'assegnazione del mezzo alla missione", ex);
        }
    }
}
