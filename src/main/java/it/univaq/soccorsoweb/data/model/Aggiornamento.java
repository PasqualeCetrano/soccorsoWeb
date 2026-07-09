package it.univaq.soccorsoweb.data.model;

import it.univaq.framework.data.DataItem;
import java.time.LocalDateTime;

public interface Aggiornamento extends DataItem<Integer> {

    String getTesto();

    void setTesto(String testo);

    LocalDateTime getDataOra();

    void setDataOra(LocalDateTime dataOra);

    // --- RELAZIONI BASATE SULLE CHIAVI ESTERNE FISICHE (FK) ---

    // Fa riferimento alla FK verso la missione
    Missione getMissione();

    void setMissione(Missione missione);

    // Fa riferimento alla FK verso l'utente (l'operatore che scrive il report)
    Utente getAutore();

    void setAutore(Utente autore);
}
