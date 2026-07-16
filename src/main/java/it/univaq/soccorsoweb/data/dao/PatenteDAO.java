package it.univaq.soccorsoweb.data.dao;

import it.univaq.soccorsoweb.data.model.Patente;
import it.univaq.soccorsoweb.data.model.Utente;
import it.univaq.framework.data.DataException;
import java.util.List;

public interface PatenteDAO {

    // --- METODI FACTORY ---
    Patente createPatente();

    // --- METODI DA AGGIORNAMENTI.TXT E REVISIONE ---

    // 1) recupera la patente di un utente (chiamato anche da UtenteProxy)
    List<Patente> getPatentiByUtente(Utente utente) throws DataException;

    // 2) inserimento di una nuova tipologia di patente nel sistema se non esiste
    // gia
    void storePatente(Patente patente) throws DataException;

    // 3) recupero di tutte le patenti a sistema (utile per riempire le tendine nel
    // web)
    List<Patente> getPatenti() throws DataException;
}
