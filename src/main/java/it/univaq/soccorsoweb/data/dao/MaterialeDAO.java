package it.univaq.soccorsoweb.data.dao;

import it.univaq.soccorsoweb.data.model.Materiale;
import it.univaq.framework.data.DataException;
import java.util.List;

public interface MaterialeDAO {

    // --- METODI FACTORY ---
    Materiale createMateriale();

    // METODI DA AGGIORNAMENTI.TXT

    // 1) materiali disponibili
    List<Materiale> getMaterialiDisponibili() throws DataException;

    // 2) info specifico materiale
    Materiale getMateriale(int id_materiale) throws DataException;

    // 3) aggiungere/modificare materiale
    void storeMateriale(Materiale materiale) throws DataException;

    // 4) rimuovere materiale
    void deleteMateriale(Materiale materiale) throws DataException;

    // 5) tutti i materiali (per essere visualizzati nella pagina admin)
    List<Materiale> getMateriali() throws DataException;
}
