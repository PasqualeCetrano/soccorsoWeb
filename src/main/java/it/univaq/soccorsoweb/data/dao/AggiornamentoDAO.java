package it.univaq.soccorsoweb.data.dao;

import it.univaq.soccorsoweb.data.model.Aggiornamento;
import it.univaq.soccorsoweb.data.model.Missione;
import it.univaq.soccorsoweb.data.model.Utente;
import it.univaq.framework.data.DataException;
import java.util.List;

public interface AggiornamentoDAO {

    // --- METODI FACTORY ---
    Aggiornamento createAggiornamento();

    // METODI DA AGGIORNAMENTI.TXT

    // 1) inserimento aggiornamento (bisogna controllare che la missione non sia
    // chiusa)
    void storeAggiornamento(Aggiornamento aggiornamento) throws DataException;

    // 2) tutti gli aggiornamenti di una missione data una missione
    List<Aggiornamento> getAggiornamentiByMissione(Missione missione) throws DataException;

    // * Metodo richiamato dal UtenteProxy ---metodo facoltativo non richiesto dalle
    // specifiche--
    List<Aggiornamento> getAggiornamentiByUtente(Utente utente) throws DataException;
}
