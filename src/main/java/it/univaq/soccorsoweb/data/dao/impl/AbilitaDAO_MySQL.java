package it.univaq.soccorsoweb.data.dao.impl;

import it.univaq.framework.data.DAO;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataLayer;
import it.univaq.soccorsoweb.data.dao.AbilitaDAO;
import it.univaq.soccorsoweb.data.model.Abilita;
import it.univaq.soccorsoweb.data.model.Utente;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class AbilitaDAO_MySQL extends DAO implements AbilitaDAO {

    private PreparedStatement selectAbilitaByUtente;
    private PreparedStatement insertAbilita;
    private PreparedStatement selectAbilita;

    @Override
    public void init() throws DataException {
        try {
            super.init();

            selectAbilitaByUtente = connection.prepareStatement(
                    "SELECT a.* FROM Abilita a JOIN Possiede p ON a.id_abilita = p.fk_id_abilita WHERE p.fk_id_utente = ?");
            selectAbilita = connection.prepareStatement("SELECT * FROM Abilita");
            insertAbilita = connection.prepareStatement("INSERT INTO Abilita (descrizione) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS);

        } catch (SQLException ex) {
            throw new DataException("Error initializing newspaper data layer", ex);
        }
    }

    public AbilitaDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public Abilita createAbilita() {

        throw new UnsupportedOperationException("Unimplemented method 'createAbilita'");
    }

    @Override
    public List<Abilita> getAbilitaByUtente(Utente utente) throws DataException {

        throw new UnsupportedOperationException("Unimplemented method 'getAbilitaByUtente'");
    }

    @Override
    public void storeAbilita(Abilita abilita) throws DataException {

        throw new UnsupportedOperationException("Unimplemented method 'storeAbilita'");
    }

    @Override
    public List<Abilita> getAbilita() throws DataException {

        throw new UnsupportedOperationException("Unimplemented method 'getAbilita'");
    }
    // implementate query per le abilita
}