package it.univaq.soccorsoweb.data.dao;

import it.univaq.framework.data.DataException;
import it.univaq.soccorsoweb.data.model.Partecipa;
import it.univaq.soccorsoweb.data.model.Squadra;
import it.univaq.soccorsoweb.data.model.Utente;

import java.util.List;

public interface PartecipaDAO {

    Partecipa createPartecipa();

    // query per inserire una partecipazione in squadra
    void storePartecipa(Partecipa partecipa) throws DataException;

    // query per ottenere le partecipazioni data una squadra
    List<Partecipa> getPartecipazioniBySquadra(Squadra squadra) throws DataException;

}
