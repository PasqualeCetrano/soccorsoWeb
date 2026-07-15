package it.univaq.soccorsoweb.data.model.impl.proxy;

import it.univaq.soccorsoweb.data.model.impl.RichiestaSoccorsoImpl;
import it.univaq.framework.data.DataItemProxy;
import it.univaq.framework.data.DataLayer;
import java.time.LocalDateTime;

public class RichiestaSoccorsoProxy extends RichiestaSoccorsoImpl implements DataItemProxy {

    protected boolean modified;
    protected DataLayer dataLayer;

    public RichiestaSoccorsoProxy(DataLayer d) {
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
    public void setSegnalante(String segnalante) {
        super.setSegnalante(segnalante);
        this.modified = true;
    }

    @Override
    public void setEmail_segnalante(String email_segnalante) {
        super.setEmail_segnalante(email_segnalante);
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
