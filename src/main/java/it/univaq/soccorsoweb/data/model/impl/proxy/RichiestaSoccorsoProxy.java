package it.univaq.soccorsoweb.data.model.impl.proxy;

package it.univaq.soccorsoweb.data.model.impl.proxy;

import it.univaq.soccorsoweb.data.dao.SegnalanteDAO;
import it.univaq.soccorsoweb.data.dao.UtenteDAO;
import it.univaq.soccorsoweb.data.model.RichiestaSoccorso;
import it.univaq.soccorsoweb.data.model.Segnalante;
import it.univaq.soccorsoweb.data.model.Utente;
import it.univaq.soccorsoweb.data.model.impl.RichiestaSoccorsoImpl;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataItemProxy;
import it.univaq.framework.data.DataLayer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class RichiestaSoccorsoProxy extends RichiestaSoccorsoImpl implements DataItemProxy {

    protected boolean modified;
    protected int utente_key;
    protected DataLayer dataLayer;

    public RichiestaSoccorsoProxy(DataLayer d) {
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
    public void setDescrizione(String descrizione) {
        super.setDescrizione(descrizione);
        this.modified = true;
    }

    @Override
    public void setLatitudine(Double latitudine) {
        super.setLatitudine(latitudine);
        this.modified = true;
    }

    @Override
    public void setLongitudine(Double longitudine) {
        super.setLongitudine(longitudine);
        this.modified = true;
    }

    @Override
    public void setDataOraInvio(LocalDateTime dataOraInvio) {
        super.setDataOraInvio(dataOraInvio);
        this.modified = true;
    }

    @Override
    public void setStato(String stato) {
        super.setStato(stato);
        this.modified = true;
    }

    @Override
    public void setTokenConvalida(String tokenConvalida) {
        super.setTokenConvalida(tokenConvalida);
        this.modified = true;
    }

    @Override
    public void setIp(String ip) {
        super.setIp(ip);
        this.modified = true;
    }

    @Override
    public void setFoto(byte[] foto) {
        super.setFoto(foto);
        this.modified = true;
    }

    @Override
    public Utente getUtente() {
        if (super.getUtente() == null && utente_key > 0) {
            try {
                super.setUtente(((UtenteDAO) dataLayer.getDAO(Utente.class)).getUtente(utente_key));
            } catch (DataException ex) {
                Logger.getLogger(RichiestaSoccorsoProxy.class.getName()).log(Level.SEVERE, null, ex);
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
    public List<Segnalante> getSegnalanti() {
        if (super.getSegnalanti() == null && getKey() != null && getKey() > 0) {
            try {
                super.setSegnalanti(((SegnalanteDAO) dataLayer.getDAO(Segnalante.class)).getSegnalantiByRichiestaSoccorso(this));
            } catch (DataException ex) {
                Logger.getLogger(RichiestaSoccorsoProxy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (super.getSegnalanti() == null) {
            super.setSegnalanti(new ArrayList<>());
        }
        return super.getSegnalanti();
    }

    @Override
    public void setSegnalanti(List<Segnalante> segnalanti) {
        super.setSegnalanti(segnalanti);
        this.modified = true;
    }

    @Override
    public void addSegnalante(Segnalante segnalante) {
        List<Segnalante> list = getSegnalanti();
        list.add(segnalante);
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

    public void setUtenteKey(int utente_key) {
        this.utente_key = utente_key;
        super.setUtente(null);
    }
}
