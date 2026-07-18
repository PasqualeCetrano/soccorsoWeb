package it.univaq.soccorsoweb.data.model;

import it.univaq.framework.data.DataItem;
import java.time.LocalDate;
import java.util.List;

public interface Utente extends DataItem<Integer> {

    String getNome();

    void setNome(String nome);

    String getCognome();

    void setCognome(String cognome);

    String getEmail();

    void setEmail(String email);

    String getPassword();

    void setPassword(String password);

    String getIndirizzo();

    void setIndirizzo(String indirizzo);

    String getCodiceFiscale();

    void setCodiceFiscale(String codiceFiscale);

    LocalDate getDataNascita(); 

    void setDataNascita(LocalDate nascita);

    String getTipo(); 

    void setTipo(String tipo);
    
    // Riferimento all'amministratore che ha creato questo utente nel sistema (chiave esterna)
    Utente getAmministratoreCreatore();

    void setAmministratoreCreatore(Utente amministratore);

    String getTelefono();

    void setTelefono(String numero);

    List<Patente> getPatenti();

    void setPatenti(List<Patente> patenti);

    void addPatente(Patente patente);

    List<Abilita> getAbilita();

    void setAbilita(List<Abilita> abilita);

    void addAbilita(Abilita abilita);


    // Lista di aggiornamenti inseriti da questo utente/amministratore
    List<Aggiornamento> getAggiornamenti();

    void setAggiornamenti(List<Aggiornamento> aggiornamenti);

    void addAggiornamento(Aggiornamento aggiornamento);




    // Missioni chiuse da questo utente (se amministratore)
    List<Missione> getMissioniChiuse();

    void setMissioniChiuse(List<Missione> missioniChiuse);

    void addMissioneChiusa(Missione missione);


    // Missioni a cui l'utente ha partecipato sul campo (se operatore) rapp lo storico
    List<Missione> getMissioniPartecipate();

    void setMissioniPartecipate(List<Missione> missioniPartecipate);

    void addMissionePartecipata(Missione missione);


}
