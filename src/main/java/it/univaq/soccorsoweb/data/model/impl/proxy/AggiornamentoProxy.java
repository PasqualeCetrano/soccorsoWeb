package it.univaq.soccorsoweb.data.model.impl.proxy;

import it.univaq.soccorsoweb.data.dao.MissioneDAO;
import it.univaq.soccorsoweb.data.dao.UtenteDAO;

import it.univaq.soccorsoweb.data.model.Missione;
import it.univaq.soccorsoweb.data.model.Utente;
import it.univaq.soccorsoweb.data.model.impl.AggiornamentoImpl;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataItemProxy;
import it.univaq.framework.data.DataLayer;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * quindi in questa classe del proxy, abbiamo effettuato l'Override dei metodi contenuti in 
 * AggiornamentoImpl e per modificare i valori degli attributi dell'oggetto Aggiornamento salvato in RAM,
 * ci basiamo sul richiamare i metodi presenti in AggiornamentoImpl all'interno dei metodi 
 * ridefiniti nel proxy.
 *
 * richiamiamo i metodi in AggiornamentoImpl perchè poichè quegli attributi sono dichiarati private,
 * possono essere modificati solamente dai metodi di quella classe
 *
 * @author Antigravity
 */
public class AggiornamentoProxy extends AggiornamentoImpl implements DataItemProxy {

    // I FILE PROXY FORNISCONO IL TRACCIAMENTO DELLE MODIFICHE E PERMETTONO IL LAZY LOADING

    // Indica se l'oggetto in memoria contiene delle modifiche non ancora salvate sul database.
    protected boolean modified;
    // in AggiornamentoImpl, noi abbiamo l'attributo missione che rappresenta
    // l'intero
    // oggetto missione, quindi può contenere un intero oggetto missione.
    // mentre nel momento in cui riceviamo i dati dal db per l'aggiornamento, noi
    // invece
    // dell'intero oggetto missione, otteniamo il valore della chiave esterna che
    // andremo a salvare nella nuova variabile missione_key e poi da li saremo in
    // grado
    // di risalire all'intero oggetto missione (permette il lazy_loading)
    protected int missione_key;
    protected int utente_key;
    // Riferimento al DataLayer, necessario per caricare i dati dal database
    // dataLayer è il punto di accesso (connessioni) che permette al Proxy di
    // recuperare i DAO e mantenere la connessione attiva al DB
    // permette di mantenere gli oggetti caricati in memoria
    // necessari per caricare autonomamente i propri dati correlati (Lazy Loading).
    protected DataLayer dataLayer;

    public AggiornamentoProxy(DataLayer d) {
        super();
        this.dataLayer = d;
        this.modified = false;
        this.missione_key = 0;
        this.utente_key = 0;
    }

    @Override
    public void setKey(Integer key) {
        super.setKey(key);
        this.modified = true;
    }

    @Override
    public void setTimestampAgg(LocalDateTime timestampAgg) {
        super.setTimestampAgg(timestampAgg);
        this.modified = true;
    }

    @Override
    public void setTesto(String testo) {
        super.setTesto(testo);
        this.modified = true;
    }

    @Override
    public Missione getMissione() {
        // notare come la Missione in relazione venga caricata solo su richiesta
        if (super.getMissione() == null && missione_key > 0) {
            try {
                super.setMissione(((MissioneDAO) dataLayer.getDAO(Missione.class)).getMissione(missione_key));
            } catch (DataException ex) {
                Logger.getLogger(AggiornamentoProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getMissione();
    }

    @Override
    public void setMissione(Missione missione) {
        super.setMissione(missione);
        if (missione != null) {
            this.missione_key = missione.getKey();
        } else {
            this.missione_key = 0;
        }
        this.modified = true;
    }

    @Override
    public Utente getUtente() {
        // notare come l'Utente in relazione venga caricato solo su richiesta
        if (super.getUtente() == null && utente_key > 0) {
            try {
                super.setUtente(((UtenteDAO) dataLayer.getDAO(Utente.class)).getUtente(utente_key));
            } catch (DataException ex) {
                Logger.getLogger(AggiornamentoProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getUtente();
    }

    @Override
    public void setUtente(Utente utente) {
        super.setUtente(utente);
        if (utente != null) {
            this.utente_key = utente.getKey();
        } else {
            this.utente_key = 0;
        }
        this.modified = true;
    }

    // METODI DEL PROXY
    // dopo che l'oggetto viene salvato nel DB tramite il DAO, la modifica per 
    // questo oggetto viene reimpostata a false
    @Override
    public void setModified(boolean dirty) {
        this.modified = dirty;
    }

    // Chiesto dal DAO per sapere: "C'è qualcosa da salvare su DB?"
    @Override
    public boolean isModified() {
        return modified;
    }

    public void setMissioneKey(int missione_key) {
        this.missione_key = missione_key;
        super.setMissione(null);
    }

    public void setUtenteKey(int utente_key) {
        this.utente_key = utente_key;
        super.setUtente(null);
    }
}
