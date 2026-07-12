package it.univaq.soccorsoweb.data.model.impl.proxy;

package it.univaq.soccorsoweb.data.model.impl.proxy;

import it.univaq.soccorsoweb.data.dao.SquadraDAO;
import it.univaq.soccorsoweb.data.dao.UtenteDAO;
import it.univaq.soccorsoweb.data.model.Partecipa;
import it.univaq.soccorsoweb.data.model.Squadra;
import it.univaq.soccorsoweb.data.model.Utente;
import it.univaq.soccorsoweb.data.model.impl.PartecipaImpl;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataItemProxy;
import it.univaq.framework.data.DataLayer;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PartecipaProxy extends PartecipaImpl implements DataItemProxy {

    protected boolean modified;
    protected int squadra_key;
    protected int utente_key;
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

    @Override
    public void setModified(boolean dirty) {
        this.modified = dirty;
    }

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
