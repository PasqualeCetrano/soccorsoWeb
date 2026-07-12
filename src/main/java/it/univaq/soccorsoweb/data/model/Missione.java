package it.univaq.soccorsoweb.data.model;

import it.univaq.framework.data.DataItem;
import java.time.LocalDateTime;


public interface Missione extends DataItem<Integer> {

    String getPosizione();

    void setPosizione(String posizione);

    String getObiettivo();

    void setObiettivo(String obiettivo);

    Integer getLivelloSuccesso();

    void setLivelloSuccesso(Integer livelloSuccesso);

    String getCommenti();

    void setCommenti(String commenti);

    LocalDateTime getInizio();

    void setInizio(LocalDateTime inizio);

    LocalDateTime getFine();

    void setFine(LocalDateTime fine);

    // Richiesta di soccorso associata
    RichiestaSoccorso getRichiestaSoccorso();

    void setRichiestaSoccorso(RichiestaSoccorso richiestaSoccorso);

    // Amministratore che ha chiuso la missione 
    Utente getAmministratore();

    void setAmministratore(Utente amministratore);

    // Squadra di operatori assegnata (aggiunta in più non presente nel DB)
    Squadra getSquadra();

    void setSquadra(Squadra squadra);

    // Lista dei mezzi impiegati (tabella Impiega_Mezzo)
    List<Mezzo> getMezzi();

    void setMezzi(List<Mezzo> mezzi);

    void addMezzo(Mezzo mezzo);

    // Lista dei materiali impiegati (tabella Impiega_Materiale)
    List<Materiale> getMateriali();

    void setMateriali(List<Materiale> materiali);

    void addMateriale(Materiale materiale);

    // Storico degli aggiornamenti inseriti durante la missione
    List<Aggiornamento> getAggiornamenti();

    void setAggiornamenti(List<Aggiornamento> aggiornamenti);

    void addAggiornamento(Aggiornamento aggiornamento);

}
