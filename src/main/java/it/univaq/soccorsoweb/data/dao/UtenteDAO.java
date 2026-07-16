package it.univaq.soccorsoweb.data.dao;

import it.univaq.soccorsoweb.data.model.Utente;
import it.univaq.framework.data.DataException;
import java.util.List;

public interface UtenteDAO {

    // --- METODI FACTORY ---
    Utente createUtente();

    // --- METODI DA AGGIORNAMENTI.TXT ---

    // 1) operatori disponibili
    List<Utente> getOperatoriDisponibili() throws DataException;

    // 2,3) recupero anagrafica (nome,cognome,email,indirizzo,data di nascita, tipo,
    // patente, abilità,missioni associate)

    Utente getUtente(int id_utente) throws DataException;

    // 4,5) creazione e modifica anagrafica (usata solo da admin)
    void storeUtente(Utente utente) throws DataException;

    // 6) tutti gli operatori (admin)
    List<Utente> getOperatori() throws DataException;
}
