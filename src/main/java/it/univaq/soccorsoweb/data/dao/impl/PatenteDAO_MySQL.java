package it.univaq.soccorsoweb.data.dao.impl;

import it.univaq.soccorsoweb.data.dao.PatenteDAO;
import it.univaq.soccorsoweb.data.model.Patente;
import it.univaq.soccorsoweb.data.model.Utente;
import it.univaq.soccorsoweb.data.model.impl.proxy.PatenteProxy;
import it.univaq.framework.data.DAO;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataLayer;
import it.univaq.framework.data.DataItemProxy;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;



public class PatenteDAO_MySQL extends DAO implements PatenteDAO {

    private PreparedStatement selectPatenteByUtente;
    private PreparedStatement selectPatenti;
    private PreparedStatement insertPatente;

    public PatenteDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            selectPatenteByUtente = connection.prepareStatement("SELECT Patente.* FROM Patente INNER JOIN Detiene ON Patente.id_patente = Detiene.fk_id_patente WHERE Detiene.fk_id_utente = ?;");
            selectPatenti = connection.prepareStatement("SELECT * FROM patente");
            insertPatente = connection.prepareStatement("INSERT INTO Patente (codice_patente, tipo) VALUES (?, ?);", Statement.RETURN_GENERATED_KEYS);
            
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
            p.setKey(rs.getInt("ID"));
            p.setTipo(rs.getString("tipo"));
            p.setVersion(rs.getLong("version"));
        } catch (SQLException ex) {
            throw new DataException("Unable to create Patente object from ResultSet", ex);
        }
        return p;
    }

    @Override
    public List<Patente> getPatentiByUtente(Utente utente) throws DataException {
        List<Patente> result = new ArrayList<>();
        try {
            selectPatenteByUtente.setInt(1, utente.getKey());
            try (ResultSet rs = selectPatenteByUtente.executeQuery()) {
                while (rs.next()) {
                    Patente p = createPatente(rs);
                    dataLayer.getCache().add(Patente.class, p);
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

    }

    @Override
    public List<Patente> getPatenti() throws DataException {
        List<Patente> result = new ArrayList<>();
        try (ResultSet rs = selectPatenti.executeQuery()) {
            while (rs.next()) {
                Patente p = createPatente(rs);
                dataLayer.getCache().add(Patente.class, p);
                result.add(p);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load all patenti", ex);
        }
        return result;
    }
}

