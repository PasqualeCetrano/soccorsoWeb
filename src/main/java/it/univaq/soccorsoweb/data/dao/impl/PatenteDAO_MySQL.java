package it.univaq.soccorsoweb.data.dao.impl;

import it.univaq.soccorsoweb.data.dao.PatenteDAO;
import it.univaq.soccorsoweb.data.model.Patente;
import it.univaq.soccorsoweb.data.model.Utente;
import it.univaq.soccorsoweb.data.model.impl.proxy.PatenteProxy;
import it.univaq.framework.data.DAO;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataLayer;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.List;

public class PatenteDAO_MySQL extends DAO implements PatenteDAO {

    // andiamo a definire una variabile per ogni query che vogliamo eseguire su Patente, così che
    // la query viene compilata una sola volta all'avvio del DataLayer e poi riutilizzata tutte le volte che vogliamo
    // questo permette di migliorare le prestazioni
    private PreparedStatement selectPatenteByUtente;
    private PreparedStatement selectPatenti;
    private PreparedStatement insertPatente;
    private PreparedStatement insertPatenteUtente;

    // costruttore della classe: riceve in input l'istanza del DataLayer
    public PatenteDAO_MySQL(DataLayer d) {
        // chiama il costruttore della superclasse (DAO) passandogli l'istanza del DataLayer, 
        // in modo che il padre possa memorizzarla e renderla disponibile a tutti i metodi
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            // precompiliamo tutte le query utilizzate nella classe così da tenerle salvate in 
            // memoria e poi andiamo a sostituire i ? con i valori che gli passeremo
            // connection rappresenta la connessione al db tramite la quale viene eseguita la query 
            selectPatenteByUtente = connection.prepareStatement(
                    "SELECT Patente.* FROM Patente INNER JOIN Detiene ON Patente.id_patente = Detiene.fk_id_patente WHERE Detiene.fk_id_utente = ?;");
            selectPatenti = connection.prepareStatement("SELECT * FROM patente");
            insertPatente = connection.prepareStatement("INSERT INTO Patente (tipo) VALUES (?);",
                    Statement.RETURN_GENERATED_KEYS);
            insertPatenteUtente = connection
                    .prepareStatement("INSERT IGNORE INTO Detiene (fk_id_utente, fk_id_patente) VALUES (?, ?);");

        } catch (SQLException ex) {
            throw new DataException("Error initializing soccorso data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        // nel momento in cui chiudiamo il DB o l'applicazione, viene liberata tutta la
        // memoria allocata per le variabili contenenti le query precompilate
        try {
            selectPatenteByUtente.close();
            selectPatenti.close();
            insertPatente.close();
            if (insertPatenteUtente != null) {
                insertPatenteUtente.close();
            }
        } catch (SQLException ex) {
            // ignore
        }
        // va a riprendere l'implementazione del metodo destroy in DAO e la esegue
        super.destroy();
    }

    @Override
    public Patente createPatente() {
        // tramite il datalayer il proxy ottiene il dao giusto che contiene 
        // la query per restituire le informazioni che gli servono
        return new PatenteProxy(getDataLayer());
    }

    // metodo per creare un Patente partendo da un risultato del database
    // viene usato dal DAO internamente ogni volta che dobbiamo leggere ed esporre patenti già esistenti nel database
    // riceve la riga dal db (result set) e restituisce l'oggetto Proxy di quel record
    private PatenteProxy createPatente(ResultSet rs) throws DataException {
        PatenteProxy p = (PatenteProxy) createPatente();
        try {
            p.setKey(rs.getInt("id_patente"));
            p.setTipo(rs.getString("tipo"));
        } catch (SQLException ex) {
            throw new DataException("Unable to create Patente object from ResultSet", ex);
        }
        return p;
    }

    @Override
    public List<Patente> getPatentiByUtente(Utente utente) throws DataException {
        List<Patente> result = new java.util.ArrayList<>();
        try {
            selectPatenteByUtente.setInt(1, utente.getKey());
            try (ResultSet rs = selectPatenteByUtente.executeQuery()) {
                while (rs.next()) {
                    int id_patente = rs.getInt("id_patente");
                    Patente p = null;
                    // controllo se ha già in memoria un oggetto di tipo Patente con quello specifico 
                    // id, se lo ha prende quell'oggetto e ne fa il cast
                    if (dataLayer.getCache().has(Patente.class, id_patente)) {
                        p = (Patente) dataLayer.getCache().get(Patente.class, id_patente);
                    } else {
                        // altrimenti va a prenderlo dal db (evitando l'N+1 query problem)
                        p = createPatente(rs);
                        // non dimentichiamo anche qui la cache!
                        dataLayer.getCache().add(Patente.class, p);
                    }
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
        try {
            if (patente.getKey() != null && patente.getKey() > 0) { // UPDATE
                // Ignoriamo l'update perché una patente (es. "Patente C") una volta creata non
                // viene modificata
            } else { // INSERT
                insertPatente.setString(1, patente.getTipo());
                // rappresenta il numero di righe inserite o modificate all'interno del db
                if (insertPatente.executeUpdate() == 1) {
                    // per leggere la chiave generata dal database per il record appena inserito, 
                    // usiamo il metodo getGeneratedKeys sullo statement.
                    try (ResultSet keys = insertPatente.getGeneratedKeys()) {
                        // il valore restituito è un ResultSet (tabella di risultati) con un record
                        // per ciascuna chiave generata
                        if (keys.next()) {
                            // i campi del record sono le componenti della chiave
                            // va a leggere il nuovo ID generato dal db
                            int key = keys.getInt(1);
                            // aggiorniamo la chiave in caso di inserimento
                            patente.setKey(key);
                            // inseriamo il nuovo oggetto nella cache
                            dataLayer.getCache().add(Patente.class, patente);
                        }
                    }
                }
            }
            // se abbiamo un proxy, resettiamo il suo attributo dirty
            if (patente instanceof PatenteProxy) {
                ((PatenteProxy) patente).setModified(false);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to store Patente", ex);
        }
    }

    @Override
    public List<Patente> getPatenti() throws DataException {
        List<Patente> result = new java.util.ArrayList<>();
        try (ResultSet rs = selectPatenti.executeQuery()) {
            while (rs.next()) {
                int id_patente = rs.getInt("id_patente");
                Patente p = null;
                // controllo se ha già in memoria un oggetto di tipo Patente
                if (dataLayer.getCache().has(Patente.class, id_patente)) {
                    p = (Patente) dataLayer.getCache().get(Patente.class, id_patente);
                } else {
                    // altrimenti va a prenderlo dal db
                    p = createPatente(rs);
                    dataLayer.getCache().add(Patente.class, p);
                }
                result.add(p);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load all patenti", ex);
        }
        return result;
    }

    @Override // permette di associare la patente a un utente se la patente non esiste a db la
              // inserisce prima, poi aggiorna la tabella detiene
    public void aggiungiPatenteUtente(Utente utente, Patente patente) throws DataException {
        try {
            // Se la patente non è ancora a sistema (non ha un ID), la salviamo prima
            if (patente.getKey() == null) {
                storePatente(patente);
            }

            insertPatenteUtente.setInt(1, utente.getKey());
            insertPatenteUtente.setInt(2, patente.getKey());
            insertPatenteUtente.executeUpdate();
        } catch (SQLException ex) {
            throw new DataException("Unable to add patente to utente", ex);
        }
    }
}
