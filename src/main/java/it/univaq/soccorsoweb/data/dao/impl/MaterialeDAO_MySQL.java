package it.univaq.soccorsoweb.data.dao.impl;

import it.univaq.framework.data.DAO;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataLayer;
import it.univaq.framework.data.DataItemProxy;
import it.univaq.soccorsoweb.data.dao.MaterialeDAO;
import it.univaq.soccorsoweb.data.model.Materiale;
import it.univaq.soccorsoweb.data.model.impl.proxy.MaterialeProxy;
import it.univaq.soccorsoweb.data.model.Missione;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MaterialeDAO_MySQL extends DAO implements MaterialeDAO {

    private PreparedStatement selectMaterialiDisponibili;
    private PreparedStatement selectMaterialeById;
    private PreparedStatement insertMateriale;
    private PreparedStatement deleteMateriale;
    private PreparedStatement updateMateriale;
    private PreparedStatement selectAllMateriali;
    private PreparedStatement selectMaterialiByMissione;

    public MaterialeDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            selectMaterialiDisponibili = connection.prepareStatement(
                    "SELECT m.* FROM Materiale m WHERE m.id_materiale NOT IN (SELECT im.fk_id_materiale FROM Impiega_Materiale im JOIN Missione mi ON im.fk_id_missione = mi.id_missione WHERE mi.fine IS NULL)");
            selectMaterialeById = connection.prepareStatement("SELECT * FROM Materiale WHERE id_materiale = ?");
            insertMateriale = connection.prepareStatement("INSERT INTO Materiale (nome, descrizione) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            updateMateriale = connection
                    .prepareStatement("UPDATE Materiale SET nome = ?, descrizione = ? WHERE id_materiale = ?");
            deleteMateriale = connection.prepareStatement("DELETE FROM Materiale WHERE id_materiale = ?");
            selectAllMateriali = connection.prepareStatement("SELECT * FROM Materiale");
            selectMaterialiByMissione = connection.prepareStatement(
                    "SELECT m.* FROM Materiale m JOIN Impiega_Materiale im ON m.id_materiale = im.fk_id_materiale WHERE im.fk_id_missione = ?");

        } catch (SQLException ex) {
            throw new DataException("Error initializing materiale data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            selectMaterialiDisponibili.close();
            selectMaterialeById.close();
            insertMateriale.close();
            updateMateriale.close();
            deleteMateriale.close();
            selectAllMateriali.close();
            selectMaterialiByMissione.close();
        } catch (SQLException ex) {

        }
        super.destroy();
    }

    // l'istanza del getDataLayer la otteniamo da ApplicationDataLayer, quando
    // creiamo il file DAO_MySQL
    @Override
    public Materiale createMateriale() {
        return new MaterialeProxy(getDataLayer());
    }

    private MaterialeProxy createMateriale(ResultSet rs) throws DataException {
        MaterialeProxy m = (MaterialeProxy) createMateriale();
        try {
            m.setKey(rs.getInt("id_materiale"));
            m.setNome(rs.getString("nome"));
            m.setDescrizione(rs.getString("descrizione"));
        } catch (SQLException ex) {
            throw new DataException("Unable to create Materiale object from ResultSet", ex);
        }
        return m;
    }

    @Override
    public List<Materiale> getMaterialiDisponibili() throws DataException {
        List<Materiale> result = new ArrayList<>();
        try (ResultSet rs = selectMaterialiDisponibili.executeQuery()) {
            while (rs.next()) {
                Materiale m = createMateriale(rs);
                dataLayer.getCache().add(Materiale.class, m);
                result.add(m);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load disponibili materiali", ex);
        }
        return result;
    }

    @Override
    public Materiale getMateriale(int id_materiale) throws DataException {
        if (dataLayer.getCache().has(Materiale.class, id_materiale)) {
            return dataLayer.getCache().get(Materiale.class, id_materiale);
        }
        try {
            selectMaterialeById.setInt(1, id_materiale);
            try (ResultSet rs = selectMaterialeById.executeQuery()) {
                if (rs.next()) {
                    Materiale m = createMateriale(rs);
                    dataLayer.getCache().add(Materiale.class, m);
                    return m;
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load materiale by ID", ex);
        }
        return null;
    }

    @Override
    public void storeMateriale(Materiale materiale) throws DataException {
        try {
            if (materiale.getKey() != null && materiale.getKey() > 0) {
                // UPDATE
                if (materiale instanceof DataItemProxy && !((DataItemProxy) materiale).isModified()) {
                    return;
                }
                updateMateriale.setString(1, materiale.getNome());
                updateMateriale.setString(2, materiale.getDescrizione());
                updateMateriale.setInt(3, materiale.getKey());
                updateMateriale.executeUpdate();
            } else {
                // INSERT
                insertMateriale.setString(1, materiale.getNome());
                insertMateriale.setString(2, materiale.getDescrizione());
                if (insertMateriale.executeUpdate() == 1) {
                    try (ResultSet keys = insertMateriale.getGeneratedKeys()) {
                        if (keys.next()) {
                            int key = keys.getInt(1);
                            materiale.setKey(key);
                            dataLayer.getCache().add(Materiale.class, materiale);
                        }
                    }
                }
            }
            if (materiale instanceof DataItemProxy) {
                ((DataItemProxy) materiale).setModified(false);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to store materiale", ex);
        }
    }

    @Override
    public void deleteMateriale(Materiale materiale) throws DataException {
        try {
            deleteMateriale.setInt(1, materiale.getKey());
            deleteMateriale.executeUpdate();
            dataLayer.getCache().delete(Materiale.class, materiale.getKey());
        } catch (SQLException ex) {
            throw new DataException("Unable to delete materiale", ex);
        }
    }

    @Override
    public List<Materiale> getMateriali() throws DataException {
        List<Materiale> result = new ArrayList<>();
        try (ResultSet rs = selectAllMateriali.executeQuery()) {
            while (rs.next()) {
                Materiale m = createMateriale(rs);
                dataLayer.getCache().add(Materiale.class, m);
                result.add(m);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load all materiali", ex);
        }
        return result;
    }

    @Override
    public List<Materiale> getMaterialiByMissione(Missione missione) throws DataException {
        List<Materiale> result = new ArrayList<>();
        try {
            selectMaterialiByMissione.setInt(1, missione.getKey());
            try (ResultSet rs = selectMaterialiByMissione.executeQuery()) {
                while (rs.next()) {
                    Materiale m = createMateriale(rs);
                    dataLayer.getCache().add(Materiale.class, m);
                    result.add(m);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load materiali by missione", ex);
        }
        return result;
    }
}
