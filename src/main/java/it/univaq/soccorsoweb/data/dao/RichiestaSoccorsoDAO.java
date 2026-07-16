package it.univaq.soccorsoweb.data.dao;

import it.univaq.soccorsoweb.data.model.RichiestaSoccorso;

import it.univaq.framework.data.DataException;
import java.util.List;

public interface RichiestaSoccorsoDAO {

    // --- METODI FACTORY ---
    RichiestaSoccorso createRichiestaSoccorso();

    // 1) inserimento richiesta di soccorso da parte di un utente sulla home
    // 4) modificare lo stato di una richiesta da parte di un amministratore quando
    // si crea una missione o si annulla
    void storeRichiestaSoccorso(RichiestaSoccorso richiesta) throws DataException;

    // 2) convalida (può essere gestito da storeRichiestaSoccorso cambiando stato a
    // 'attiva',
    // oppure con un metodo specifico se si passa la stringa di convalida)
    // QUESTA è DA VERIFICARE!!!!!!!!!!
    RichiestaSoccorso getRichiestaByStringaConvalida(String stringa) throws DataException;

    // 3) ottenere richieste per stato (attive, in corso, ignorate, chiuse) serve
    // per la pagina dell amministratore
    List<RichiestaSoccorso> getRichiesteSoccorsoByStato(String stato) throws DataException;

    // Metodo opzionale per avere tutte le richieste
    List<RichiestaSoccorso> getTutteRichiesteSoccorso() throws DataException;

    // ottenere tutte le info di una richiesta
    RichiestaSoccorso getRichiestaSoccorso(int id_richiesta) throws DataException;

}
