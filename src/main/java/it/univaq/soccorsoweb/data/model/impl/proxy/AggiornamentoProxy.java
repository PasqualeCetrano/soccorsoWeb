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

public class AggiornamentoProxy extends AggiornamentoImpl implements DataItemProxy {

    protected boolean modified;
    protected int missione_key;
    protected int utente_key;
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

    @Override
    public void setModified(boolean dirty) {
        this.modified = dirty;
    }

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
