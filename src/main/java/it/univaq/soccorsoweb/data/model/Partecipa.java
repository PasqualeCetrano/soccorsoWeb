package it.univaq.soccorsoweb.data.model;

import it.univaq.framework.data.DataItem;
import java.util.List;

//ho creato questa interfaccia per la relazione partecipa, poichè essa contiene l'attributo ruolo 
//che permette di verificare se un Utente è operatore o caposquadra 

public interface Partecipa extends DataItem<Integer> {

    Squadra getSquadra();

    void setSquadra(Squadra squadra);

    Utente getUtente();

    void setUtente(Utente utente);

    String getRuolo();

    void setRuolo(String ruolo);

}
