package it.univaq.soccorsoweb.data.dao.impl;

import it.univaq.framework.data.DAO;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataLayer;
import it.univaq.soccorsoweb.data.dao.AggiornamentoDAO;
import it.univaq.soccorsoweb.data.model.Aggiornamento;
import it.univaq.soccorsoweb.data.model.Missione;
import it.univaq.soccorsoweb.data.model.Utente;
import it.univaq.soccorsoweb.data.model.impl.proxy.AggiornamentoProxy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AggiornamentoDAO_MySQL extends DAO implements AggiornamentoDAO {

    // andiamo a definire una variabile per ogni query che vogliamo eseguire su Aggiornamento, così che
    // la query viene compilata una sola volta all'avvio del DataLayer e poi riutilizzata tutte le volte che vogliamo
    // questo permette di migliorare le prestazioni
    private PreparedStatement insertAggiornamento;
    private PreparedStatement selectAggiornamentiByMissione;
    private PreparedStatement selectAggiornamentiByUtente;

    // costruttore della classe: riceve in input l'istanza del DataLayer
    public AggiornamentoDAO_MySQL(DataLayer d) {
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
            insertAggiornamento = connection.prepareStatement(
                    "INSERT INTO Aggiornamento (timestamp_agg, testo, fk_id_missione, fk_id_utente) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);

            selectAggiornamentiByMissione = connection.prepareStatement(
                    "SELECT * FROM Aggiornamento WHERE fk_id_missione = ? ORDER BY timestamp_agg DESC");

            selectAggiornamentiByUtente = connection.prepareStatement(
                    "SELECT * FROM Aggiornamento WHERE fk_id_utente = ? ORDER BY timestamp_agg DESC");

        } catch (SQLException ex) {
            throw new DataException("Error initializing aggiornamento data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        // nel momento in cui chiudiamo il DB o l'applicazione, viene liberata tutta la
        // memoria allocata per le variabili contenenti le query precompilate
        try {
            if (insertAggiornamento != null)
                insertAggiornamento.close();
            if (selectAggiornamentiByMissione != null)
                selectAggiornamentiByMissione.close();
            if (selectAggiornamentiByUtente != null)
                selectAggiornamentiByUtente.close();
        } catch (SQLException ex) {
            // ignore
        }
        // va a riprendere l'implementazione del metodo destroy in DAO e la esegue
        super.destroy();
    }

    @Override
    public Aggiornamento createAggiornamento() {
        // tramite il datalayer il proxy ottiene il dao giusto che contiene 
        // la query per restituire le informazioni che gli servono
        return new AggiornamentoProxy(getDataLayer());
    }

    // metodo per creare un Aggiornamento partendo da un risultato del database
    // viene usato dal DAO internamente ogni volta che dobbiamo leggere ed esporre aggiornamenti già esistenti nel database
    // riceve la riga dal db (result set) e restituisce l'oggetto Proxy di quel record
    private AggiornamentoProxy createAggiornamento(ResultSet rs) throws DataException {
        AggiornamentoProxy a = (AggiornamentoProxy) createAggiornamento();
        try {
            a.setKey(rs.getInt("id_aggiornamento"));
            a.setTesto(rs.getString("testo"));
            a.setTimestampAgg(rs.getTimestamp("timestamp_agg").toLocalDateTime());
            a.setMissioneKey(rs.getInt("fk_id_missione"));
            a.setUtenteKey(rs.getInt("fk_id_utente"));
        } catch (SQLException ex) {
            throw new DataException("Unable to create Aggiornamento object from ResultSet", ex);
        }
        return a;
    }

    @Override
    public void storeAggiornamento(Aggiornamento aggiornamento) throws DataException {
        // Un aggiornamento può essere solo inserito, mai modificato
        if (aggiornamento.getKey() != null && aggiornamento.getKey() > 0) {
            throw new DataException("Un aggiornamento non può essere modificato dopo la creazione");
        }
        try {
            // Il timestamp viene preso al momento esatto dell'inserimento
            java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
            insertAggiornamento.setTimestamp(1, now);
            insertAggiornamento.setString(2, aggiornamento.getTesto());
            insertAggiornamento.setInt(3, aggiornamento.getMissione().getKey());
            insertAggiornamento.setInt(4, aggiornamento.getUtente().getKey());

            // rappresenta il numero di righe inserite o modificate all'interno del db,
            // poichè stiamo inserendo un solo oggetto, ci aspettiamo che il numero di
            // righe modificate o inserite sia = 1
            if (insertAggiornamento.executeUpdate() == 1) {
                // per leggere la chiave generata dal database per il record appena inserito, 
                // usiamo il metodo getGeneratedKeys sullo statement.
                try (ResultSet keys = insertAggiornamento.getGeneratedKeys()) {
                    // il valore restituito è un ResultSet (tabella di risultati) con un record
                    // per ciascuna chiave generata (uno solo nel nostro caso)
                    if (keys.next()) {
                        // i campi del record sono le componenti della chiave
                        // va a leggere il nuovo ID generato dal db
                        aggiornamento.setKey(keys.getInt(1));
                        // Aggiorniamo anche il timestamp sull'oggetto in memoria
                        aggiornamento.setTimestampAgg(now.toLocalDateTime());
                        // inseriamo il nuovo oggetto nella cache
                        dataLayer.getCache().add(Aggiornamento.class, aggiornamento);
                    }
                }
            }
            // se abbiamo un proxy, resettiamo il suo attributo dirty
            if (aggiornamento instanceof AggiornamentoProxy) {
                ((AggiornamentoProxy) aggiornamento).setModified(false);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to store Aggiornamento", ex);
        }
    }

    @Override
    public List<Aggiornamento> getAggiornamentiByMissione(Missione missione) throws DataException {
        List<Aggiornamento> result = new ArrayList<>();
        try {
            selectAggiornamentiByMissione.setInt(1, missione.getKey());
            try (ResultSet rs = selectAggiornamentiByMissione.executeQuery()) {
                while (rs.next()) {
                    int id_aggiornamento = rs.getInt("id_aggiornamento");
                    Aggiornamento a = null;
                    if (dataLayer.getCache().has(Aggiornamento.class, id_aggiornamento)) {
                        a = (Aggiornamento) dataLayer.getCache().get(Aggiornamento.class, id_aggiornamento);
                    } else {
                        a = createAggiornamento(rs);
                        dataLayer.getCache().add(Aggiornamento.class, a);
                    }
                    result.add(a);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load aggiornamenti by missione", ex);
        }
        return result;
    }

    @Override // metodo facoltativo non richiesto dalle specifiche
    public List<Aggiornamento> getAggiornamentiByUtente(Utente utente) throws DataException {
        List<Aggiornamento> result = new ArrayList<>();
        try {
            selectAggiornamentiByUtente.setInt(1, utente.getKey());
            try (ResultSet rs = selectAggiornamentiByUtente.executeQuery()) {
                while (rs.next()) {
                    int id_aggiornamento = rs.getInt("id_aggiornamento");
                    Aggiornamento a = null;
                    if (dataLayer.getCache().has(Aggiornamento.class, id_aggiornamento)) {
                        a = (Aggiornamento) dataLayer.getCache().get(Aggiornamento.class, id_aggiornamento);
                    } else {
                        a = createAggiornamento(rs);
                        dataLayer.getCache().add(Aggiornamento.class, a);
                    }
                    result.add(a);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load aggiornamenti by utente", ex);
        }
        return result;
    }
}
