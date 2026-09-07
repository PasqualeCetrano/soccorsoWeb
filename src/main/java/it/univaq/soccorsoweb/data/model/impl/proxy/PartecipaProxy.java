package it.univaq.soccorsoweb.data.model.impl.proxy;

import it.univaq.soccorsoweb.data.dao.SquadraDAO;
import it.univaq.soccorsoweb.data.dao.UtenteDAO;
import it.univaq.soccorsoweb.data.model.Squadra;
import it.univaq.soccorsoweb.data.model.Utente;
import it.univaq.soccorsoweb.data.model.impl.PartecipaImpl;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataItemProxy;
import it.univaq.framework.data.DataLayer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * quindi in questa classe del proxy, abbiamo effettuato l'Override dei metodi contenuti in 
 * PartecipaImpl e per modificare i valori degli attributi dell'oggetto Partecipa salvato in RAM,
 * ci basiamo sul richiamare i metodi presenti in PartecipaImpl all'interno dei metodi 
 * ridefiniti nel proxy.
 *
 * richiamiamo i metodi in PartecipaImpl perchè poichè quegli attributi sono dichiarati private,
 * possono essere modificati solamente dai metodi di quella classe
 *
 * @author Antigravity
 */
public class PartecipaProxy extends PartecipaImpl implements DataItemProxy {

    // I FILE PROXY FORNISCONO IL TRACCIAMENTO DELLE MODIFICHE E PERMETTONO IL LAZY LOADING

    // Indica se l'oggetto in memoria contiene delle modifiche non ancora salvate sul database.
    protected boolean modified;
    protected int squadra_key;
    protected int utente_key;
    // Riferimento al DataLayer, necessario per caricare i dati dal database
    // dataLayer è il punto di accesso (connessioni) che permette al Proxy di
    // recuperare i DAO e mantenere la connessione attiva al DB
    // permette di mantenere gli oggetti caricati in memoria
    // necessari per caricare autonomamente i propri dati correlati (Lazy Loading).
    protected DataLayer dataLayer;

    public PartecipaProxy(DataLayer d) {
        super();
        this.dataLayer = d;
        this.modified = false;
        this.squadra_key = 0;
        this.utente_key = 0;
    }

    @Override
    public void setKey(Integer key) {
        super.setKey(key);
        this.modified = true;
    }

    @Override
    public void setRuolo(String ruolo) {
        super.setRuolo(ruolo);
        this.modified = true;
    }

    @Override
    public Squadra getSquadra() {
        // notare come la Squadra in relazione venga caricata solo su richiesta
        if (super.getSquadra() == null && squadra_key > 0) {
            try {
                super.setSquadra(((SquadraDAO) dataLayer.getDAO(Squadra.class)).getSquadra(squadra_key));
            } catch (DataException ex) {
                Logger.getLogger(PartecipaProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getSquadra();
    }

    @Override
    public void setSquadra(Squadra squadra) {
        super.setSquadra(squadra);
        if (squadra != null) {
            this.squadra_key = squadra.getKey();
        } else {
            this.squadra_key = 0;
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
                Logger.getLogger(PartecipaProxy.class.getName()).log(Level.SEVERE, null, ex);
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

    public void setSquadraKey(int squadra_key) {
        this.squadra_key = squadra_key;
        super.setSquadra(null);
    }

    public void setUtenteKey(int utente_key) {
        this.utente_key = utente_key;
        super.setUtente(null);
    }
}
