package it.univaq.soccorsoweb.data.dao;

import it.univaq.soccorsoweb.data.model.Partecipa;
import it.univaq.soccorsoweb.data.model.Squadra;

import java.util.List;

import it.univaq.framework.data.DataException;

public interface SquadraDAO {

    // --- METODI FACTORY ---
    Squadra createSquadra();

    // Visto che Partecipa è gestita dalla Squadra, usiamo questo DAO come sua
    // Factory!
    Partecipa createPartecipa();

    // METODI DA AGGIORNAMENTI.TXT

    // 1) Inserimento squadra (il DAO si occuperà di salvare sia la Squadra sia la
    // sua lista di Partecipazioni nella tabella ponte)
    void storeSquadra(Squadra squadra) throws DataException;

    // Metodo di utilità per recuperare una squadra
    Squadra getSquadra(int id_squadra) throws DataException;

    // serve per ottenere la lista di utenti e i ruoli che appartengono a una
    // squadra
    List<Partecipa> getPartecipazioniBySquadra(Squadra squadra) throws DataException;

}
