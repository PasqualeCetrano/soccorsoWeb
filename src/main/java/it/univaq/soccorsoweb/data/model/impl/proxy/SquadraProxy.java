package it.univaq.soccorsoweb.data.model.impl.proxy;

import it.univaq.soccorsoweb.data.dao.MissioneDAO;

import it.univaq.soccorsoweb.data.model.Missione;
import it.univaq.soccorsoweb.data.model.Partecipa;

import it.univaq.soccorsoweb.data.model.impl.SquadraImpl;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataItemProxy;
import it.univaq.framework.data.DataLayer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * quindi in questa classe del proxy, abbiamo effettuato l'Override dei metodi contenuti in 
 * SquadraImpl e per modificare i valori degli attributi dell'oggetto Squadra salvato in RAM,
 * ci basiamo sul richiamare i metodi presenti in SquadraImpl all'interno dei metodi 
 * ridefiniti nel proxy.
 *
 * richiamiamo i metodi in SquadraImpl perchè poichè quegli attributi sono dichiarati private,
 * possono essere modificati solamente dai metodi di quella classe
 *
 * @author Antigravity
 */
public class SquadraProxy extends SquadraImpl implements DataItemProxy {

    // I FILE PROXY FORNISCONO IL TRACCIAMENTO DELLE MODIFICHE E PERMETTONO IL LAZY LOADING

    // Indica se l'oggetto in memoria contiene delle modifiche non ancora salvate sul database.
    protected boolean modified;
    protected int missione_key;
    // Riferimento al DataLayer, necessario per caricare i dati dal database
    // dataLayer è il punto di accesso (connessioni) che permette al Proxy di
    // recuperare i DAO e mantenere la connessione attiva al DB
    // permette di mantenere gli oggetti caricati in memoria
    // necessari per caricare autonomamente i propri dati correlati (Lazy Loading).
    protected DataLayer dataLayer;

    public SquadraProxy(DataLayer d) {
        super();
        this.dataLayer = d;
        this.modified = false;
        this.missione_key = 0;
    }

    @Override
    public void setKey(Integer key) {
        super.setKey(key);
        this.modified = true;
    }

    @Override
    public void setCodice(String codice) {
        super.setCodice(codice);
        this.modified = true;
    }

    @Override
    public Missione getMissione() {
        // notare come la Missione in relazione venga caricata solo su richiesta
        if (super.getMissione() == null && missione_key > 0) {
            try {
                super.setMissione(((MissioneDAO) dataLayer.getDAO(Missione.class)).getMissione(missione_key));
            } catch (DataException ex) {
                Logger.getLogger(SquadraProxy.class.getName()).log(Level.SEVERE, null, ex);
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

    // una volta caricata una lista dal DB, successivamente non verranno fatte
    // query per restituire la lista, ma verrà restituita direttamente
    // la lista memorizzata in RAM
    @Override
    public List<Partecipa> getPartecipazioni() {
        // notare come le Partecipazioni in relazione vengano caricate solo su richiesta
        if (super.getPartecipazioni() == null && getKey() != null && getKey() > 0) {
            try {
                super.setPartecipazioni(
                        ((it.univaq.soccorsoweb.data.dao.PartecipaDAO) dataLayer.getDAO(Partecipa.class))
                                .getPartecipazioniBySquadra(this));
            } catch (DataException ex) {
                Logger.getLogger(SquadraProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (super.getPartecipazioni() == null) {
            super.setPartecipazioni(new ArrayList<>());
        }
        return super.getPartecipazioni();
    }

    @Override
    public void setPartecipazioni(List<Partecipa> partecipazioni) {
        super.setPartecipazioni(partecipazioni);
        this.modified = true;
    }

    @Override
    public void addPartecipazione(Partecipa partecipazione) {
        List<Partecipa> list = getPartecipazioni();
        list.add(partecipazione);
        partecipazione.setSquadra(this);
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
}
