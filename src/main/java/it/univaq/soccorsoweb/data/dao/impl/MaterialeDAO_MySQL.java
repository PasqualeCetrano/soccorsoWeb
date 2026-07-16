package it.univaq.soccorsoweb.data.dao.impl;

import it.univaq.framework.data.DAO;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataLayer;
import it.univaq.soccorsoweb.data.dao.MaterialeDAO;
import it.univaq.soccorsoweb.data.model.Materiale;
import it.univaq.soccorsoweb.data.model.Missione;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
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
    public Materiale createMateriale() {
        throw new UnsupportedOperationException("Unimplemented method 'createMateriale'");
    }

    @Override
    public List<Materiale> getMaterialiDisponibili() throws DataException {
        throw new UnsupportedOperationException("Unimplemented method 'getMaterialiDisponibili'");
    }

    @Override
    public Materiale getMateriale(int id_materiale) throws DataException {
        throw new UnsupportedOperationException("Unimplemented method 'getMateriale'");
    }

    @Override
    public void storeMateriale(Materiale materiale) throws DataException {
        throw new UnsupportedOperationException("Unimplemented method 'storeMateriale'");
    }

    @Override
    public void deleteMateriale(Materiale materiale) throws DataException {
        throw new UnsupportedOperationException("Unimplemented method 'deleteMateriale'");
    }

    @Override
    public List<Materiale> getMateriali() throws DataException {
        throw new UnsupportedOperationException("Unimplemented method 'getMateriali'");
    }

    @Override
    public List<Materiale> getMaterialiByMissione(Missione missione) throws DataException {
        throw new UnsupportedOperationException("Unimplemented method 'getMaterialiByMissione'");
    }
}
