package it.univaq.soccorsoweb.data.dao.impl;

import it.univaq.framework.data.DAO;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataLayer;
import it.univaq.soccorsoweb.data.dao.UtenteDAO;
import it.univaq.soccorsoweb.data.model.Utente;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class UtenteDAO_MySQL extends DAO implements UtenteDAO {

    private PreparedStatement selectOperatoriDisponibili;
    private PreparedStatement selectUtenteById;
    private PreparedStatement insertUtente; // usata da amministratore
    private PreparedStatement selectOperatori;

    public UtenteDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            selectOperatoriDisponibili = connection.prepareStatement(
                    "SELECT u.* FROM Utente u WHERE u.tipo = 'operatore'   AND u.id_utente NOT IN (SELECT p.fk_id_utente FROM Partecipa p JOIN Squadra s ON p.fk_id_squadra = s.id_squadra JOIN Missione m ON s.fk_id_missione = m.id_missione WHERE m.fine IS NULL)");
            selectUtenteById = connection.prepareStatement("SELECT * FROM Utente WHERE id_utente = ?");
            // usata solo da amministratore
            insertUtente = connection.prepareStatement(
                    "INSERT INTO Utente (indirizzo, tipo, nascita, email, telefono, nome, cognome, codicefiscale, password, id_utente_amministratore) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            selectOperatori = connection.prepareStatement("SELECT * FROM Utente WHERE tipo = 'operatore'");

        } catch (SQLException ex) {
            throw new DataException("Errore durante l'inizializzazione del data layer Utente", ex);
        }
    }

    @Override
    public Utente createUtente() {
        // Implementazione factory per creare un'istanza vuota (o proxy) di Utente
        throw new UnsupportedOperationException("Metodo 'createUtente' non ancora implementato");
    }

    @Override
    public List<Utente> getOperatoriDisponibili() throws DataException {
        throw new UnsupportedOperationException("Metodo 'getOperatoriDisponibili' non ancora implementato");
    }

    @Override
    public Utente getUtente(int id_utente) throws DataException {
        throw new UnsupportedOperationException("Metodo 'getUtente' non ancora implementato");
    }

    @Override
    public void storeUtente(Utente utente) throws DataException {
        throw new UnsupportedOperationException("Metodo 'storeUtente' non ancora implementato");
    }

    @Override
    public List<Utente> getOperatori() throws DataException {
        throw new UnsupportedOperationException("Metodo 'getOperatori' non ancora implementato");
    }
}
