package it.univaq.soccorsoweb.data.dao.impl;

import it.univaq.framework.data.DAO;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataItemProxy;
import it.univaq.framework.data.DataLayer;
import it.univaq.soccorsoweb.data.dao.MezzoDAO;
import it.univaq.soccorsoweb.data.model.Mezzo;
import it.univaq.soccorsoweb.data.model.Missione;
import it.univaq.soccorsoweb.data.model.impl.proxy.MezzoProxy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MezzoDAO_MySQL extends DAO implements MezzoDAO {

    // andiamo a definire una variabile per ogni query che vogliamo eseguire su Mezzo, così che
    // la query viene compilata una sola volta all'avvio del DataLayer e poi riutilizzata tutte le volte che vogliamo
    // questo permette di migliorare le prestazioni
    private PreparedStatement selectMezziDisponibili;
    private PreparedStatement selectMezzoById;
    private PreparedStatement insertMezzo;
    private PreparedStatement updateMezzo;
    private PreparedStatement deleteMezzo;
    private PreparedStatement selectAllMezzi;
    private PreparedStatement selectMezziByMissione;
    private PreparedStatement checkMezzoInUso;
    private PreparedStatement deleteImpiegaMezzo;

    // costruttore della classe: riceve in input l'istanza del DataLayer
    public MezzoDAO_MySQL(DataLayer d) {
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
            selectMezziDisponibili = connection.prepareStatement(
                    "SELECT m.* FROM Mezzo m WHERE m.id_mezzo NOT IN (SELECT im.fk_id_mezzo FROM Impiega_Mezzo im JOIN Missione mi ON im.fk_id_missione = mi.id_missione WHERE mi.fine IS NULL)");
            selectMezzoById = connection.prepareStatement("SELECT * FROM Mezzo WHERE id_mezzo = ?");
            insertMezzo = connection.prepareStatement("INSERT INTO Mezzo (nome, descrizione) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            updateMezzo = connection.prepareStatement("UPDATE Mezzo SET nome = ?, descrizione = ? WHERE id_mezzo = ?");
            deleteMezzo = connection.prepareStatement("DELETE FROM Mezzo WHERE id_mezzo = ?");
            selectAllMezzi = connection.prepareStatement("SELECT * FROM Mezzo");
            selectMezziByMissione = connection.prepareStatement(
                    "SELECT * FROM Mezzo WHERE id_mezzo IN (SELECT im.fk_id_mezzo FROM Impiega_Mezzo im WHERE im.fk_id_missione = ?)");
            checkMezzoInUso = connection.prepareStatement(
                    "SELECT COUNT(*) FROM Impiega_Mezzo im JOIN Missione mi ON im.fk_id_missione = mi.id_missione WHERE im.fk_id_mezzo = ? AND mi.fine IS NULL");
            deleteImpiegaMezzo = connection.prepareStatement("DELETE FROM Impiega_Mezzo WHERE fk_id_mezzo = ?");

        } catch (SQLException ex) {
            throw new DataException("Error initializing mezzo data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        // nel momento in cui chiudiamo il DB o l'applicazione, viene liberata tutta la
        // memoria allocata per le variabili contenenti le query precompilate
        try {
            selectMezziDisponibili.close();
            selectMezzoById.close();
            insertMezzo.close();
            updateMezzo.close();
            deleteMezzo.close();
            selectAllMezzi.close();
            selectMezziByMissione.close();
            checkMezzoInUso.close();
            deleteImpiegaMezzo.close();
        } catch (SQLException ex) {
            // ignore
        }
        // va a riprendere l'implementazione del metodo destroy in DAO e la esegue
        super.destroy();
    }

    @Override
    public Mezzo createMezzo() {
        // tramite il datalayer il proxy ottiene il dao giusto che contiene 
        // la query per restituire le informazioni che gli servono
        return new MezzoProxy(getDataLayer());
    }

    // metodo per creare un Mezzo partendo da un risultato del database
    // viene usato dal DAO internamente ogni volta che dobbiamo leggere ed esporre mezzi già esistenti nel database
    // riceve la riga dal db (result set) e restituisce l'oggetto Proxy di quel record
    private MezzoProxy createMezzo(ResultSet rs) throws DataException {
        MezzoProxy m = (MezzoProxy) createMezzo();
        try {
            m.setKey(rs.getInt("id_mezzo"));
            m.setNome(rs.getString("nome"));
            m.setDescrizione(rs.getString("descrizione"));
        } catch (SQLException ex) {
            throw new DataException("Unable to create Mezzo object from ResultSet", ex);
        }
        return m;
    }

    @Override
    public List<Mezzo> getMezziDisponibili() throws DataException {
        List<Mezzo> result = new ArrayList<>();
        try (ResultSet rs = selectMezziDisponibili.executeQuery()) {
            while (rs.next()) {
                int id_mezzo = rs.getInt("id_mezzo");
                Mezzo m = null;
                if (dataLayer.getCache().has(Mezzo.class, id_mezzo)) {
                    m = (Mezzo) dataLayer.getCache().get(Mezzo.class, id_mezzo);
                } else {
                    m = createMezzo(rs);
                    dataLayer.getCache().add(Mezzo.class, m);
                }
                result.add(m);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load mezzi disponibili", ex);
        }
        return result;
    }

    @Override
    public Mezzo getMezzo(int id_mezzo) throws DataException {
        // Controlla se il mezzo è già nella cache del DataLayer
        if (dataLayer.getCache().has(Mezzo.class, id_mezzo)) {
            return (Mezzo) dataLayer.getCache().get(Mezzo.class, id_mezzo);
        }
        try {
            selectMezzoById.setInt(1, id_mezzo);
            try (ResultSet rs = selectMezzoById.executeQuery()) {
                if (rs.next()) {
                    Mezzo m = createMezzo(rs);
                    dataLayer.getCache().add(Mezzo.class, m);
                    return m;
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load mezzo by ID", ex);
        }
        return null;
    }

    @Override
    public void storeMezzo(Mezzo mezzo) throws DataException {
        try {
            if (mezzo.getKey() == null) { // INSERT
                insertMezzo.setString(1, mezzo.getNome());
                insertMezzo.setString(2, mezzo.getDescrizione());
                // rappresenta il numero di righe inserite o modificate all'interno del db
                if (insertMezzo.executeUpdate() == 1) {
                    // per leggere la chiave generata dal database per il record appena inserito, 
                    // usiamo il metodo getGeneratedKeys sullo statement.
                    try (ResultSet keys = insertMezzo.getGeneratedKeys()) {
                        // il valore restituito è un ResultSet (tabella di risultati) con un record
                        // per ciascuna chiave generata
                        if (keys.next()) {
                            // i campi del record sono le componenti della chiave
                            // va a leggere il nuovo ID generato dal db
                            mezzo.setKey(keys.getInt(1));
                            // inseriamo il nuovo oggetto nella cache
                            dataLayer.getCache().add(Mezzo.class, mezzo);
                        }
                    }
                }
            } else { // UPDATE
                if (mezzo instanceof DataItemProxy && !((DataItemProxy) mezzo).isModified()) {
                    return;
                }
                updateMezzo.setString(1, mezzo.getNome());
                updateMezzo.setString(2, mezzo.getDescrizione());
                updateMezzo.setInt(3, mezzo.getKey());
                updateMezzo.executeUpdate();
            }
            // se abbiamo un proxy, resettiamo il suo attributo dirty
            if (mezzo instanceof MezzoProxy) {
                ((MezzoProxy) mezzo).setModified(false);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to store Mezzo", ex);
        }
    }

    @Override
    public void deleteMezzo(Mezzo mezzo) throws DataException {
        try {
            if (mezzo.getKey() == null) {
                throw new DataException("Impossibile eliminare un mezzo senza ID");
            }
            // 1. Verifichiamo se il mezzo è impiegato in una missione attiva
            checkMezzoInUso.setInt(1, mezzo.getKey());
            try (ResultSet rs = checkMezzoInUso.executeQuery()) {
                // sposto il cursore e valuto se il numero restituito (indica in quante missione
                // attive o in corso
                // è impegnato un mezzo )è maggiore di 0 e quindi impegnato in almeno una
                // missione
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new DataException("Impossibile eliminare: il mezzo è impiegato in una missione attiva");
                }
            }

            // 2. Rimuoviamo i riferimenti dalle missioni storiche concluse
            deleteImpiegaMezzo.setInt(1, mezzo.getKey());
            deleteImpiegaMezzo.executeUpdate();

            // 3. Eliminiamo il mezzo
            deleteMezzo.setInt(1, mezzo.getKey());
            deleteMezzo.executeUpdate();

            // Rimuoviamo dalla cache
            dataLayer.getCache().delete(Mezzo.class, mezzo.getKey());
        } catch (SQLException ex) {
            throw new DataException("Unable to delete Mezzo", ex);
        }
    }

    @Override
    public List<Mezzo> getMezzi() throws DataException {
        List<Mezzo> result = new ArrayList<>();
        try (ResultSet rs = selectAllMezzi.executeQuery()) {
            // rs.next è come se fosse posizionato prima della prima riga e quindi va a
            // verificare se vi è effettivamente un rige vera
            while (rs.next()) {
                int id_mezzo = rs.getInt("id_mezzo");
                Mezzo m = null;
                if (dataLayer.getCache().has(Mezzo.class, id_mezzo)) {
                    m = (Mezzo) dataLayer.getCache().get(Mezzo.class, id_mezzo);
                } else {
                    m = createMezzo(rs);
                    dataLayer.getCache().add(Mezzo.class, m);
                }
                result.add(m);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load all mezzi", ex);
        }
        return result;
    }

    @Override
    public List<Mezzo> getMezziByMissione(Missione missione) throws DataException {
        List<Mezzo> result = new ArrayList<>();
        try {
            selectMezziByMissione.setInt(1, missione.getKey());
            try (ResultSet rs = selectMezziByMissione.executeQuery()) {
                while (rs.next()) {
                    int id_mezzo = rs.getInt("id_mezzo");
                    Mezzo m = null;
                    if (dataLayer.getCache().has(Mezzo.class, id_mezzo)) {
                        m = (Mezzo) dataLayer.getCache().get(Mezzo.class, id_mezzo);
                    } else {
                        m = createMezzo(rs);
                        dataLayer.getCache().add(Mezzo.class, m);
                    }
                    result.add(m);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load mezzi by missione", ex);
        }
        return result;
    }
}
