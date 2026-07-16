package it.univaq.soccorsoweb.data.dao;

import it.univaq.soccorsoweb.data.model.Telefono;
import it.univaq.soccorsoweb.data.model.Utente;
import it.univaq.framework.data.DataException;
import java.util.List;

public interface TelefonoDAO {
    
    // --- METODI FACTORY ---
    Telefono createTelefono();

    // --- METODI PER UTENTE PROXY ---
    List<Telefono> getTelefoniByUtente(Utente utente) throws DataException;
}
