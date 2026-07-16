package it.univaq.soccorsoweb.data.dao;

import it.univaq.soccorsoweb.data.model.Missione;
import it.univaq.soccorsoweb.data.model.Mezzo;
import it.univaq.framework.data.DataException;
import java.util.List;

public interface MezzoDAO {

    // --- METODI FACTORY ---
    Mezzo createMezzo();

    // METODI DA AGGIORNAMENTI.TXT

    // 1) mezzi disponibili
    List<Mezzo> getMezziDisponibili() throws DataException;

    // 2) info di uno specifico mezzo
    Mezzo getMezzo(int id_mezzo) throws DataException;

    // 3) aggiungere/modificare mezzo
    void storeMezzo(Mezzo mezzo) throws DataException;

    // 4) rimuovere mezzo
    void deleteMezzo(Mezzo mezzo) throws DataException;

    // 5) tutti i mezzi (per essere visualizzati nella pagina admin)
    List<Mezzo> getMezzi() throws DataException;

    // 6) mezzi associati a una specifica missione (chiamato da MissioneProxy)
    List<Mezzo> getMezziByMissione(Missione missione) throws DataException;
}
