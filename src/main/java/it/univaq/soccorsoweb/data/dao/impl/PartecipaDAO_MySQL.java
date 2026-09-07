package it.univaq.soccorsoweb.data.dao.impl;

import it.univaq.framework.data.DAO;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataItemProxy;
import it.univaq.framework.data.DataLayer;
import it.univaq.soccorsoweb.data.dao.PartecipaDAO;
import it.univaq.soccorsoweb.data.model.Partecipa;
import it.univaq.soccorsoweb.data.model.Squadra;
import it.univaq.soccorsoweb.data.model.impl.proxy.PartecipaProxy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PartecipaDAO_MySQL extends DAO implements PartecipaDAO {

    // andiamo a definire una variabile per ogni query che vogliamo eseguire su Partecipa, così che
    // la query viene compilata una sola volta all'avvio del DataLayer e poi riutilizzata tutte le volte che vogliamo
    // questo permette di migliorare le prestazioni
    private PreparedStatement insertPartecipa;
    private PreparedStatement selectPartecipazioniBySquadra;

    // costruttore della classe: riceve in input l'istanza del DataLayer
    public PartecipaDAO_MySQL(DataLayer d) {
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
            insertPartecipa = connection.prepareStatement(
                    "INSERT INTO Partecipa (fk_id_squadra, fk_id_utente, ruolo) VALUES (?, ?, ?);",
                    Statement.RETURN_GENERATED_KEYS);
            // serve per ottenere la lista dei componenti di una squadra con il rispettivo
            // ruolo
            selectPartecipazioniBySquadra = connection.prepareStatement(
                    "SELECT * FROM Partecipa WHERE fk_id_squadra = ?;");

        } catch (SQLException ex) {
            throw new DataException("Error initializing Partecipa data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        // nel momento in cui chiudiamo il DB o l'applicazione, viene liberata tutta la
        // memoria allocata per le variabili contenenti le query precompilate
        try {
            if (insertPartecipa != null) {
                insertPartecipa.close();
            }
            if (selectPartecipazioniBySquadra != null) {
                selectPartecipazioniBySquadra.close();
            }
        } catch (SQLException ex) {
            // ignore
        }
        // va a riprendere l'implementazione del metodo destroy in DAO e la esegue
        super.destroy();
    }

    @Override
    public Partecipa createPartecipa() {
        // tramite il datalayer il proxy ottiene il dao giusto che contiene 
        // la query per restituire le informazioni che gli servono
        return new PartecipaProxy(getDataLayer());
    }

    // metodo per creare un Partecipa partendo da un risultato del database
    // viene usato dal DAO internamente ogni volta che dobbiamo leggere ed esporre partecipazioni già esistenti nel database
    // riceve la riga dal db (result set) e restituisce l'oggetto Proxy di quel record
    private PartecipaProxy createPartecipa(ResultSet rs) throws DataException {
        PartecipaProxy p = (PartecipaProxy) createPartecipa();
        try {
            p.setKey(rs.getInt("id_partecipa"));
            p.setSquadraKey(rs.getInt("fk_id_squadra"));
            p.setUtenteKey(rs.getInt("fk_id_utente"));
            p.setRuolo(rs.getString("ruolo"));
        } catch (SQLException ex) {
            throw new DataException("Unable to create Partecipa object from ResultSet", ex);
        }
        return p;
    }

    @Override
    public void storePartecipa(Partecipa partecipa) throws DataException {
        try {
            if (partecipa.getSquadra() == null) {
                throw new DataException("Partecipa deve essere associata a una Squadra");
            }
            if (partecipa.getUtente() == null) {
                throw new DataException("Partecipa deve essere associata a un Utente");
            }
            insertPartecipa.setInt(1, partecipa.getSquadra().getKey());
            insertPartecipa.setInt(2, partecipa.getUtente().getKey());
            insertPartecipa.setString(3, partecipa.getRuolo());

            // rappresenta il numero di righe inserite o modificate all'interno del db
            if (insertPartecipa.executeUpdate() == 1) {
                // per leggere la chiave generata dal database per il record appena inserito, 
                // usiamo il metodo getGeneratedKeys sullo statement.
                try (ResultSet keys = insertPartecipa.getGeneratedKeys()) {
                    // il valore restituito è un ResultSet (tabella di risultati) con un record
                    // per ciascuna chiave generata
                    if (keys.next()) {
                        // i campi del record sono le componenti della chiave
                        // va a leggere il nuovo ID generato dal db
                        partecipa.setKey(keys.getInt(1));
                        // inseriamo il nuovo oggetto nella cache
                        dataLayer.getCache().add(Partecipa.class, partecipa);
                    }
                }
            }
            // se abbiamo un proxy, resettiamo il suo attributo dirty
            if (partecipa instanceof DataItemProxy) {
                ((DataItemProxy) partecipa).setModified(false);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to store Partecipa", ex);
        }
    }

    @Override
    public List<Partecipa> getPartecipazioniBySquadra(Squadra squadra) throws DataException {
        List<Partecipa> result = new ArrayList<>();
        try {
            selectPartecipazioniBySquadra.setInt(1, squadra.getKey());
            try (ResultSet rs = selectPartecipazioniBySquadra.executeQuery()) {
                while (rs.next()) {
                    int id_partecipa = rs.getInt("id_partecipa");
                    Partecipa p = null;
                    // controllo se ha già in memoria un oggetto di tipo Partecipa con quello specifico 
                    // id, se lo ha prende quell'oggetto e ne fa il cast
                    if (dataLayer.getCache().has(Partecipa.class, id_partecipa)) {
                        p = (Partecipa) dataLayer.getCache().get(Partecipa.class, id_partecipa);
                    } else {
                        // altrimenti va a prenderlo dal db (evitando l'N+1 query problem)
                        p = createPartecipa(rs);
                        // non dimentichiamo anche qui la cache!
                        dataLayer.getCache().add(Partecipa.class, p);
                    }
                    result.add(p);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load partecipazioni by squadra", ex);
        }
        return result;
    }
}
