package it.univaq.soccorsoweb.data.dao.impl;

import it.univaq.framework.data.DAO;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataLayer;
import it.univaq.framework.data.DataItemProxy;
import it.univaq.soccorsoweb.data.dao.PartecipaDAO;
import it.univaq.soccorsoweb.data.dao.SquadraDAO;
import it.univaq.soccorsoweb.data.model.Partecipa;
import it.univaq.soccorsoweb.data.model.Squadra;
import it.univaq.soccorsoweb.data.model.impl.proxy.SquadraProxy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SquadraDAO_MySQL extends DAO implements SquadraDAO {

    // andiamo a definire una variabile per ogni query che vogliamo eseguire su Squadra, così che
    // la query viene compilata una sola volta all'avvio del DataLayer e poi riutilizzata tutte le volte che vogliamo
    // questo permette di migliorare le prestazioni
    private PreparedStatement insertSquadra;
    private PreparedStatement updateSquadra;
    private PreparedStatement selectSquadraById;
    private PreparedStatement selectSquadraByMissione;

    // costruttore della classe: riceve in input l'istanza del DataLayer
    public SquadraDAO_MySQL(DataLayer d) {
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
            insertSquadra = connection.prepareStatement(
                    "INSERT INTO Squadra (codice, fk_id_missione) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);

            updateSquadra = connection.prepareStatement(
                    "UPDATE Squadra SET codice = ?, fk_id_missione = ? WHERE id_squadra = ?");

            selectSquadraById = connection.prepareStatement(
                    "SELECT * FROM Squadra WHERE id_squadra = ?");
            selectSquadraByMissione = connection.prepareStatement(
                    "SELECT * FROM Squadra WHERE fk_id_missione = ?");

        } catch (SQLException ex) {
            throw new DataException("Error initializing Squadra data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        // nel momento in cui chiudiamo il DB o l'applicazione, viene liberata tutta la
        // memoria allocata per le variabili contenenti le query precompilate
        try {
            if (insertSquadra != null) insertSquadra.close();
            if (updateSquadra != null) updateSquadra.close();
            if (selectSquadraById != null) selectSquadraById.close();
            if (selectSquadraByMissione != null) selectSquadraByMissione.close();
        } catch (SQLException ex) {
            // ignore
        }
        // va a riprendere l'implementazione del metodo destroy in DAO e la esegue
        super.destroy();
    }

    @Override
    public Squadra createSquadra() {
        // tramite il datalayer il proxy ottiene il dao giusto che contiene 
        // la query per restituire le informazioni che gli servono
        return new SquadraProxy(getDataLayer());
    }

    // metodo per creare un Squadra partendo da un risultato del database
    // viene usato dal DAO internamente ogni volta che dobbiamo leggere ed esporre squadre già esistenti nel database
    // riceve la riga dal db (result set) e restituisce l'oggetto Proxy di quel record
    private SquadraProxy createSquadra(ResultSet rs) throws DataException {
        SquadraProxy s = (SquadraProxy) createSquadra();
        try {
            s.setKey(rs.getInt("id_squadra"));
            s.setCodice(rs.getString("codice"));
            s.setMissioneKey(rs.getInt("fk_id_missione"));
        } catch (SQLException ex) {
            throw new DataException("Unable to create Squadra object from ResultSet", ex);
        }
        return s;
    }

    @Override
    public void storeSquadra(Squadra squadra) throws DataException {
        try {
            if (squadra.getKey() != null && squadra.getKey() > 0) {
                // UPDATE
                if (squadra instanceof DataItemProxy && !((DataItemProxy) squadra).isModified()) {
                    return;
                }
                updateSquadra.setString(1, squadra.getCodice());
                if (squadra.getMissione() != null) {
                    updateSquadra.setInt(2, squadra.getMissione().getKey());
                } else {
                    throw new DataException("Squadra must be associated with a Missione");
                }
                updateSquadra.setInt(3, squadra.getKey());
                updateSquadra.executeUpdate();
            } else {
                // INSERT
                insertSquadra.setString(1, squadra.getCodice());
                if (squadra.getMissione() != null) {
                    insertSquadra.setInt(2, squadra.getMissione().getKey());
                } else {
                    throw new DataException("Squadra must be associated with a Missione");
                }

                // rappresenta il numero di righe inserite o modificate all'interno del db
                if (insertSquadra.executeUpdate() == 1) {
                    // per leggere la chiave generata dal database per il record appena inserito, 
                    // usiamo il metodo getGeneratedKeys sullo statement.
                    try (ResultSet keys = insertSquadra.getGeneratedKeys()) {
                        // il valore restituito è un ResultSet (tabella di risultati) con un record
                        // per ciascuna chiave generata
                        if (keys.next()) {
                            // i campi del record sono le componenti della chiave
                            // va a leggere il nuovo ID generato dal db
                            int key = keys.getInt(1);
                            // aggiorniamo la chiave in caso di inserimento
                            squadra.setKey(key);
                            // inseriamo il nuovo oggetto nella cache
                            dataLayer.getCache().add(Squadra.class, squadra);
                        }
                    }
                }
            }

            // Delegare il salvataggio o la sincronizzazione di Partecipa
            // PartecipaDAO gestirà i suoi record in autonomia quando richiesto
            // O, se vogliamo che Squadra continui a "sincronizzare" i suoi figli,
            // possiamo usare il PartecipaDAO qui.
            if (squadra.getPartecipazioni() != null) {
                PartecipaDAO partecipaDAO = (PartecipaDAO) dataLayer.getDAO(Partecipa.class);
                if (partecipaDAO != null) {
                    for (Partecipa p : squadra.getPartecipazioni()) {
                        p.setSquadra(squadra); // assicuriamoci che il link ci sia
                        partecipaDAO.storePartecipa(p);
                    }
                }
            }

            // se abbiamo un proxy, resettiamo il suo attributo dirty
            if (squadra instanceof DataItemProxy) {
                ((DataItemProxy) squadra).setModified(false);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to store Squadra", ex);
        }
    }

    @Override
    public Squadra getSquadra(int id_squadra) throws DataException {
        try {
            selectSquadraById.setInt(1, id_squadra);
            try (ResultSet rs = selectSquadraById.executeQuery()) {
                if (rs.next()) {
                    Squadra s = createSquadra(rs);
                    dataLayer.getCache().add(Squadra.class, s);
                    return s;
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load squadra by ID", ex);
        }
        return null;
    }

    @Override
    public Squadra getSquadraByMissione(it.univaq.soccorsoweb.data.model.Missione missione) throws DataException {
        try {
            selectSquadraByMissione.setInt(1, missione.getKey());
            try (ResultSet rs = selectSquadraByMissione.executeQuery()) {
                if (rs.next()) {
                    Squadra s = createSquadra(rs);
                    dataLayer.getCache().add(Squadra.class, s);
                    return s;
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load squadra by Missione", ex);
        }
        return null;
    }
}
