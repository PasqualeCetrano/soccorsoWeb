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

    // andiamo a definire una variabile per ogni query che vogliamo eseguire su
    // Utente, così che
    // la query viene compilata una sola volta all'avvio del DataLayer e poi
    // riutilizzata tutte le volte che vogliamo
    // questo permette di migliorare le prestazioni
    private PreparedStatement selectOperatoriDisponibili;
    private PreparedStatement selectUtenteById;
    private PreparedStatement selectUtenteByEmail;
    private PreparedStatement insertUtente; // usata da amministratore
    private PreparedStatement selectOperatori;
    private PreparedStatement selectUtenti;

    // costruttore della classe: riceve in input l'istanza del DataLayer
    public UtenteDAO_MySQL(DataLayer d) {
        // chiama il costruttore della superclasse (DAO) passandogli l'istanza del
        // DataLayer,
        // in modo che il padre possa memorizzarla e renderla disponibile a tutti i
        // metodi
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            // precompiliamo tutte le query utilizzate nella classe così da tenerle salvate
            // in
            // memoria e poi andiamo a sostituire i ? con i valori che gli passeremo
            selectOperatoriDisponibili = connection.prepareStatement(
                    "SELECT u.* FROM Utente u WHERE u.tipo = 'operatore'   AND u.id_utente NOT IN (SELECT p.fk_id_utente FROM Partecipa p JOIN Squadra s ON p.fk_id_squadra = s.id_squadra JOIN Missione m ON s.fk_id_missione = m.id_missione WHERE m.fine IS NULL)");
            // connection rappresenta la connessione al db tramite la quale viene eseguita
            // la query
            selectUtenteById = connection.prepareStatement("SELECT * FROM Utente WHERE id_utente = ?");
            selectUtenteByEmail = connection.prepareStatement("SELECT * FROM Utente WHERE email = ?");
            // usata solo da amministratore
            // notare l'ultimo parametro extra di questa chiamata a
            // prepareStatement: lo usiamo per assicurarci che il JDBC
            // restituisca la chiave generata automaticamente per il
            // record inserito
            insertUtente = connection.prepareStatement(
                    "INSERT INTO Utente (indirizzo, tipo, nascita, email, telefono, nome, cognome, codicefiscale, password, id_utente_amministratore) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            selectOperatori = connection.prepareStatement("SELECT * FROM Utente WHERE tipo = 'operatore'");
            selectUtenti = connection.prepareStatement("SELECT * FROM Utente");

        } catch (SQLException ex) {
            throw new DataException("Errore durante l'inizializzazione del data layer Utente", ex);
        }
    }

    @Override
    public Utente createUtente() {
        // tramite il datalayer il proxy ottiene il dao giusto che contiene
        // la query per restituire le informazioni che gli servono,
        return new UtenteProxy(getDataLayer());
    }

    // metodo per creare un utente partendo da un risultato del database
    // viene usato dal DAO internamente ogni volta che dobbiamo leggere ed esporre
    // utenti già esistenti nel database
    // riceve la riga dal db (result set) e restituisce l'oggetto Proxy di quel
    // record
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

            u.setTelefono(rs.getString("telefono"));

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
                // controllo se ha già in memoria un oggetto di tipo Utente con quello specifico
                // id, se lo ha prende quell'oggetto e ne fa il cast
                if (dataLayer.getCache().has(Utente.class, id_utente)) {
                    u = (Utente) dataLayer.getCache().get(Utente.class, id_utente);
                } else {
                    // altrimenti va a prenderlo dal db
                    u = createUtente(rs);
                    // non dimentichiamo anche qui la cache!
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
            // 1 serve ad indicare la posizione del ? a cui vogliamo sostituire il valore
            // perché potrebbero essercene piú di uno
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
    public Utente getUtenteByEmail(String email) throws DataException {
        try {
            selectUtenteByEmail.setString(1, email);
            try (ResultSet rs = selectUtenteByEmail.executeQuery()) {
                if (rs.next()) {
                    Utente u = createUtente(rs);
                    // Aggiungiamo alla cache con il suo ID appena estratto
                    // e lo mettiamo anche nella cache
                    dataLayer.getCache().add(Utente.class, u);
                    return u;
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load utente by email", ex);
        }
        return null;
    }

    @Override
    public void storeUtente(Utente utente) throws DataException {
        try {
            if (utente.getKey() != null && utente.getKey() > 0) {
                // Il requisito prevede che storeUtente permetta SOLO l'inserimento
                throw new DataException(
                        "Aggiornamento non supportato: il metodo storeUtente permette solo l'inserimento di nuovi utenti.");
            }

            insertUtente.setString(1, utente.getIndirizzo());
            insertUtente.setString(2, utente.getTipo());
            if (utente.getDataNascita() != null) {
                insertUtente.setDate(3, java.sql.Date.valueOf(utente.getDataNascita()));
            } else {
                insertUtente.setNull(3, java.sql.Types.DATE);
            }
            insertUtente.setString(4, utente.getEmail());
            if (utente.getTelefono() != null) {
                insertUtente.setString(5, utente.getTelefono());
            } else {
                insertUtente.setNull(5, java.sql.Types.VARCHAR);
            }
            insertUtente.setString(6, utente.getNome());
            insertUtente.setString(7, utente.getCognome());
            insertUtente.setString(8, utente.getCodiceFiscale());
            insertUtente.setString(9, utente.getPassword());

            if (utente.getAmministratoreCreatore() != null) { // controllo se ho l amministratore settato
                insertUtente.setInt(10, utente.getAmministratoreCreatore().getKey());
            } else {
                throw new DataException(
                        "Impossibile inserire l'utente: è obbligatorio specificare l'amministratore creatore.");
            }

            // rappresenta il numero di righe inserite o modificate all'interno del db,
            // poichè stiamo inserendo un solo oggetto, ci aspettiamo che il numero di
            // righe modificate o inserite sia = 1
            if (insertUtente.executeUpdate() == 1) {
                // per leggere la chiave generata dal database
                // per il record appena inserito, usiamo il metodo
                // getGeneratedKeys sullo statement.
                try (ResultSet keys = insertUtente.getGeneratedKeys()) {
                    // il valore restituito è un ResultSet (tabella di risultati) con un record
                    // per ciascuna chiave generata (uno solo nel nostro caso)
                    if (keys.next()) {
                        // i campi del record sono le componenti della chiave
                        // (nel nostro caso, un solo intero)
                        // va a leggere il nuovo ID generato dal db
                        int key = keys.getInt(1);
                        // aggiorniamo la chiave in caso di inserimento
                        // facciamo passare l'ID dell'oggetto in memoria da null al valore generato dal
                        // db
                        utente.setKey(key);
                        // inseriamo il nuovo oggetto nella cache
                        dataLayer.getCache().add(Utente.class, utente);
                    }
                }
                // se abbiamo un proxy, resettiamo il suo attributo dirty
                if (utente instanceof UtenteProxy) { // setto modified a false perche l ho appena iserito e quindi l
                                                     // oggetto corrisponde con il record del database
                    ((UtenteProxy) utente).setModified(false);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to store utente", ex);
        }
    }

    @Override
    public List<Utente> getOperatori() throws DataException {
        List<Utente> result = new ArrayList<>();
        try (ResultSet rs = selectOperatori.executeQuery()) {
            while (rs.next()) {
                int id_utente = rs.getInt("id_utente");
                Utente u = null;

                if (dataLayer.getCache().has(Utente.class, id_utente)) {
                    u = (Utente) dataLayer.getCache().get(Utente.class, id_utente);
                } else {
                    // evitiamo l'N+1 query problem!
                    u = createUtente(rs);
                    // non dimentichiamo anche qui la cache!
                    dataLayer.getCache().add(Utente.class, u);
                }

                result.add(u);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load operatori", ex);
        }
        return result;
    }

    @Override
    public List<Utente> getUtenti() throws DataException {
        List<Utente> result = new ArrayList<>();
        try (ResultSet rs = selectUtenti.executeQuery()) {
            while (rs.next()) {
                int id_utente = rs.getInt("id_utente");
                Utente u = null;

                if (dataLayer.getCache().has(Utente.class, id_utente)) {
                    u = (Utente) dataLayer.getCache().get(Utente.class, id_utente);
                } else {
                    // evitiamo l'N+1 query problem!
                    u = createUtente(rs);
                    // non dimentichiamo anche qui la cache!
                    dataLayer.getCache().add(Utente.class, u);
                }

                result.add(u);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load utenti", ex);
        }
        return result;
    }

    @Override // serve per chiudere le query
    public void destroy() throws DataException {
        // nel momento in cui chiudiamo il DB o l'applicazione, viene liberata tutta la
        // memoria allocata per le variabili contenenti le query precompilate
        try {
            if (selectOperatoriDisponibili != null) {
                selectOperatoriDisponibili.close();
            }
            if (selectUtenteById != null) {
                selectUtenteById.close();
            }
            if (selectUtenteByEmail != null) {
                selectUtenteByEmail.close();
            }
            if (insertUtente != null) {
                insertUtente.close();
            }
            if (selectOperatori != null) {
                selectOperatori.close();
            }
            if (selectUtenti != null) {
                selectUtenti.close();
            }
        } catch (SQLException ex) {
            throw new DataException("Errore durante la chiusura delle query nel data layer Utente", ex);
        }
        // va a riprendere l'implementazione del metodo destroy in DAO e la esegue
        super.destroy();
    }
}
