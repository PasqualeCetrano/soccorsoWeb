package it.univaq.soccorsoweb.data.model;

import it.univaq.framework.data.DataItem;
import java.time.LocalDateTime;
import java.util.List;

public interface RichiestaSoccorso extends DataItem<Integer> {

    String getDescrizione();

    void setDescrizione(String descrizione);

    Double getLatitudine();

    void setLatitudine(Double latitudine);

    Double getLongitudine();

    void setLongitudine(Double longitudine);

    LocalDateTime getDataOraInvio();

    void setDataOraInvio(LocalDateTime dataOraInvio);

    String getStato();

    void setStato(String stato);

    String getTokenConvalida();

    void setTokenConvalida(String tokenConvalida);

    String getIp();

    void setIp(String ip);

    byte[] getFoto();

    void setFoto(byte[] foto);

    String getSegnalante();

    void setSegnalante(String segnalante);

    String getEmail_segnalante();

    void setEmail_segnalante(String email_segnalante);

}
