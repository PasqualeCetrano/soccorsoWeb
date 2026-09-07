package it.univaq.soccorsoweb.data.model.impl.proxy;

import it.univaq.soccorsoweb.data.dao.AbilitaDAO;
import it.univaq.soccorsoweb.data.dao.AggiornamentoDAO;
import it.univaq.soccorsoweb.data.dao.MissioneDAO;
import it.univaq.soccorsoweb.data.dao.PatenteDAO;

import it.univaq.soccorsoweb.data.dao.UtenteDAO;
import it.univaq.soccorsoweb.data.model.Abilita;
import it.univaq.soccorsoweb.data.model.Aggiornamento;
import it.univaq.soccorsoweb.data.model.Missione;
import it.univaq.soccorsoweb.data.model.Patente;

import it.univaq.soccorsoweb.data.model.Utente;
import it.univaq.soccorsoweb.data.model.impl.UtenteImpl;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataItemProxy;
import it.univaq.framework.data.DataLayer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * quindi in questa classe del proxy, abbiamo effettuato l'Override dei metodi
 * contenuti in
 * UtenteImpl e per modificare i valori degli attributi dell'oggetto Utente
 * salvato in RAM,
 * ci basiamo sul richiamare i metodi presenti in UtenteImpl all'interno dei
 * metodi
 * ridefiniti nel proxy.
 *
 * richiamiamo i metodi in UtenteImpl perchè poichè quegli attributi sono
 * dichiarati private,
 * possono essere modificati solamente dai metodi di quella classe
 *
 * @author Antigravity
 */
public class UtenteProxy extends UtenteImpl implements DataItemProxy {

    // I FILE PROXY FORNISCONO IL TRACCIAMENTO DELLE MODIFICHE E PERMETTONO IL LAZY
    // LOADING

    // Indica se l'oggetto in memoria contiene delle modifiche non ancora salvate
    // sul database.
    protected boolean modified;
    // variabili per chiavi esterne nella tabella, che favoriscono il Lazy Loading
    protected int amministratore_key;

    // Riferimento al DataLayer, necessario per caricare i dati dal database
    // dataLayer è il punto di accesso (connessioni) che permette al Proxy di
    // recuperare i DAO e mantenere la connessione attiva al DB
    // permette di mantenere gli oggetti caricati in memoria
    // necessari per caricare autonomamente i propri dati correlati (Lazy Loading).
    protected DataLayer dataLayer;

    public UtenteProxy(DataLayer d) {
        super();
        // dependency injection
        this.dataLayer = d;
        this.modified = false;
        this.amministratore_key = 0;
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
    public void setCognome(String cognome) {
        super.setCognome(cognome);
        this.modified = true;
    }

    @Override
    public void setEmail(String email) {
        super.setEmail(email);
        this.modified = true;
    }

    @Override
    public void setPassword(String password) {
        super.setPassword(password);
        this.modified = true;
    }

    @Override
    public void setIndirizzo(String indirizzo) {
        super.setIndirizzo(indirizzo);
        this.modified = true;
    }

    @Override
    public void setCodiceFiscale(String codiceFiscale) {
        super.setCodiceFiscale(codiceFiscale);
        this.modified = true;
    }

    @Override
    public void setDataNascita(LocalDate dataNascita) {
        super.setDataNascita(dataNascita);
        this.modified = true;
    }

    @Override
    public void setTipo(String tipo) {
        super.setTipo(tipo);
        this.modified = true;
    }

    @Override
    public Utente getAmministratoreCreatore() {
        // notare come l'amministratore creatore in relazione venga caricato solo su
        // richiesta
        if (super.getAmministratoreCreatore() == null && amministratore_key > 0) {
            try {
                // viene effettuata la chiamata al DAO (dopo aver preso l'istanza dal dataLayer)
                // per recuperare l'amministratore tramite la sua chiave esterna
                // da ricordare che è come se avessimo un Dao generico che contiene tutti gli
                // altri
                // Dao definiti per ogni file dell'interfaccia che abbiamo creato, da quello
                // generico
                // andiamo a trovare il Dao che ci serve e poi lo castiamo al tipo di File Dao
                // definito nel nostro progetto.
                // se non effettuassimo il cast è come se potessimo accedere solamente ai metodi
                // definiti
                // in DAO.java, mentre facendo il cast accediamo anche ai metodi definiti nel
                // DAO
                // specifico che ci serve e poi da questo file andiamo a chiamare il metodo
                // che ci permette di ottenere tutte le info per completare l'oggetto e poi
                // modifichiamo la variabile null
                super.setAmministratoreCreatore(
                        ((UtenteDAO) dataLayer.getDAO(Utente.class)).getUtente(amministratore_key));
            } catch (DataException ex) {
                Logger.getLogger(UtenteProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // va a chiamare sull'oggetto preso in considerazione l'implementazione del
        // metodo
        // getAmministratoreCreatore()presente in UtenteImpl e non l'implementazione
        // presente
        // in UtenteProxy
        return super.getAmministratoreCreatore();
    }

    @Override
    public void setAmministratoreCreatore(Utente amministratore) {
        super.setAmministratoreCreatore(amministratore);
        if (amministratore != null) {
            this.amministratore_key = amministratore.getKey();
        } else {
            this.amministratore_key = 0;
        }
        this.modified = true;
    }

    @Override
    public void setTelefono(String numero) {
        super.setTelefono(numero);
        this.modified = true;
    }

    @Override
    public List<Patente> getPatenti() {
        if (super.getPatenti() == null && getKey() != null && getKey() > 0) {
            try {
                super.setPatenti(((PatenteDAO) dataLayer.getDAO(Patente.class)).getPatentiByUtente(this));
            } catch (DataException ex) {
                Logger.getLogger(UtenteProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (super.getPatenti() == null) {
            super.setPatenti(new ArrayList<>());
        }
        return super.getPatenti();
    }

    @Override
    public void setPatenti(List<Patente> patenti) {
        super.setPatenti(patenti);
        this.modified = true;
    }

    @Override
    public void addPatente(Patente patente) {
        List<Patente> list = getPatenti();
        list.add(patente);
        this.modified = true;
    }

    @Override // recupero le abilità dell utente se non le ho caricate e se l utente ha una
              // key maggiore di 0 quindi è un utente nel database
    public List<Abilita> getAbilita() {
        if (super.getAbilita() == null && getKey() != null && getKey() > 0) {
            try {
                super.setAbilita(((AbilitaDAO) dataLayer.getDAO(Abilita.class)).getAbilitaByUtente(this));
            } catch (DataException ex) {
                Logger.getLogger(UtenteProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (super.getAbilita() == null) {
            super.setAbilita(new ArrayList<>());
        }
        return super.getAbilita();
    }

    @Override
    public void setAbilita(List<Abilita> abilita) {
        super.setAbilita(abilita);
        this.modified = true;
    }

    @Override
    public void addAbilita(Abilita abilita) {
        List<Abilita> list = getAbilita();
        list.add(abilita);
        this.modified = true;
    }

    @Override
    public List<Aggiornamento> getAggiornamenti() {
        if (super.getAggiornamenti() == null && getKey() != null && getKey() > 0) {
            try {
                super.setAggiornamenti(
                        ((AggiornamentoDAO) dataLayer.getDAO(Aggiornamento.class)).getAggiornamentiByUtente(this));
            } catch (DataException ex) {
                Logger.getLogger(UtenteProxy.class.getName()).log(Level.SEVERE, null, ex);
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
        aggiornamento.setUtente(this);
        this.modified = true;
    }

    @Override
    public List<Missione> getMissioniChiuse() {
        if (super.getMissioniChiuse() == null && getKey() != null && getKey() > 0) {
            try {
                super.setMissioniChiuse(
                        ((MissioneDAO) dataLayer.getDAO(Missione.class)).getMissioniChiuseByUtente(this));
            } catch (DataException ex) {
                Logger.getLogger(UtenteProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (super.getMissioniChiuse() == null) {
            super.setMissioniChiuse(new ArrayList<>());
        }
        return super.getMissioniChiuse();
    }

    @Override
    public void setMissioniChiuse(List<Missione> missioniChiuse) {
        super.setMissioniChiuse(missioniChiuse);
        this.modified = true;
    }

    @Override
    public void addMissioneChiusa(Missione missione) {
        List<Missione> list = getMissioniChiuse();
        list.add(missione);
        missione.setAmministratore(this);
        this.modified = true;
    }

    // una volta caricata una lista dal DB, successivamente non verranno fatte
    // query per restituire la lista, ma verrà restituita direttamente
    // la lista memorizzata in RAM
    @Override
    public List<Missione> getMissioniPartecipate() {
        if (super.getMissioniPartecipate() == null && getKey() != null && getKey() > 0) {
            try {
                // ci facciamo restituire il Dao dal dataLayer, in modo tale
                // da avere accesso alla tabella nel DB e prendere la lista in riferimento
                // all'utente
                super.setMissioniPartecipate(
                        ((MissioneDAO) dataLayer.getDAO(Missione.class)).getMissioniPartecipateByUtente(this));
            } catch (DataException ex) {
                Logger.getLogger(UtenteProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //// caso in cui l'utente non è ancora stato inserito nel DB, quindi non ha una
        //// key
        if (super.getMissioniPartecipate() == null) {
            super.setMissioniPartecipate(new ArrayList<>());
        }
        return super.getMissioniPartecipate();
    }

    @Override
    public void setMissioniPartecipate(List<Missione> missioniPartecipate) {
        super.setMissioniPartecipate(missioniPartecipate);
        this.modified = true;
    }

    @Override
    public void addMissionePartecipata(Missione missione) {
        List<Missione> list = getMissioniPartecipate();
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

    public void setAmministratoreKey(int amministratore_key) {
        this.amministratore_key = amministratore_key;
        // resettiamo la cache dell'amministratore
        // reset amministratore cache
        super.setAmministratoreCreatore(null);
    }
}
