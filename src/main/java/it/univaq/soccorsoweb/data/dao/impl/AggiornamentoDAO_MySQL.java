package it.univaq.soccorsoweb.data.dao.impl;

import it.univaq.framework.data.DAO;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataLayer;
import it.univaq.soccorsoweb.data.dao.AggiornamentoDAO;
import it.univaq.soccorsoweb.data.model.Aggiornamento;
import it.univaq.soccorsoweb.data.model.Missione;
import it.univaq.soccorsoweb.data.model.Utente;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class AggiornamentoDAO_MySQL extends DAO implements AggiornamentoDAO {

    private PreparedStatement insertAggiornamento; // inserimento aggiornamento
    private PreparedStatement selectAggiornamentiByMissione;// tutti gli aggiornamenti di una missione data una missione
    private PreparedStatement selectAggiornamentiByUtente;// tutti gli aggiornamenti di un dato utente (facoltativo)

    public AggiornamentoDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            insertAggiornamento = connection.prepareStatement(
                    "INSERT INTO Aggiornamento (timestamp_agg, testo, fk_id_missione, fk_id_utente) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);

            selectAggiornamentiByMissione = connection.prepareStatement(
                    "SELECT * FROM Aggiornamento WHERE fk_id_missione = ? ORDER BY timestamp_agg DESC");
            // query facoltativa
            selectAggiornamentiByUtente = connection.prepareStatement(
                    "SELECT * FROM Aggiornamento WHERE fk_id_utente = ? ORDER BY timestamp_agg DESC");

        } catch (SQLException ex) {
            throw new DataException("Error initializing aggiornamento data layer", ex);
        }
    }

    @Override
    public Aggiornamento createAggiornamento() {
        throw new UnsupportedOperationException("Unimplemented method 'createAggiornamento'");
    }

    @Override
    public void storeAggiornamento(Aggiornamento aggiornamento) throws DataException {
        throw new UnsupportedOperationException("Unimplemented method 'storeAggiornamento'");
    }

    @Override
    public List<Aggiornamento> getAggiornamentiByMissione(Missione missione) throws DataException {
        throw new UnsupportedOperationException("Unimplemented method 'getAggiornamentiByMissione'");
    }

    @Override
    public List<Aggiornamento> getAggiornamentiByUtente(Utente utente) throws DataException {
        throw new UnsupportedOperationException("Unimplemented method 'getAggiornamentiByUtente'");
    }
}
