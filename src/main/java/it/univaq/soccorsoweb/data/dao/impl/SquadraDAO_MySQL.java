package it.univaq.soccorsoweb.data.dao.impl;

import it.univaq.framework.data.DAO;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataLayer;
import it.univaq.framework.data.DataItemProxy;
import it.univaq.soccorsoweb.data.dao.PartecipaDAO;
import it.univaq.soccorsoweb.data.dao.SquadraDAO;
import it.univaq.soccorsoweb.data.model.Partecipa;
import it.univaq.soccorsoweb.data.model.Squadra;
import it.univaq.soccorsoweb.data.model.impl.proxy.SquadraProxy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SquadraDAO_MySQL extends DAO implements SquadraDAO {

    private PreparedStatement insertSquadra;
    private PreparedStatement updateSquadra;
    private PreparedStatement selectSquadraById;

    public SquadraDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            insertSquadra = connection.prepareStatement(
                    "INSERT INTO Squadra (codice, fk_id_missione) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);

            updateSquadra = connection.prepareStatement(
                    "UPDATE Squadra SET codice = ?, fk_id_missione = ? WHERE id_squadra = ?");

            selectSquadraById = connection.prepareStatement(
                    "SELECT * FROM Squadra WHERE id_squadra = ?");

        } catch (SQLException ex) {
            throw new DataException("Error initializing Squadra data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (insertSquadra != null) insertSquadra.close();
            if (updateSquadra != null) updateSquadra.close();
            if (selectSquadraById != null) selectSquadraById.close();
        } catch (SQLException ex) {
            // ignore
        }
        super.destroy();
    }

    @Override
    public Squadra createSquadra() {
        return new SquadraProxy(getDataLayer());
    }

    private SquadraProxy createSquadra(ResultSet rs) throws DataException {
        SquadraProxy s = (SquadraProxy) createSquadra();
        try {
            s.setKey(rs.getInt("id_squadra"));
            s.setCodice(rs.getString("codice"));
            s.setMissioneKey(rs.getInt("fk_id_missione"));
        } catch (SQLException ex) {
            throw new DataException("Unable to create Squadra object from ResultSet", ex);
        }
        return s;
    }

    @Override
    public void storeSquadra(Squadra squadra) throws DataException {
        try {
            if (squadra.getKey() != null && squadra.getKey() > 0) {
                // UPDATE
                updateSquadra.setString(1, squadra.getCodice());
                if (squadra.getMissione() != null) {
                    updateSquadra.setInt(2, squadra.getMissione().getKey());
                } else {
                    throw new DataException("Squadra must be associated with a Missione");
                }
                updateSquadra.setInt(3, squadra.getKey());
                updateSquadra.executeUpdate();
            } else {
                // INSERT
                insertSquadra.setString(1, squadra.getCodice());
                if (squadra.getMissione() != null) {
                    insertSquadra.setInt(2, squadra.getMissione().getKey());
                } else {
                    throw new DataException("Squadra must be associated with a Missione");
                }

                if (insertSquadra.executeUpdate() == 1) {
                    try (ResultSet keys = insertSquadra.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            squadra.setKey(key);
                            // Add to cache
                            dataLayer.getCache().add(Squadra.class, squadra);
                        }
                    }
                }
            }

            // Delegare il salvataggio o la sincronizzazione di Partecipa
            // PartecipaDAO gestirà i suoi record in autonomia quando richiesto
            // O, se vogliamo che Squadra continui a "sincronizzare" i suoi figli,
            // possiamo usare il PartecipaDAO qui.
            if (squadra.getPartecipazioni() != null) {
                PartecipaDAO partecipaDAO = (PartecipaDAO) dataLayer.getDAO(Partecipa.class);
                if (partecipaDAO != null) {
                    for (Partecipa p : squadra.getPartecipazioni()) {
                        p.setSquadra(squadra); // assicuriamoci che il link ci sia
                        partecipaDAO.storePartecipa(p);
                    }
                }
            }

            if (squadra instanceof DataItemProxy) {
                ((DataItemProxy) squadra).setModified(false);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to store Squadra", ex);
        }
    }

    @Override
    public Squadra getSquadra(int id_squadra) throws DataException {
        try {
            selectSquadraById.setInt(1, id_squadra);
            try (ResultSet rs = selectSquadraById.executeQuery()) {
                if (rs.next()) {
                    Squadra s = createSquadra(rs);
                    dataLayer.getCache().add(Squadra.class, s);
                    return s;
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load squadra by ID", ex);
        }
        return null;
    }
}
