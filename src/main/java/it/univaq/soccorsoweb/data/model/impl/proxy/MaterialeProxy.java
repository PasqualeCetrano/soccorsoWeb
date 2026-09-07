package it.univaq.soccorsoweb.data.model.impl.proxy;

import it.univaq.soccorsoweb.data.dao.MissioneDAO;
import it.univaq.soccorsoweb.data.model.Missione;
import it.univaq.soccorsoweb.data.model.impl.MaterialeImpl;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataItemProxy;
import it.univaq.framework.data.DataLayer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * quindi in questa classe del proxy, abbiamo effettuato l'Override dei metodi contenuti in 
 * MaterialeImpl e per modificare i valori degli attributi dell'oggetto Materiale salvato in RAM,
 * ci basiamo sul richiamare i metodi presenti in MaterialeImpl all'interno dei metodi 
 * ridefiniti nel proxy.
 *
 * richiamiamo i metodi in MaterialeImpl perchè poichè quegli attributi sono dichiarati private,
 * possono essere modificati solamente dai metodi di quella classe
 *
 * @author Antigravity
 */
public class MaterialeProxy extends MaterialeImpl implements DataItemProxy {

    // I FILE PROXY FORNISCONO IL TRACCIAMENTO DELLE MODIFICHE E PERMETTONO IL LAZY LOADING

    // Indica se l'oggetto in memoria contiene delle modifiche non ancora salvate sul database.
    protected boolean modified;
    // Riferimento al DataLayer, necessario per caricare i dati dal database
    // dataLayer è il punto di accesso (connessioni) che permette al Proxy di
    // recuperare i DAO e mantenere la connessione attiva al DB
    // permette di mantenere gli oggetti caricati in memoria
    // necessari per caricare autonomamente i propri dati correlati (Lazy Loading).
    protected DataLayer dataLayer;

    public MaterialeProxy(DataLayer d) {
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
    public void setNome(String nome) {
        super.setNome(nome);
        this.modified = true;
    }

    @Override
    public void setDescrizione(String descrizione) {
        super.setDescrizione(descrizione);
        this.modified = true;
    }

    // una volta caricata una lista dal DB, successivamente non verranno fatte
    // query per restituire la lista, ma verrà restituita direttamente
    // la lista memorizzata in RAM
    @Override
    public List<Missione> getMissioni() {
        // con getKey andiamo a chiamare la chiave del materiale stesso
        // notare come le Missioni in relazione vengano caricate solo su richiesta
        if (super.getMissioni() == null && getKey() != null && getKey() > 0) {
            try {
                super.setMissioni(((MissioneDAO) dataLayer.getDAO(Missione.class)).getMissioniByMateriale(this));
            } catch (DataException ex) {
                Logger.getLogger(MaterialeProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // nel caso peggiore viene restituita una lista vuota evitando il null pointer
        // exception
        if (super.getMissioni() == null) {
            super.setMissioni(new ArrayList<>());
        }
        return super.getMissioni();
    }

    @Override
    public void setMissioni(List<Missione> missioni) {
        super.setMissioni(missioni);
        this.modified = true;
    }

    @Override
    public void addMissione(Missione missione) {
        List<Missione> list = getMissioni();
        list.add(missione);
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