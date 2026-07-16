package it.univaq.soccorsoweb.data.model.impl.proxy;

import it.univaq.soccorsoweb.data.dao.AbilitaDAO;
import it.univaq.soccorsoweb.data.dao.AggiornamentoDAO;
import it.univaq.soccorsoweb.data.dao.MissioneDAO;
import it.univaq.soccorsoweb.data.dao.PatenteDAO;

import it.univaq.soccorsoweb.data.dao.TelefonoDAO;
import it.univaq.soccorsoweb.data.dao.UtenteDAO;
import it.univaq.soccorsoweb.data.model.Abilita;
import it.univaq.soccorsoweb.data.model.Aggiornamento;
import it.univaq.soccorsoweb.data.model.Missione;
import it.univaq.soccorsoweb.data.model.Patente;

import it.univaq.soccorsoweb.data.model.Telefono;
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
 *
 * @author Antigravity
 */
public class UtenteProxy extends UtenteImpl implements DataItemProxy {

    protected boolean modified;
    protected int amministratore_key;
    protected DataLayer dataLayer;

    public UtenteProxy(DataLayer d) {
        super();
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
        if (super.getAmministratoreCreatore() == null && amministratore_key > 0) {
            try {
                super.setAmministratoreCreatore(
                        ((UtenteDAO) dataLayer.getDAO(Utente.class)).getUtente(amministratore_key));
            } catch (DataException ex) {
                Logger.getLogger(UtenteProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
    public List<Telefono> getTelefoni() {
        if (super.getTelefoni() == null && getKey() != null && getKey() > 0) {
            try {
                super.setTelefoni(((TelefonoDAO) dataLayer.getDAO(Telefono.class)).getTelefoniByUtente(this));
            } catch (DataException ex) {
                Logger.getLogger(UtenteProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // caso in cui l'utente non è ancora stato inserito nel DB, quindi non ha una
        // key,
        // evita di fare una query inutile
        if (super.getTelefoni() == null) {
            super.setTelefoni(new ArrayList<>());
        }
        return super.getTelefoni();
    }

    @Override
    public void setTelefoni(List<Telefono> telefoni) {
        super.setTelefoni(telefoni);
        this.modified = true;
    }

    @Override
    public void addTelefono(Telefono telefono) {
        List<Telefono> list = getTelefoni();
        list.add(telefono);
        telefono.setUtente(this);
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

    @Override
    public List<Missione> getMissioniPartecipate() {
        if (super.getMissioniPartecipate() == null && getKey() != null && getKey() > 0) {
            try {
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

    @Override
    public void setModified(boolean dirty) {
        this.modified = dirty;
    }

    @Override
    public boolean isModified() {
        return modified;
    }

    public void setAmministratoreKey(int amministratore_key) {
        this.amministratore_key = amministratore_key;
        super.setAmministratoreCreatore(null);
    }
}
