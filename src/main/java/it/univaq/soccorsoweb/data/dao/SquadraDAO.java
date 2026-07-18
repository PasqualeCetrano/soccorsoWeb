package it.univaq.soccorsoweb.data.dao;

import it.univaq.soccorsoweb.data.model.Squadra;

import it.univaq.framework.data.DataException;

public interface SquadraDAO {

    // --- METODI FACTORY ---
    Squadra createSquadra();

    // METODI DA AGGIORNAMENTI.TXT

    // 1) Inserimento squadra
    void storeSquadra(Squadra squadra) throws DataException;

    // Metodo di utilità per recuperare una squadra
    Squadra getSquadra(int id_squadra) throws DataException;

}
