package it.univaq.soccorsoweb.data.dao.impl;

import it.univaq.framework.data.DAO;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataLayer;
import it.univaq.framework.data.DataItemProxy;
import it.univaq.soccorsoweb.data.dao.MaterialeDAO;
import it.univaq.soccorsoweb.data.model.Materiale;
import it.univaq.soccorsoweb.data.model.impl.proxy.MaterialeProxy;
import it.univaq.soccorsoweb.data.model.Missione;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MaterialeDAO_MySQL extends DAO implements MaterialeDAO {

    // andiamo a definire una variabile per ogni query che vogliamo eseguire su Materiale, così che
    // la query viene compilata una sola volta all'avvio del DataLayer e poi riutilizzata tutte le volte che vogliamo
    // questo permette di migliorare le prestazioni
    private PreparedStatement selectMaterialiDisponibili;
    private PreparedStatement selectMaterialeById;
    private PreparedStatement insertMateriale;
    private PreparedStatement deleteMateriale;
    private PreparedStatement updateMateriale;
    private PreparedStatement selectAllMateriali;
    private PreparedStatement selectMaterialiByMissione;
    private PreparedStatement checkMaterialeInUso;
    private PreparedStatement deleteImpiegaMateriale;

    // costruttore della classe: riceve in input l'istanza del DataLayer
    public MaterialeDAO_MySQL(DataLayer d) {
        // chiama il costruttore della superclasse (DAO) passandogli l'istanza del DataLayer, 
        // in modo che il padre possa memorizzarla e renderla disponibile a tutti i metodi
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            // precompiliamo tutte le query utilizzate nella classe così da tenerle salvate in 
            // memoria e poi andiamo a sostituire i ? con i valori che gli passeremo
            // connection rappresenta la connessione al db tramite la quale viene eseguita la query 
            selectMaterialiDisponibili = connection.prepareStatement(
                    "SELECT m.* FROM Materiale m WHERE m.id_materiale NOT IN (SELECT im.fk_id_materiale FROM Impiega_Materiale im JOIN Missione mi ON im.fk_id_missione = mi.id_missione WHERE mi.fine IS NULL)");
            selectMaterialeById = connection.prepareStatement("SELECT * FROM Materiale WHERE id_materiale = ?");
            insertMateriale = connection.prepareStatement("INSERT INTO Materiale (nome, descrizione) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            updateMateriale = connection
                    .prepareStatement("UPDATE Materiale SET nome = ?, descrizione = ? WHERE id_materiale = ?");
            deleteMateriale = connection.prepareStatement("DELETE FROM Materiale WHERE id_materiale = ?");
            selectAllMateriali = connection.prepareStatement("SELECT * FROM Materiale");
            selectMaterialiByMissione = connection.prepareStatement(
                    "SELECT m.* FROM Materiale m JOIN Impiega_Materiale im ON m.id_materiale = im.fk_id_materiale WHERE im.fk_id_missione = ?");
            checkMaterialeInUso = connection.prepareStatement(
                    "SELECT COUNT(*) FROM Impiega_Materiale im JOIN Missione mi ON im.fk_id_missione = mi.id_missione WHERE im.fk_id_materiale = ? AND mi.fine IS NULL");
            deleteImpiegaMateriale = connection.prepareStatement("DELETE FROM Impiega_Materiale WHERE fk_id_materiale = ?");

        } catch (SQLException ex) {
            throw new DataException("Error initializing materiale data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        // nel momento in cui chiudiamo il DB o l'applicazione, viene liberata tutta la
        // memoria allocata per le variabili contenenti le query precompilate
        try {
            selectMaterialiDisponibili.close();
            selectMaterialeById.close();
            insertMateriale.close();
            updateMateriale.close();
            deleteMateriale.close();
            selectAllMateriali.close();
            selectMaterialiByMissione.close();
            checkMaterialeInUso.close();
            deleteImpiegaMateriale.close();
        } catch (SQLException ex) {

        }
        // va a riprendere l'implementazione del metodo destroy in DAO e la esegue
        super.destroy();
    }

    // l'istanza del getDataLayer la otteniamo da ApplicationDataLayer, quando
    // creiamo il file DAO_MySQL
    @Override
    public Materiale createMateriale() {
        // tramite il datalayer il proxy ottiene il dao giusto che contiene 
        // la query per restituire le informazioni che gli servono
        return new MaterialeProxy(getDataLayer());
    }

    // metodo per creare un Materiale partendo da un risultato del database
    // viene usato dal DAO internamente ogni volta che dobbiamo leggere ed esporre materiali già esistenti nel database
    // riceve la riga dal db (result set) e restituisce l'oggetto Proxy di quel record
    private MaterialeProxy createMateriale(ResultSet rs) throws DataException {
        MaterialeProxy m = (MaterialeProxy) createMateriale();
        try {
            m.setKey(rs.getInt("id_materiale"));
            m.setNome(rs.getString("nome"));
            m.setDescrizione(rs.getString("descrizione"));
        } catch (SQLException ex) {
            throw new DataException("Unable to create Materiale object from ResultSet", ex);
        }
        return m;
    }

    @Override
    public List<Materiale> getMaterialiDisponibili() throws DataException {
        List<Materiale> result = new ArrayList<>();
        try (ResultSet rs = selectMaterialiDisponibili.executeQuery()) {
            while (rs.next()) {
                // altrimenti va a prenderlo dal db (qui non abbiamo il check in cache ma lo inseriamo)
                Materiale m = createMateriale(rs);
                // non dimentichiamo anche qui la cache!
                dataLayer.getCache().add(Materiale.class, m);
                result.add(m);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load disponibili materiali", ex);
        }
        return result;
    }

    @Override
    public Materiale getMateriale(int id_materiale) throws DataException {
        // Controlla se il materiale è già nella cache del DataLayer
        if (dataLayer.getCache().has(Materiale.class, id_materiale)) {
            return dataLayer.getCache().get(Materiale.class, id_materiale);
        }
        try {
            selectMaterialeById.setInt(1, id_materiale);
            try (ResultSet rs = selectMaterialeById.executeQuery()) {
                if (rs.next()) {
                    Materiale m = createMateriale(rs);
                    dataLayer.getCache().add(Materiale.class, m);
                    return m;
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load materiale by ID", ex);
        }
        return null;
    }

    @Override
    public void storeMateriale(Materiale materiale) throws DataException {
        try {
            if (materiale.getKey() != null && materiale.getKey() > 0) {
                // UPDATE
                if (materiale instanceof DataItemProxy && !((DataItemProxy) materiale).isModified()) {
                    return;
                }
                updateMateriale.setString(1, materiale.getNome());
                updateMateriale.setString(2, materiale.getDescrizione());
                updateMateriale.setInt(3, materiale.getKey());
                updateMateriale.executeUpdate();
            } else {
                // INSERT
                insertMateriale.setString(1, materiale.getNome());
                insertMateriale.setString(2, materiale.getDescrizione());
            // rappresenta il numero di righe inserite o modificate all'interno del db
            if (insertMateriale.executeUpdate() == 1) {
                // per leggere la chiave generata dal database per il record appena inserito, 
                // usiamo il metodo getGeneratedKeys sullo statement.
                try (ResultSet keys = insertMateriale.getGeneratedKeys()) {
                    // il valore restituito è un ResultSet (tabella di risultati) con un record
                    // per ciascuna chiave generata
                    if (keys.next()) {
                            // i campi del record sono le componenti della chiave
                            // va a leggere il nuovo ID generato dal db
                            int key = keys.getInt(1);
                            // aggiorniamo la chiave in caso di inserimento
                            materiale.setKey(key);
                            // inseriamo il nuovo oggetto nella cache
                            dataLayer.getCache().add(Materiale.class, materiale);
                        }
                    }
                }
            }
            // se abbiamo un proxy, resettiamo il suo attributo dirty
            if (materiale instanceof DataItemProxy) {
                ((DataItemProxy) materiale).setModified(false);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to store materiale", ex);
        }
    }

    @Override
    public void deleteMateriale(Materiale materiale) throws DataException {
        try {
            if (materiale.getKey() == null) {
                throw new DataException("Impossibile eliminare un materiale senza ID");
            }
            // 1. Verifichiamo se il materiale è impiegato in una missione attiva
            checkMaterialeInUso.setInt(1, materiale.getKey());
            try (ResultSet rs = checkMaterialeInUso.executeQuery()) {
                // sposto il cursore e valuto se il numero restituito (indica in quante missioni
                // attive o in corso è impegnato un materiale) è maggiore di 0 e quindi impegnato in almeno una missione
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new DataException("Impossibile eliminare: il materiale è impiegato in una missione attiva");
                }
            }

            // 2. Rimuoviamo i riferimenti dalle missioni storiche concluse
            deleteImpiegaMateriale.setInt(1, materiale.getKey());
            deleteImpiegaMateriale.executeUpdate();

            // 3. Eliminiamo il materiale
            deleteMateriale.setInt(1, materiale.getKey());
            deleteMateriale.executeUpdate();

            // Rimuoviamo dalla cache
            dataLayer.getCache().delete(Materiale.class, materiale.getKey());
        } catch (SQLException ex) {
            throw new DataException("Unable to delete materiale", ex);
        }
    }

    @Override
    public List<Materiale> getMateriali() throws DataException {
        List<Materiale> result = new ArrayList<>();
        try (ResultSet rs = selectAllMateriali.executeQuery()) {
            while (rs.next()) {
                Materiale m = createMateriale(rs);
                dataLayer.getCache().add(Materiale.class, m);
                result.add(m);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load all materiali", ex);
        }
        return result;
    }

    @Override
    public List<Materiale> getMaterialiByMissione(Missione missione) throws DataException {
        List<Materiale> result = new ArrayList<>();
        try {
            selectMaterialiByMissione.setInt(1, missione.getKey());
            try (ResultSet rs = selectMaterialiByMissione.executeQuery()) {
                while (rs.next()) {
                    Materiale m = createMateriale(rs);
                    dataLayer.getCache().add(Materiale.class, m);
                    result.add(m);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load materiali by missione", ex);
        }
        return result;
    }
}
