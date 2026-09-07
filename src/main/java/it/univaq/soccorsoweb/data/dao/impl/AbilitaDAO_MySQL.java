package it.univaq.soccorsoweb.data.dao.impl;

import it.univaq.framework.data.DAO;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataLayer;
import it.univaq.soccorsoweb.data.dao.AbilitaDAO;
import it.univaq.soccorsoweb.data.model.Abilita;
import it.univaq.soccorsoweb.data.model.Utente;
import it.univaq.soccorsoweb.data.model.impl.proxy.AbilitaProxy;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AbilitaDAO_MySQL extends DAO implements AbilitaDAO {

    // andiamo a definire una variabile per ogni query che vogliamo eseguire su Abilita, così che
    // la query viene compilata una sola volta all'avvio del DataLayer e poi riutilizzata tutte le volte che vogliamo
    // questo permette di migliorare le prestazioni
    private PreparedStatement selectAbilitaByUtente;
    private PreparedStatement insertAbilita;
    private PreparedStatement selectAbilita;
    private PreparedStatement insertAbilitaUtente;

    @Override
    public void init() throws DataException {
        try {
            super.init();

            // precompiliamo tutte le query utilizzate nella classe così da tenerle salvate in 
            // memoria e poi andiamo a sostituire i ? con i valori che gli passeremo
            // connection rappresenta la connessione al db tramite la quale viene eseguita la query 
            selectAbilitaByUtente = connection.prepareStatement(
                    "SELECT a.* FROM Abilita a JOIN Possiede p ON a.id_abilita = p.fk_id_abilita WHERE p.fk_id_utente = ?");
            selectAbilita = connection.prepareStatement("SELECT * FROM Abilita");
            insertAbilita = connection.prepareStatement("INSERT INTO Abilita (descrizione) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS);
            insertAbilitaUtente = connection
                    .prepareStatement("INSERT IGNORE INTO Possiede (fk_id_utente, fk_id_abilita) VALUES (?, ?)");

        } catch (SQLException ex) {
            throw new DataException("Error initializing newspaper data layer", ex);
        }
    }

    // costruttore della classe: riceve in input l'istanza del DataLayer
    public AbilitaDAO_MySQL(DataLayer d) {
        // chiama il costruttore della superclasse (DAO) passandogli l'istanza del DataLayer, 
        // in modo che il padre possa memorizzarla e renderla disponibile a tutti i metodi
        super(d);
    }

    @Override
    public Abilita createAbilita() {
        // tramite il datalayer il proxy ottiene il dao giusto che contiene 
        // la query per restituire le informazioni che gli servono
        return new AbilitaProxy(getDataLayer());
    }

    // metodo per creare un Abilita partendo da un risultato del database
    // viene usato dal DAO internamente ogni volta che dobbiamo leggere ed esporre abilità già esistenti nel database
    // riceve la riga dal db (result set) e restituisce l'oggetto Proxy di quel record
    private AbilitaProxy createAbilita(ResultSet rs) throws DataException {
        AbilitaProxy a = (AbilitaProxy) createAbilita();
        try {
            a.setKey(rs.getInt("id_abilita"));
            a.setDescrizione(rs.getString("descrizione"));
        } catch (SQLException ex) {
            throw new DataException("Unable to create Abilita object from ResultSet", ex);
        }
        return a;
    }

    @Override
    public List<Abilita> getAbilitaByUtente(Utente utente) throws DataException {
        List<Abilita> result = new java.util.ArrayList<>();
        try {
            selectAbilitaByUtente.setInt(1, utente.getKey());
            try (ResultSet rs = selectAbilitaByUtente.executeQuery()) {
                while (rs.next()) {
                    int id_abilita = rs.getInt("id_abilita");
                    Abilita a = null;
                    // controllo se ha già in memoria un oggetto di tipo Abilita con quello specifico 
                    // id, se lo ha prende quell'oggetto e ne fa il cast
                    if (dataLayer.getCache().has(Abilita.class, id_abilita)) {
                        a = (Abilita) dataLayer.getCache().get(Abilita.class, id_abilita);
                    } else {
                        // altrimenti va a prenderlo dal db (evitando l'N+1 query problem)
                        a = createAbilita(rs);
                        // non dimentichiamo anche qui la cache!
                        dataLayer.getCache().add(Abilita.class, a);
                    }
                    result.add(a);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load abilita by utente", ex);
        }
        return result;
    }

    @Override
    public void storeAbilita(Abilita abilita) throws DataException {
        try {
            if (abilita.getKey() != null && abilita.getKey() > 0) {
                // Come per l'utente, gestiamo solo l'inserimento
                throw new DataException(
                        "Aggiornamento non supportato: il metodo storeAbilita permette solo l'inserimento di nuove abilità.");
            }

            insertAbilita.setString(1, abilita.getDescrizione());

            // rappresenta il numero di righe inserite o modificate all'interno del db,
            // poichè stiamo inserendo un solo oggetto, ci aspettiamo che il numero di
            // righe modificate o inserite sia = 1
            if (insertAbilita.executeUpdate() == 1) {
                // per leggere la chiave generata dal database per il record appena inserito, 
                // usiamo il metodo getGeneratedKeys sullo statement.
                try (ResultSet keys = insertAbilita.getGeneratedKeys()) {
                    // il valore restituito è un ResultSet (tabella di risultati) con un record
                    // per ciascuna chiave generata (uno solo nel nostro caso)
                    if (keys.next()) {
                        // i campi del record sono le componenti della chiave
                        // va a leggere il nuovo ID generato dal db
                        int key = keys.getInt(1);
                        // aggiorniamo la chiave in caso di inserimento (da null al valore generato)
                        abilita.setKey(key);
                        // inseriamo il nuovo oggetto nella cache
                        dataLayer.getCache().add(Abilita.class, abilita);
                    }
                }
                // se abbiamo un proxy, resettiamo il suo attributo dirty
                if (abilita instanceof AbilitaProxy) {
                    ((AbilitaProxy) abilita).setModified(false);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to store abilita", ex);
        }
    }

    @Override // restitutice tutte le abilita presenti nel db
    public List<Abilita> getAbilita() throws DataException {
        List<Abilita> result = new java.util.ArrayList<>();
        try (ResultSet rs = selectAbilita.executeQuery()) {
            while (rs.next()) {
                int id_abilita = rs.getInt("id_abilita");
                Abilita a = null;

                // controllo se ha già in memoria un oggetto di tipo Abilita
                if (dataLayer.getCache().has(Abilita.class, id_abilita)) {
                    a = (Abilita) dataLayer.getCache().get(Abilita.class, id_abilita);
                } else {
                    // altrimenti va a prenderlo dal db
                    a = createAbilita(rs);
                    dataLayer.getCache().add(Abilita.class, a);
                }
                result.add(a);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load abilita", ex);
        }
        return result;
    }

    @Override // permette all untente di aggiungere un abilita al suo profilo anche non
              // esistente(se non esiste la salva prima nella tabella e poi nell'associazione)
    public void aggiungiAbilitaUtente(Utente utente, Abilita abilita) throws DataException {
        try {
            // Se l'abilità è nuova e non ha ancora un ID, la salviamo prima nel DB
            if (abilita.getKey() == null) {
                storeAbilita(abilita);
            }

            insertAbilitaUtente.setInt(1, utente.getKey());
            insertAbilitaUtente.setInt(2, abilita.getKey());
            insertAbilitaUtente.executeUpdate();

        } catch (SQLException ex) {
            throw new DataException("Unable to add abilita to utente", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        // nel momento in cui chiudiamo il DB o l'applicazione, viene liberata tutta la
        // memoria allocata per le variabili contenenti le query precompilate
        try {
            if (selectAbilitaByUtente != null) {
                selectAbilitaByUtente.close();
            }
            if (insertAbilita != null) {
                insertAbilita.close();
            }
            if (selectAbilita != null) {
                selectAbilita.close();
            }
            if (insertAbilitaUtente != null) {
                insertAbilitaUtente.close();
            }
        } catch (SQLException ex) {
            throw new DataException("Errore durante la chiusura delle query nel data layer Abilita", ex);
        }
        // va a riprendere l'implementazione del metodo destroy in DAO e la esegue
        super.destroy();
    }
}