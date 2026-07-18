package it.univaq.soccorsoweb.data.dao.impl;

import it.univaq.framework.data.DAO;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataItemProxy;
import it.univaq.framework.data.DataLayer;
import it.univaq.soccorsoweb.data.dao.PartecipaDAO;
import it.univaq.soccorsoweb.data.model.Partecipa;
import it.univaq.soccorsoweb.data.model.Squadra;
import it.univaq.soccorsoweb.data.model.impl.proxy.PartecipaProxy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PartecipaDAO_MySQL extends DAO implements PartecipaDAO {

    private PreparedStatement insertPartecipa;
    private PreparedStatement selectPartecipazioniBySquadra;

    public PartecipaDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            insertPartecipa = connection.prepareStatement(
                    "INSERT INTO Partecipa (fk_id_squadra, fk_id_utente, ruolo) VALUES (?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS);
            // serve per ottenere la lista dei componenti di una squadra con il rispettivo
            // ruolo
            selectPartecipazioniBySquadra = connection.prepareStatement(
                    "SELECT * FROM Partecipa WHERE fk_id_squadra = ?;");

        } catch (SQLException ex) {
            throw new DataException("Error initializing Partecipa data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (insertPartecipa != null) {
                insertPartecipa.close();
            }
            if (selectPartecipazioniBySquadra != null) {
                selectPartecipazioniBySquadra.close();
            }
        } catch (SQLException ex) {
            // ignore
        }
        super.destroy();
    }

    @Override
    public Partecipa createPartecipa() {
        return new PartecipaProxy(getDataLayer());
    }

    private PartecipaProxy createPartecipa(ResultSet rs) throws DataException {
        PartecipaProxy p = (PartecipaProxy) createPartecipa();
        try {
            p.setKey(rs.getInt("id_partecipa"));
            p.setSquadraKey(rs.getInt("fk_id_squadra"));
            p.setUtenteKey(rs.getInt("fk_id_utente"));
            p.setRuolo(rs.getString("ruolo"));
        } catch (SQLException ex) {
            throw new DataException("Unable to create Partecipa object from ResultSet", ex);
        }
        return p;
    }

    @Override
    public void storePartecipa(Partecipa partecipa) throws DataException {
        try {
            if (partecipa.getSquadra() == null) {
                throw new DataException("Partecipa deve essere associata a una Squadra");
            }
            if (partecipa.getUtente() == null) {
                throw new DataException("Partecipa deve essere associata a un Utente");
            }
            insertPartecipa.setInt(1, partecipa.getSquadra().getKey());
            insertPartecipa.setInt(2, partecipa.getUtente().getKey());
            insertPartecipa.setString(3, partecipa.getRuolo());

            if (insertPartecipa.executeUpdate() == 1) {
                try (ResultSet keys = insertPartecipa.getGeneratedKeys()) {
                    if (keys.next()) {
                        partecipa.setKey(keys.getInt(1));
                        dataLayer.getCache().add(Partecipa.class, partecipa);
                    }
                }
            }
            if (partecipa instanceof DataItemProxy) {
                ((DataItemProxy) partecipa).setModified(false);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to store Partecipa", ex);
        }
    }

    @Override
    public List<Partecipa> getPartecipazioniBySquadra(Squadra squadra) throws DataException {
        List<Partecipa> result = new ArrayList<>();
        try {
            selectPartecipazioniBySquadra.setInt(1, squadra.getKey());
            try (ResultSet rs = selectPartecipazioniBySquadra.executeQuery()) {
                while (rs.next()) {
                    int id_partecipa = rs.getInt("id_partecipa");
                    Partecipa p = null;
                    if (dataLayer.getCache().has(Partecipa.class, id_partecipa)) {
                        p = (Partecipa) dataLayer.getCache().get(Partecipa.class, id_partecipa);
                    } else {
                        p = createPartecipa(rs);
                        dataLayer.getCache().add(Partecipa.class, p);
                    }
                    result.add(p);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load partecipazioni by squadra", ex);
        }
        return result;
    }
}
