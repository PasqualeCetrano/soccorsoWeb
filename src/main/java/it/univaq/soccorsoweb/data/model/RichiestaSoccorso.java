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

    Utente getUtente();

    void setUtente(Utente utente);

    // Lista di segnalanti esterni associati a questa richiesta (tabella Invia)
    List<Segnalante> getSegnalanti();

    void setSegnalanti(List<Segnalante> segnalanti);

    void addSegnalante(Segnalante segnalante);

}
