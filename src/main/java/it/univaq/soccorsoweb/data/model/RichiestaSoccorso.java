package it.univaq.soccorsoweb.data.model;

import it.univaq.framework.data.DataItem;
import java.time.LocalDateTime;

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

    // Mappiamo l'attributo "ip" dello schema logico
    String getIp();

    void setIp(String ip);

    byte[] getFoto();

    void setFoto(byte[] foto);

    // FK verso l'utente (fk_id_utente)
    Utente getUtente();

    void setUtente(Utente utente);
}
