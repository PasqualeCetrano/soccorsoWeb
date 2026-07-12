package it.univaq.soccorsoweb.data.model.impl.proxy;

package it.univaq.soccorsoweb.data.model.impl.proxy;

import it.univaq.soccorsoweb.data.dao.MissioneDAO;
import it.univaq.soccorsoweb.data.dao.PartecipaDAO;
import it.univaq.soccorsoweb.data.model.Missione;
import it.univaq.soccorsoweb.data.model.Partecipa;
import it.univaq.soccorsoweb.data.model.Squadra;
import it.univaq.soccorsoweb.data.model.impl.SquadraImpl;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataItemProxy;
import it.univaq.framework.data.DataLayer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;



public class SquadraProxy extends SquadraImpl implements DataItemProxy {

    protected boolean modified;
    protected int missione_key;
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

    @Override
    public List<Partecipa> getPartecipazioni() {
        if (super.getPartecipazioni() == null && getKey() != null && getKey() > 0) {
            try {
                super.setPartecipazioni(((PartecipaDAO) dataLayer.getDAO(Partecipa.class)).getPartecipazioniBySquadra(this));
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
}

