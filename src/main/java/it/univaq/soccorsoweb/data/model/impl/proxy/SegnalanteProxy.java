package it.univaq.soccorsoweb.data.model.impl.proxy;

package it.univaq.soccorsoweb.data.model.impl.proxy;

import it.univaq.soccorsoweb.data.model.impl.SegnalanteImpl;
import it.univaq.framework.data.DataItemProxy;
import it.univaq.framework.data.DataLayer;


public class SegnalanteProxy extends SegnalanteImpl implements DataItemProxy {

    protected boolean modified;
    protected DataLayer dataLayer;

    public SegnalanteProxy(DataLayer d) {
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
    public void setEmail(String email) {
        super.setEmail(email);
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
