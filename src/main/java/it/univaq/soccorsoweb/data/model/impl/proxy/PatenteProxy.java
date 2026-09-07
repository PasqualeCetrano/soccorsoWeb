package it.univaq.soccorsoweb.data.model.impl.proxy;

import it.univaq.soccorsoweb.data.model.impl.PatenteImpl;
import it.univaq.framework.data.DataItemProxy;
import it.univaq.framework.data.DataLayer;

/**
 * quindi in questa classe del proxy, abbiamo effettuato l'Override dei metodi contenuti in 
 * PatenteImpl e per modificare i valori degli attributi dell'oggetto Patente salvato in RAM,
 * ci basiamo sul richiamare i metodi presenti in PatenteImpl all'interno dei metodi 
 * ridefiniti nel proxy.
 *
 * richiamiamo i metodi in PatenteImpl perchè poichè quegli attributi sono dichiarati private,
 * possono essere modificati solamente dai metodi di quella classe
 *
 * @author Antigravity
 */
public class PatenteProxy extends PatenteImpl implements DataItemProxy {

    // I FILE PROXY FORNISCONO IL TRACCIAMENTO DELLE MODIFICHE E PERMETTONO IL LAZY LOADING

    // Indica se l'oggetto in memoria contiene delle modifiche non ancora salvate sul database.
    protected boolean modified;
    // Riferimento al DataLayer, necessario per caricare i dati dal database
    // dataLayer è il punto di accesso (connessioni) che permette al Proxy di
    // recuperare i DAO e mantenere la connessione attiva al DB
    // permette di mantenere gli oggetti caricati in memoria
    // necessari per caricare autonomamente i propri dati correlati (Lazy Loading).
    protected DataLayer dataLayer;

    public PatenteProxy(DataLayer d) {
        super();
        this.dataLayer = d;
        this.modified = false;
    }

    @Override
    public void setKey(Integer key) {
        super.setKey(key);
        this.modified = true;
    }

    @Override
    public void setTipo(String tipo) {
        super.setTipo(tipo);
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

}
