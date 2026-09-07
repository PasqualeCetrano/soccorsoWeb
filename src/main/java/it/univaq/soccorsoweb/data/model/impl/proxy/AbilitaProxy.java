package it.univaq.soccorsoweb.data.model.impl.proxy;

import it.univaq.soccorsoweb.data.model.impl.AbilitaImpl;
import it.univaq.framework.data.DataItemProxy;
import it.univaq.framework.data.DataLayer;

/**
 * quindi in questa classe del proxy, abbiamo effettuato l'Override dei metodi contenuti in 
 * AbilitaImpl e per modificare i valori degli attributi dell'oggetto Abilita salvato in RAM,
 * ci basiamo sul richiamare i metodi presenti in AbilitaImpl all'interno dei metodi 
 * ridefiniti nel proxy.
 *
 * richiamiamo i metodi in AbilitaImpl perchè poichè quegli attributi sono dichiarati private,
 * possono essere modificati solamente dai metodi di quella classe
 *
 * @author Antigravity
 */
public class AbilitaProxy extends AbilitaImpl implements DataItemProxy {

    // I FILE PROXY FORNISCONO IL TRACCIAMENTO DELLE MODIFICHE E PERMETTONO IL LAZY LOADING

    // Indica se l'oggetto in memoria contiene delle modifiche non ancora salvate sul database.
    protected boolean modified;
    // Riferimento al DataLayer, necessario per caricare i dati dal database
    // dataLayer è il punto di accesso (connessioni) che permette al Proxy di
    // recuperare i DAO e mantenere la connessione attiva al DB
    // permette di mantenere gli oggetti caricati in memoria
    // necessari per caricare autonomamente i propri dati correlati (Lazy Loading).
    protected DataLayer dataLayer;

    public AbilitaProxy(DataLayer d) {
        super();
        this.dataLayer = d;
        this.modified = false;
    }

    @Override
    public void setKey(Integer key) {
        // viene eseguito il metodo setKey in AbilitaImpl, poichè non contiene tale
        // metodo
        // viene eseguito il metodo setKey della superclasse DataItemImpl che imposta la
        // variabile key
        // inizialmente le chiavi degli oggetti Java sono null, per poi essere
        // modificate tramite questo metodo in quando l'ID viene generato
        // automaticamente dal DB
        super.setKey(key);
        this.modified = true;
    }

    @Override
    public void setDescrizione(String descrizione) {
        // cioè sostanzialmente molti metodi vanno a richiamare l'esecuzione
        // della classe madre, poichè il proxy ha come obiettivo solamente
        // quello di settare la modifica a true ed il lazy loading e quindi
        // non va ad intaccare l'obiettivo del metodo scritto nella classe madre
        super.setDescrizione(descrizione);
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
