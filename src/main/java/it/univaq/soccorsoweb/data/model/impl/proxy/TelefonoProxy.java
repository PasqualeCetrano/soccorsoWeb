package it.univaq.soccorsoweb.data.model.impl.proxy;

import it.univaq.soccorsoweb.data.model.impl.TelefonoImpl;
import it.univaq.soccorsoweb.data.model.Utente;
import it.univaq.framework.data.DataLayer;
import it.univaq.framework.data.DataItemProxy;

import java.util.logging.Level;
import java.util.logging.Logger;

public class TelefonoProxy extends TelefonoImpl implements DataItemProxy {

    protected boolean modified;
    protected int utente_key;
    protected DataLayer dataLayer;

    public TelefonoProxy(DataLayer d) {
        super();
        this.dataLayer = d;
        this.modified = false;
        this.utente_key = 0;
    }

    @Override
    public void setKey(Integer key) {
        super.setKey(key);
        this.modified = true;
    }

    @Override
    public void setNumero(String numero) {
        super.setNumero(numero);
        this.modified = true;
    }

    @Override
    public Utente getUtente() {
        if (super.getUtente() == null && utente_key > 0) {
            try {
                super.setUtente(((UtenteDAO) dataLayer.getDAO(Utente.class)).getUtente(utente_key));
            } catch (DataException ex) {
                Logger.getLogger(TelefonoProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return super.getUtente();
    }

    /*
     * @Override
     * public void setUtente(Utente utente) {
     * super.setUtente(utente);
     * if (utente != null) {
     * this.utente_key = utente.getKey();
     * } else {
     * this.utente_key = 0;
     * }
     * this.modified = true;
     * }
     */

    @Override
    public void setModified(boolean dirty) {
        this.modified = dirty;
    }

    @Override
    public boolean isModified() {
        return modified;
    }

    public void setUtenteKey(int utente_key) {
        this.utente_key = utente_key;
        super.setUtente(null);
    }

}
