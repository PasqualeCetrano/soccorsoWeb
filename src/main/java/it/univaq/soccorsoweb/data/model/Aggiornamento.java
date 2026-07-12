package it.univaq.soccorsoweb.data.model;

import it.univaq.framework.data.DataItem;
import java.time.LocalDateTime;

public interface Aggiornamento extends DataItem<Integer> {

    LocalDateTime getTimestampAgg();

    void setTimestampAgg(LocalDateTime timestampAgg);

    String getTesto();

    void setTesto(String testo);

    Missione getMissione();

    void setMissione(Missione missione);

    Utente getUtente();

    void setUtente(Utente utente);

}