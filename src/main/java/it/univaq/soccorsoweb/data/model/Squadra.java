package it.univaq.soccorsoweb.data.model;

import it.univaq.framework.data.DataItem;
import java.util.List;

public interface Squadra extends DataItem<Integer> {

    String getCodice();

    void setCodice(String codice);

    Missione getMissione();

    void setMissione(Missione missione);


    //inserendo una lista di oggetti Partecipa facciamo in modo di sapere quali sono gli utenti che 
    //apprtengono a tale squadra e quali ruoli ricoprono

    List<Partecipa> getPartecipazioni();

    void setPartecipazioni(List<Partecipa> partecipazioni);

    void addPartecipazione(Partecipa partecipazione);

}