package it.univaq.soccorsoweb.data.model.impl.proxy;

import it.univaq.soccorsoweb.data.model.impl.RichiestaSoccorsoImpl;
import it.univaq.framework.data.DataItemProxy;
import it.univaq.framework.data.DataLayer;
import java.time.LocalDateTime;

/**
 * quindi in questa classe del proxy, abbiamo effettuato l'Override dei metodi contenuti in 
 * RichiestaSoccorsoImpl e per modificare i valori degli attributi dell'oggetto RichiestaSoccorso salvato in RAM,
 * ci basiamo sul richiamare i metodi presenti in RichiestaSoccorsoImpl all'interno dei metodi 
 * ridefiniti nel proxy.
 *
 * richiamiamo i metodi in RichiestaSoccorsoImpl perchè poichè quegli attributi sono dichiarati private,
 * possono essere modificati solamente dai metodi di quella classe
 *
 * @author Antigravity
 */
public class RichiestaSoccorsoProxy extends RichiestaSoccorsoImpl implements DataItemProxy {

    // I FILE PROXY FORNISCONO IL TRACCIAMENTO DELLE MODIFICHE E PERMETTONO IL LAZY LOADING

    // Indica se l'oggetto in memoria contiene delle modifiche non ancora salvate sul database.
    protected boolean modified;
    // Riferimento al DataLayer, necessario per caricare i dati dal database
    // dataLayer è il punto di accesso (connessioni) che permette al Proxy di
    // recuperare i DAO e mantenere la connessione attiva al DB
    // permette di mantenere gli oggetti caricati in memoria
    // necessari per caricare autonomamente i propri dati correlati (Lazy Loading).
    protected DataLayer dataLayer;

    public RichiestaSoccorsoProxy(DataLayer d) {
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
    public void setDescrizione(String descrizione) {
        super.setDescrizione(descrizione);
        this.modified = true;
    }

    @Override
    public void setCoordinate(String coordinate) {
        super.setCoordinate(coordinate);
        this.modified = true;
    }

    @Override
    public void setDataOraInvio(LocalDateTime dataOraInvio) {
        super.setDataOraInvio(dataOraInvio);
        this.modified = true;
    }

    @Override
    public void setStato(String stato) {
        super.setStato(stato);
        this.modified = true;
    }

    @Override
    public void setTokenConvalida(String tokenConvalida) {
        super.setTokenConvalida(tokenConvalida);
        this.modified = true;
    }

    @Override
    public void setIp(String ip) {
        super.setIp(ip);
        this.modified = true;
    }

    @Override
    public void setFoto(byte[] foto) {
        super.setFoto(foto);
        this.modified = true;
    }

    @Override
    public void setSegnalante(String segnalante) {
        super.setSegnalante(segnalante);
        this.modified = true;
    }

    @Override
    public void setEmail_segnalante(String email_segnalante) {
        super.setEmail_segnalante(email_segnalante);
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
