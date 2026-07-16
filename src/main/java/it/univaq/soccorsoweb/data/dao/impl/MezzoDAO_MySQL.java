package it.univaq.soccorsoweb.data.dao.impl;

import it.univaq.framework.data.DAO;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataLayer;
import it.univaq.soccorsoweb.data.dao.MezzoDAO;
import it.univaq.soccorsoweb.data.model.Mezzo;
import it.univaq.soccorsoweb.data.model.Missione;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
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
        throw new UnsupportedOperationException("Unimplemented method 'createMezzo'");
    }

    @Override
    public List<Mezzo> getMezziDisponibili() throws DataException {
        throw new UnsupportedOperationException("Unimplemented method 'getMezziDisponibili'");
    }

    @Override
    public Mezzo getMezzo(int id_mezzo) throws DataException {
        throw new UnsupportedOperationException("Unimplemented method 'getMezzo'");
    }

    @Override
    public void storeMezzo(Mezzo mezzo) throws DataException {
        throw new UnsupportedOperationException("Unimplemented method 'storeMezzo'");
    }

    @Override
    public void deleteMezzo(Mezzo mezzo) throws DataException {
        throw new UnsupportedOperationException("Unimplemented method 'deleteMezzo'");
    }

    @Override
    public List<Mezzo> getMezzi() throws DataException {
        throw new UnsupportedOperationException("Unimplemented method 'getMezzi'");
    }

    @Override
    public List<Mezzo> getMezziByMissione(Missione missione) throws DataException {
        throw new UnsupportedOperationException("Unimplemented method 'getMezziByMissione'");
    }
}
