package it.univaq.soccorsoweb.data.dao.impl;

import it.univaq.framework.data.DAO;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataLayer;
import it.univaq.soccorsoweb.data.dao.MezzoDAO;
import it.univaq.soccorsoweb.data.model.Mezzo;
import it.univaq.soccorsoweb.data.model.Missione;
import it.univaq.soccorsoweb.data.model.impl.proxy.MezzoProxy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MezzoDAO_MySQL extends DAO implements MezzoDAO {

    private PreparedStatement selectMezziDisponibili;
    private PreparedStatement selectMezzoById;
    private PreparedStatement insertMezzo;
    private PreparedStatement updateMezzo;
    private PreparedStatement deleteMezzo;
    private PreparedStatement selectAllMezzi;
    private PreparedStatement selectMezziByMissione;

    public MezzoDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            selectMezziDisponibili = connection.prepareStatement(
                    "SELECT m.* FROM Mezzo m WHERE m.id_mezzo NOT IN (SELECT im.fk_id_mezzo FROM Impiega_Mezzo im JOIN Missione mi ON im.fk_id_missione = mi.id_missione WHERE mi.fine IS NULL)");
            selectMezzoById = connection.prepareStatement("SELECT * FROM Mezzo WHERE id_mezzo = ?");
            insertMezzo = connection.prepareStatement("INSERT INTO Mezzo (nome, descrizione) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            updateMezzo = connection.prepareStatement("UPDATE Mezzo SET nome = ?, descrizione = ? WHERE id_mezzo = ?");
            deleteMezzo = connection.prepareStatement("DELETE FROM Mezzo WHERE id_mezzo = ?");
            selectAllMezzi = connection.prepareStatement("SELECT * FROM Mezzo");
            selectMezziByMissione = connection.prepareStatement(
                    "SELECT * FROM Mezzo WHERE id_mezzo IN (SELECT im.fk_id_mezzo FROM Impiega_Mezzo im WHERE im.fk_id_missione = ?)");

        } catch (SQLException ex) {
            throw new DataException("Error initializing mezzo data layer", ex);
        }
    }

    @Override
    public Mezzo createMezzo() {
        return new MezzoProxy(getDataLayer());
    }

    private MezzoProxy createMezzo(ResultSet rs) throws DataException {
        MezzoProxy m = (MezzoProxy) createMezzo();
        try {
            m.setKey(rs.getInt("id_mezzo"));
            m.setNome(rs.getString("nome"));
            m.setDescrizione(rs.getString("descrizione"));
        } catch (SQLException ex) {
            throw new DataException("Unable to create Mezzo object from ResultSet", ex);
        }
        return m;
    }

    @Override
    public List<Mezzo> getMezziDisponibili() throws DataException {
        List<Mezzo> result = new ArrayList<>();
        try (ResultSet rs = selectMezziDisponibili.executeQuery()) {
            while (rs.next()) {
                int id_mezzo = rs.getInt("id_mezzo");
                Mezzo m = null;
                if (dataLayer.getCache().has(Mezzo.class, id_mezzo)) {
                    m = (Mezzo) dataLayer.getCache().get(Mezzo.class, id_mezzo);
                } else {
                    m = createMezzo(rs);
                    dataLayer.getCache().add(Mezzo.class, m);
                }
                result.add(m);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load mezzi disponibili", ex);
        }
        return result;
    }

    @Override
    public Mezzo getMezzo(int id_mezzo) throws DataException {
        if (dataLayer.getCache().has(Mezzo.class, id_mezzo)) {
            return (Mezzo) dataLayer.getCache().get(Mezzo.class, id_mezzo);
        }
        try {
            selectMezzoById.setInt(1, id_mezzo);
            try (ResultSet rs = selectMezzoById.executeQuery()) {
                if (rs.next()) {
                    Mezzo m = createMezzo(rs);
                    dataLayer.getCache().add(Mezzo.class, m);
                    return m;
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load mezzo by ID", ex);
        }
        return null;
    }

    @Override
    public void storeMezzo(Mezzo mezzo) throws DataException {
        try {
            if (mezzo.getKey() == null) { // INSERT
                insertMezzo.setString(1, mezzo.getNome());
                insertMezzo.setString(2, mezzo.getDescrizione());
                if (insertMezzo.executeUpdate() == 1) {
                    try (ResultSet keys = insertMezzo.getGeneratedKeys()) {
                        if (keys.next()) {
                            mezzo.setKey(keys.getInt(1));
                            dataLayer.getCache().add(Mezzo.class, mezzo);
                        }
                    }
                }
            } else { // UPDATE
                updateMezzo.setString(1, mezzo.getNome());
                updateMezzo.setString(2, mezzo.getDescrizione());
                updateMezzo.setInt(3, mezzo.getKey());
                updateMezzo.executeUpdate();
            }
            if (mezzo instanceof MezzoProxy) {
                ((MezzoProxy) mezzo).setModified(false);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to store Mezzo", ex);
        }
    }

    @Override
    public void deleteMezzo(Mezzo mezzo) throws DataException {
        try {
            if (mezzo.getKey() == null) {
                throw new DataException("Impossibile eliminare un mezzo senza ID");
            }
            deleteMezzo.setInt(1, mezzo.getKey());
            deleteMezzo.executeUpdate();
        } catch (SQLException ex) {
            throw new DataException("Unable to delete Mezzo", ex);
        }
    }

    @Override
    public List<Mezzo> getMezzi() throws DataException {
        List<Mezzo> result = new ArrayList<>();
        try (ResultSet rs = selectAllMezzi.executeQuery()) {
            while (rs.next()) {
                int id_mezzo = rs.getInt("id_mezzo");
                Mezzo m = null;
                if (dataLayer.getCache().has(Mezzo.class, id_mezzo)) {
                    m = (Mezzo) dataLayer.getCache().get(Mezzo.class, id_mezzo);
                } else {
                    m = createMezzo(rs);
                    dataLayer.getCache().add(Mezzo.class, m);
                }
                result.add(m);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load all mezzi", ex);
        }
        return result;
    }

    @Override
    public List<Mezzo> getMezziByMissione(Missione missione) throws DataException {
        List<Mezzo> result = new ArrayList<>();
        try {
            selectMezziByMissione.setInt(1, missione.getKey());
            try (ResultSet rs = selectMezziByMissione.executeQuery()) {
                while (rs.next()) {
                    int id_mezzo = rs.getInt("id_mezzo");
                    Mezzo m = null;
                    if (dataLayer.getCache().has(Mezzo.class, id_mezzo)) {
                        m = (Mezzo) dataLayer.getCache().get(Mezzo.class, id_mezzo);
                    } else {
                        m = createMezzo(rs);
                        dataLayer.getCache().add(Mezzo.class, m);
                    }
                    result.add(m);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load mezzi by missione", ex);
        }
        return result;
    }
}
