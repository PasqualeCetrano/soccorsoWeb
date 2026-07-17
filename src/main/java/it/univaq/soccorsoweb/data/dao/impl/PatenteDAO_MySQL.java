package it.univaq.soccorsoweb.data.dao.impl;

import it.univaq.soccorsoweb.data.dao.PatenteDAO;
import it.univaq.soccorsoweb.data.model.Patente;
import it.univaq.soccorsoweb.data.model.Utente;
import it.univaq.soccorsoweb.data.model.impl.proxy.PatenteProxy;
import it.univaq.framework.data.DAO;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataLayer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.List;

public class PatenteDAO_MySQL extends DAO implements PatenteDAO {

    private PreparedStatement selectPatenteByUtente;
    private PreparedStatement selectPatenti;
    private PreparedStatement insertPatente;
    private PreparedStatement insertPatenteUtente;

    public PatenteDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            selectPatenteByUtente = connection.prepareStatement(
                    "SELECT Patente.* FROM Patente INNER JOIN Detiene ON Patente.id_patente = Detiene.fk_id_patente WHERE Detiene.fk_id_utente = ?;");
            selectPatenti = connection.prepareStatement("SELECT * FROM patente");
            insertPatente = connection.prepareStatement("INSERT INTO Patente (tipo) VALUES (?);",
                    Statement.RETURN_GENERATED_KEYS);
            insertPatenteUtente = connection
                    .prepareStatement("INSERT IGNORE INTO Detiene (fk_id_utente, fk_id_patente) VALUES (?, ?);");

        } catch (SQLException ex) {
            throw new DataException("Error initializing soccorso data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            selectPatenteByUtente.close();
            selectPatenti.close();
            insertPatente.close();
            if (insertPatenteUtente != null) {
                insertPatenteUtente.close();
            }
        } catch (SQLException ex) {
            // ignore
        }
        super.destroy();
    }

    @Override
    public Patente createPatente() {
        return new PatenteProxy(getDataLayer());
    }

    private PatenteProxy createPatente(ResultSet rs) throws DataException {
        PatenteProxy p = (PatenteProxy) createPatente();
        try {
            p.setKey(rs.getInt("id_patente"));
            p.setTipo(rs.getString("tipo"));
        } catch (SQLException ex) {
            throw new DataException("Unable to create Patente object from ResultSet", ex);
        }
        return p;
    }

    @Override
    public List<Patente> getPatentiByUtente(Utente utente) throws DataException {
        List<Patente> result = new java.util.ArrayList<>();
        try {
            selectPatenteByUtente.setInt(1, utente.getKey());
            try (ResultSet rs = selectPatenteByUtente.executeQuery()) {
                while (rs.next()) {
                    int id_patente = rs.getInt("id_patente");
                    Patente p = null;
                    if (dataLayer.getCache().has(Patente.class, id_patente)) {
                        p = (Patente) dataLayer.getCache().get(Patente.class, id_patente);
                    } else {
                        p = createPatente(rs);
                        dataLayer.getCache().add(Patente.class, p);
                    }
                    result.add(p);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load patenti by utente", ex);
        }
        return result;
    }

    @Override
    public void storePatente(Patente patente) throws DataException {
        try {
            if (patente.getKey() != null && patente.getKey() > 0) { // UPDATE
                // Ignoriamo l'update perché una patente (es. "Patente C") una volta creata non
                // viene modificata
            } else { // INSERT
                insertPatente.setString(1, patente.getTipo());
                if (insertPatente.executeUpdate() == 1) {
                    try (ResultSet keys = insertPatente.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            patente.setKey(key);
                            dataLayer.getCache().add(Patente.class, patente);
                        }
                    }
                }
            }
            if (patente instanceof PatenteProxy) {
                ((PatenteProxy) patente).setModified(false);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to store Patente", ex);
        }
    }

    @Override
    public List<Patente> getPatenti() throws DataException {
        List<Patente> result = new java.util.ArrayList<>();
        try (ResultSet rs = selectPatenti.executeQuery()) {
            while (rs.next()) {
                int id_patente = rs.getInt("id_patente");
                Patente p = null;
                if (dataLayer.getCache().has(Patente.class, id_patente)) {
                    p = (Patente) dataLayer.getCache().get(Patente.class, id_patente);
                } else {
                    p = createPatente(rs);
                    dataLayer.getCache().add(Patente.class, p);
                }
                result.add(p);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load all patenti", ex);
        }
        return result;
    }

    @Override // permette di associare la patente a un utente se la patente non esiste a db la
              // inserisce prima poi aggiorna la tabella detiene
    public void aggiungiPatenteUtente(Utente utente, Patente patente) throws DataException {
        try {
            // Se la patente non è ancora a sistema (non ha un ID), la salviamo prima
            if (patente.getKey() == null) {
                storePatente(patente);
            }

            insertPatenteUtente.setInt(1, utente.getKey());
            insertPatenteUtente.setInt(2, patente.getKey());
            insertPatenteUtente.executeUpdate();
        } catch (SQLException ex) {
            throw new DataException("Unable to add patente to utente", ex);
        }
    }
}
