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

public class MaterialeProxy extends MaterialeImpl implements DataItemProxy {

    protected boolean modified;
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

    @Override
    public List<Missione> getMissioni() {
        if (super.getMissioni() == null && getKey() != null && getKey() > 0) {
            try {
                super.setMissioni(((MissioneDAO) dataLayer.getDAO(Missione.class)).getMissioniByMateriale(this));
            } catch (DataException ex) {
                Logger.getLogger(MaterialeProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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

    @Override
    public void setModified(boolean dirty) {
        this.modified = dirty;
    }

    @Override
    public boolean isModified() {
        return modified;
    }

}