package it.univaq.soccorsoweb.data.dao.impl;

import it.univaq.framework.data.DAO;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataLayer;
import it.univaq.soccorsoweb.data.dao.UtenteDAO;
import it.univaq.soccorsoweb.data.model.Utente;
import it.univaq.soccorsoweb.data.model.impl.proxy.UtenteProxy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
        return new UtenteProxy(getDataLayer());
    }

    // metodo per creare un utente partendo da un risultato del database
    private UtenteProxy createUtente(ResultSet rs) throws DataException {
        UtenteProxy u = (UtenteProxy) createUtente();
        try {
            u.setKey(rs.getInt("id_utente"));
            u.setNome(rs.getString("nome"));
            u.setCognome(rs.getString("cognome"));
            u.setEmail(rs.getString("email"));
            u.setPassword(rs.getString("password"));
            u.setIndirizzo(rs.getString("indirizzo"));
            u.setCodiceFiscale(rs.getString("codicefiscale"));

            java.sql.Date nascita = rs.getDate("nascita");
            if (nascita != null) {
                u.setDataNascita(nascita.toLocalDate());
            }

            u.setTipo(rs.getString("tipo"));

            int telefono = rs.getInt("telefono");
            if (rs.wasNull()) {
                u.setTelefono(null);
            } else {
                u.setTelefono(telefono);
            }

            int adminId = rs.getInt("id_utente_amministratore");
            if (rs.wasNull()) {
                u.setAmministratoreKey(0);
            } else {
                u.setAmministratoreKey(adminId);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to create Utente object from ResultSet", ex);
        }
        return u;
    }

    @Override // sfurrtta la query per ottenere gli operatori liberi , crea un operatore per
              // ogni risultato del database, poi inserisce questi operatori in una lista
    public List<Utente> getOperatoriDisponibili() throws DataException {
        List<Utente> result = new ArrayList<>();
        try (ResultSet rs = selectOperatoriDisponibili.executeQuery()) {
            while (rs.next()) {
                int id_utente = rs.getInt("id_utente");
                Utente u = null;
                // controllo se ho gia l'utente nella cache
                if (dataLayer.getCache().has(Utente.class, id_utente)) {
                    u = (Utente) dataLayer.getCache().get(Utente.class, id_utente);
                } else {
                    u = createUtente(rs);
                    dataLayer.getCache().add(Utente.class, u);
                }

                result.add(u);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load operatori disponibili", ex);
        }
        return result;
    }

    @Override
    public Utente getUtente(int id_utente) throws DataException {
        // Controlla se l'utente è già nella cache del DataLayer
        if (dataLayer.getCache().has(Utente.class, id_utente)) {
            return (Utente) dataLayer.getCache().get(Utente.class, id_utente);
        }
        try {
            selectUtenteById.setInt(1, id_utente);
            try (ResultSet rs = selectUtenteById.executeQuery()) {
                if (rs.next()) {
                    Utente u = createUtente(rs);
                    dataLayer.getCache().add(Utente.class, u);
                    return u;
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load utente by ID", ex);
        }
        return null;
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
