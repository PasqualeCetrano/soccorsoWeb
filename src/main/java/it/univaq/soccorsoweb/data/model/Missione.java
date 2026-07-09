package it.univaq.soccorsoweb.data.model;

import it.univaq.framework.data.DataItem;
import java.time.LocalDateTime;
import java.util.List;

public interface Missione extends DataItem<Integer> {

    String getObiettivo();

    void setObiettivo(String obiettivo);

    Double getLatitudine();

    void setLatitudine(Double latitudine);

    Double getLongitudine();

    void setLongitudine(Double longitudine);

    LocalDateTime getDataOraInizio();

    void setDataOraInizio(LocalDateTime dataOraInizio);

    LocalDateTime getDataOraFine();

    void setDataOraFine(LocalDateTime dataOraFine);

    String getLivelloSuccesso();

    void setLivelloSuccesso(String livelloSuccesso);

    String getCommentiConclusivi();

    void setCommentiConclusivi(String commentiConclusivi);

    // --- METODI PER LE CHIAVI ESTERNE FISICHE (FK) ---

    // Corrisponde a fk_id_utente (l'admin/operatore che ha pianificato la missione)
    Utente getAmministratoreCreatore();

    void setAmministratoreCreatore(Utente utente);

    // Corrisponde a fk_id_richiesta_soccorso (la richiesta che ha fatto scattare
    // questa missione)
    RichiestaSoccorso getRichiestaSoccorso();

    void setRichiestaSoccorso(RichiestaSoccorso richiesta);

    List<Mezzo> getMezzi();

    void setMezzi(List<Mezzo> mezzi);

    void addMezzo(Mezzo mezzo);

    List<Materiale> getMateriali();

    void setMateriali(List<Materiale> materiali);

    void addMateriale(Materiale materiale); // --- METODI PER LE RELAZIONI MOLTI-A-MOLTI TRAMITE TABELLA DI JOIN ---

    java.util.List<Utente> getMezziImpiegati();

    void setMezziImpiegati(java.util.List<Utente> mezzi);

}
