package it.univaq.soccorsoweb.data.dao;

import it.univaq.soccorsoweb.data.model.Missione;
import it.univaq.soccorsoweb.data.model.Materiale;
import it.univaq.soccorsoweb.data.model.Mezzo;
import it.univaq.soccorsoweb.data.model.Utente;
import it.univaq.framework.data.DataException;
import java.util.List;

public interface MissioneDAO {

    // --- METODI FACTORY ---
    Missione createMissione();

    // METODI DA AGGIORNAMENTI.TXT

    // 1) inserimento missione
    // 6) chiusura missione (aggiornamento campi)
    void storeMissione(Missione missione) throws DataException;

    // 2) storico missioni di un materiale
    List<Missione> getMissioniByMateriale(Materiale materiale) throws DataException;

    // 3) storico missioni di un mezzo
    List<Missione> getMissioniByMezzo(Mezzo mezzo) throws DataException;

    // 4) missioni chiuse da un certo utente (chiamato anche da UtenteProxy)
    // serve per vedere quale amministratore ha chiuso una missione
    List<Missione> getMissioniChiuseByUtente(Utente utente) throws DataException;

    // 5) missioni in corso o chiuse a cui ha partecipato un certo utente (chiamato
    // anche da UtenteProxy)
    // da usare nella vista operatore per ottenere le missioni in cui è coinvolto
    List<Missione> getMissioniPartecipateByUtente(Utente utente) throws DataException;

    // 7)recupera tutti gli attributi di una missione
    Missione getMissione(int id_missione) throws DataException;

    // 8) missioni in corso per vista amministratore
    List<Missione> getMissioniInCorso() throws DataException;
}
