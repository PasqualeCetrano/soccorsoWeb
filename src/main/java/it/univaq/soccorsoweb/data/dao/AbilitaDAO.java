package it.univaq.soccorsoweb.data.dao;

import it.univaq.soccorsoweb.data.model.Abilita;
import it.univaq.soccorsoweb.data.model.Utente;
import it.univaq.framework.data.DataException;
import java.util.List;

public interface AbilitaDAO {

    // --- METODI FACTORY ---
    Abilita createAbilita();

    // --- METODI DA AGGIORNAMENTI.TXT E REVISIONE ---

    // 1) recupera le abilita di un utente (chiamato anche da UtenteProxy)
    List<Abilita> getAbilitaByUtente(Utente utente) throws DataException;

    // 2) inserimento di una nuova abilita nel sistema se non esiste gia(admin)
    void storeAbilita(Abilita abilita) throws DataException;

    // 3) recupero di tutte le abilita a sistema
    List<Abilita> getAbilita() throws DataException;
}
