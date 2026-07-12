package it.univaq.soccorsoweb.data.model.impl.proxy;

import it.univaq.soccorsoweb.data.model.impl.PatenteImpl;
import it.univaq.framework.data.DataItemProxy;
import it.univaq.framework.data.DataLayer;

public class PatenteProxy extends PatenteImpl implements DataItemProxy {

    protected boolean modified;
    protected DataLayer dataLayer;

    public PatenteProxy(DataLayer d) {
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
    public void setTipo(String tipo) {
        super.setTipo(tipo);
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
