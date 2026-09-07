package it.univaq.soccorsoweb.data.model.impl.proxy;

import it.univaq.soccorsoweb.data.dao.AggiornamentoDAO;
import it.univaq.soccorsoweb.data.dao.MaterialeDAO;
import it.univaq.soccorsoweb.data.dao.MezzoDAO;
import it.univaq.soccorsoweb.data.dao.RichiestaSoccorsoDAO;
import it.univaq.soccorsoweb.data.dao.SquadraDAO;
import it.univaq.soccorsoweb.data.dao.UtenteDAO;
import it.univaq.soccorsoweb.data.model.Aggiornamento;
import it.univaq.soccorsoweb.data.model.Materiale;
import it.univaq.soccorsoweb.data.model.Mezzo;
import it.univaq.soccorsoweb.data.model.RichiestaSoccorso;
import it.univaq.soccorsoweb.data.model.Squadra;
import it.univaq.soccorsoweb.data.model.Utente;
import it.univaq.soccorsoweb.data.model.impl.MissioneImpl;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataItemProxy;
import it.univaq.framework.data.DataLayer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * quindi in questa classe del proxy, abbiamo effettuato l'Override dei metodi contenuti in 
 * MissioneImpl e per modificare i valori degli attributi dell'oggetto Missione salvato in RAM,
 * ci basiamo sul richiamare i metodi presenti in MissioneImpl all'interno dei metodi 
 * ridefiniti nel proxy.
 *
 * richiamiamo i metodi in MissioneImpl perchè poichè quegli attributi sono dichiarati private,
 * possono essere modificati solamente dai metodi di quella classe
 *
 * @author Antigravity
 */
public class MissioneProxy extends MissioneImpl implements DataItemProxy {

    // I FILE PROXY FORNISCONO IL TRACCIAMENTO DELLE MODIFICHE E PERMETTONO IL LAZY LOADING

    // Indica se l'oggetto in memoria contiene delle modifiche non ancora salvate sul database.
    protected boolean modified;
    protected int richiesta_key;
    protected int amministratore_key;
    protected int squadra_key;
    // Riferimento al DataLayer, necessario per caricare i dati dal database
    // dataLayer è il punto di accesso (connessioni) che permette al Proxy di
    // recuperare i DAO e mantenere la connessione attiva al DB
    // permette di mantenere gli oggetti caricati in memoria
    // necessari per caricare autonomamente i propri dati correlati (Lazy Loading).
    protected DataLayer dataLayer;

    public MissioneProxy(DataLayer d) {
        super();
        this.dataLayer = d;
        this.modified = false;
        this.richiesta_key = 0;
        this.amministratore_key = 0;
        this.squadra_key = 0;
    }

    @Override
    public void setKey(Integer key) {
        super.setKey(key);
        this.modified = true;
    }

    @Override
    public void setPosizione(String posizione) {
        super.setPosizione(posizione);
        this.modified = true;
    }

    @Override
    public void setObiettivo(String obiettivo) {
        super.setObiettivo(obiettivo);
        this.modified = true;
    }

    @Override
    public void setLivelloSuccesso(Integer livelloSuccesso) {
        super.setLivelloSuccesso(livelloSuccesso);
        this.modified = true;
    }

    @Override
    public void setCommenti(String commenti) {
        super.setCommenti(commenti);
        this.modified = true;
    }

    @Override
    public void setInizio(LocalDateTime inizio) {
        super.setInizio(inizio);
        this.modified = true;
    }

    @Override
    public void setFine(LocalDateTime fine) {
        super.setFine(fine);
        this.modified = true;
    }

    @Override
    public RichiestaSoccorso getRichiestaSoccorso() {
        // notare come la RichiestaSoccorso in relazione venga caricata solo su richiesta
        if (super.getRichiestaSoccorso() == null && richiesta_key > 0) {
            try {
                super.setRichiestaSoccorso(((RichiestaSoccorsoDAO) dataLayer.getDAO(RichiestaSoccorso.class))
                        .getRichiestaSoccorso(richiesta_key));
            } catch (DataException ex) {
                Logger.getLogger(MissioneProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getRichiestaSoccorso();
    }

    @Override
    public void setRichiestaSoccorso(RichiestaSoccorso richiestaSoccorso) {
        super.setRichiestaSoccorso(richiestaSoccorso);
        if (richiestaSoccorso != null) {
            this.richiesta_key = richiestaSoccorso.getKey();
        } else {
            this.richiesta_key = 0;
        }
        this.modified = true;
    }

    @Override
    public Utente getAmministratore() {
        // notare come l'Amministratore in relazione venga caricato solo su richiesta
        if (super.getAmministratore() == null && amministratore_key > 0) {
            try {
                super.setAmministratore(((UtenteDAO) dataLayer.getDAO(Utente.class)).getUtente(amministratore_key));
            } catch (DataException ex) {
                Logger.getLogger(MissioneProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getAmministratore();
    }

    @Override
    public void setAmministratore(Utente amministratore) {
        super.setAmministratore(amministratore);
        if (amministratore != null) {
            this.amministratore_key = amministratore.getKey();
        } else {
            this.amministratore_key = 0;
        }
        this.modified = true;
    }

    @Override
    public Squadra getSquadra() {
        if (super.getSquadra() == null) {
            try {
                super.setSquadra(((SquadraDAO) dataLayer.getDAO(Squadra.class)).getSquadraByMissione(this));
            } catch (DataException ex) {
                Logger.getLogger(MissioneProxy.class.getName()).log(Level.SEVERE, null, ex);
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

    // una volta caricata una lista dal DB, successivamente non verranno fatte
    // query per restituire la lista, ma verrà restituita direttamente
    // la lista memorizzata in RAM
    @Override
    public List<Mezzo> getMezzi() {
        // notare come i Mezzi in relazione vengano caricati solo su richiesta
        if (super.getMezzi() == null && getKey() != null && getKey() > 0) {
            try {
                super.setMezzi(((MezzoDAO) dataLayer.getDAO(Mezzo.class)).getMezziByMissione(this));
            } catch (DataException ex) {
                Logger.getLogger(MissioneProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (super.getMezzi() == null) {
            super.setMezzi(new ArrayList<>());
        }
        return super.getMezzi();
    }

    @Override
    public void setMezzi(List<Mezzo> mezzi) {
        super.setMezzi(mezzi);
        this.modified = true;
    }

    @Override
    public void addMezzo(Mezzo mezzo) {
        List<Mezzo> list = getMezzi();
        list.add(mezzo);
        this.modified = true;
    }

    // una volta caricata una lista dal DB, successivamente non verranno fatte
    // query per restituire la lista, ma verrà restituita direttamente
    // la lista memorizzata in RAM
    @Override
    public List<Materiale> getMateriali() {
        // notare come i Materiali in relazione vengano caricati solo su richiesta
        if (super.getMateriali() == null && getKey() != null && getKey() > 0) {
            try {
                super.setMateriali(((MaterialeDAO) dataLayer.getDAO(Materiale.class)).getMaterialiByMissione(this));
            } catch (DataException ex) {
                Logger.getLogger(MissioneProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (super.getMateriali() == null) {
            super.setMateriali(new ArrayList<>());
        }
        return super.getMateriali();
    }

    @Override
    public void setMateriali(List<Materiale> materiali) {
        super.setMateriali(materiali);
        this.modified = true;
    }

    @Override
    public void addMateriale(Materiale materiale) {
        List<Materiale> list = getMateriali();
        list.add(materiale);
        this.modified = true;
    }

    // una volta caricata una lista dal DB, successivamente non verranno fatte
    // query per restituire la lista, ma verrà restituita direttamente
    // la lista memorizzata in RAM
    @Override
    public List<Aggiornamento> getAggiornamenti() {
        // notare come gli Aggiornamenti in relazione vengano caricati solo su richiesta
        if (super.getAggiornamenti() == null && getKey() != null && getKey() > 0) {
            try {
                super.setAggiornamenti(
                        ((AggiornamentoDAO) dataLayer.getDAO(Aggiornamento.class)).getAggiornamentiByMissione(this));
            } catch (DataException ex) {
                Logger.getLogger(MissioneProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (super.getAggiornamenti() == null) {
            super.setAggiornamenti(new ArrayList<>());
        }
        return super.getAggiornamenti();
    }

    @Override
    public void setAggiornamenti(List<Aggiornamento> aggiornamenti) {
        super.setAggiornamenti(aggiornamenti);
        this.modified = true;
    }

    @Override
    public void addAggiornamento(Aggiornamento aggiornamento) {
        List<Aggiornamento> list = getAggiornamenti();
        list.add(aggiornamento);
        aggiornamento.setMissione(this);
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

    public void setRichiestaKey(int richiesta_key) {
        this.richiesta_key = richiesta_key;
        super.setRichiestaSoccorso(null);
    }

    public void setAmministratoreKey(int amministratore_key) {
        this.amministratore_key = amministratore_key;
        super.setAmministratore(null);
    }

    public void setSquadraKey(int squadra_key) {
        this.squadra_key = squadra_key;
        super.setSquadra(null);
    }
}
