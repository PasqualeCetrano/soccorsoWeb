package it.univaq.soccorsoweb.data.dao.impl;

import it.univaq.framework.data.DAO;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataLayer;
import it.univaq.soccorsoweb.data.dao.RichiestaSoccorsoDAO;
import it.univaq.soccorsoweb.data.model.RichiestaSoccorso;
import it.univaq.soccorsoweb.data.model.impl.proxy.RichiestaSoccorsoProxy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class RichiestaSoccorsoDAO_MySQL extends DAO implements RichiestaSoccorsoDAO {

    // andiamo a definire una variabile per ogni query che vogliamo eseguire su RichiestaSoccorso, così che
    // la query viene compilata una sola volta all'avvio del DataLayer e poi riutilizzata tutte le volte che vogliamo
    // questo permette di migliorare le prestazioni
    private PreparedStatement insertRichiestaSoccorso; // da parte di un untente sulla home
    private PreparedStatement updateRichiestaSoccorso; // da parte di amministratore, cambia stato richiesta
    private PreparedStatement selectRichiestaByStringaConvalida;
    private PreparedStatement selectRichiesteByStato;
    private PreparedStatement selectTutteRichieste;
    private PreparedStatement selectRichiestaById;
    private PreparedStatement cancellaRichiesteScadute;

    // costruttore della classe: riceve in input l'istanza del DataLayer
    public RichiestaSoccorsoDAO_MySQL(DataLayer d) {
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
            // da parte di un untente sulla home, deve avere lo stato di default da
            // convalidare
            insertRichiestaSoccorso = connection.prepareStatement(
                    "INSERT INTO Richiesta_Soccorso (stato, foto, stringa_convalida, ip, descrizione, coordinate, ora_invio, email_segnalante, segnalante) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            // da parte di un admin, cambia stato richiesta, deve essere attiva o ignorata o
            // annullata
            updateRichiestaSoccorso = connection
                    .prepareStatement("UPDATE Richiesta_Soccorso SET stato = ? WHERE id_richiesta_soccorso = ?");
            selectRichiestaByStringaConvalida = connection
                    .prepareStatement("SELECT * FROM Richiesta_Soccorso WHERE stringa_convalida = ?");
            selectRichiesteByStato = connection
                    .prepareStatement("SELECT * FROM Richiesta_Soccorso WHERE stato = ? ORDER BY ora_invio DESC");
            selectTutteRichieste = connection
                    .prepareStatement("SELECT * FROM Richiesta_Soccorso ORDER BY ora_invio DESC");
            selectRichiestaById = connection
                    .prepareStatement("SELECT * FROM Richiesta_Soccorso WHERE id_richiesta_soccorso = ?");
            cancellaRichiesteScadute = connection
                    .prepareStatement("DELETE FROM Richiesta_Soccorso WHERE stato = 'da convalidare' AND ora_invio < (NOW() - INTERVAL ? HOUR)");

        } catch (SQLException ex) {
            throw new DataException("Error initializing richiesta soccorso data layer", ex);
        }
    }

    @Override
    public RichiestaSoccorso createRichiestaSoccorso() {
        // tramite il datalayer il proxy ottiene il dao giusto che contiene 
        // la query per restituire le informazioni che gli servono
        return new RichiestaSoccorsoProxy(getDataLayer());
    }

    // metodo per creare un RichiestaSoccorso partendo da un risultato del database
    // viene usato dal DAO internamente ogni volta che dobbiamo leggere ed esporre richieste già esistenti nel database
    // riceve la riga dal db (result set) e restituisce l'oggetto Proxy di quel record
    private RichiestaSoccorsoProxy createRichiestaSoccorso(ResultSet rs) throws DataException {
        RichiestaSoccorsoProxy r = (RichiestaSoccorsoProxy) createRichiestaSoccorso();
        try {
            r.setKey(rs.getInt("id_richiesta_soccorso"));
            r.setDescrizione(rs.getString("descrizione"));
            r.setCoordinate(rs.getString("coordinate"));

            java.sql.Timestamp ts = rs.getTimestamp("ora_invio");
            if (ts != null) {
                r.setDataOraInvio(ts.toLocalDateTime());
            }

            r.setStato(rs.getString("stato"));
            r.setTokenConvalida(rs.getString("stringa_convalida"));
            r.setIp(rs.getString("ip"));
            r.setFoto(rs.getBytes("foto"));
            r.setSegnalante(rs.getString("segnalante"));
            r.setEmail_segnalante(rs.getString("email_segnalante"));
        } catch (SQLException ex) {
            throw new DataException("Unable to create RichiestaSoccorso object from ResultSet", ex);
        }
        return r;
    }

    @Override // l amministratore modifica lo stato passando la stessa richiesta ma con stato
              // diverso altrimenti sto salvando la riciesta per la prima volta e metto di
              // defaul "da convalidare"
    public void storeRichiestaSoccorso(RichiestaSoccorso richiesta) throws DataException {
        try {
            if (richiesta.getKey() != null && richiesta.getKey() > 0) { // UPDATE
                // Come da query preparata, l'update modifica solo lo stato
                if (richiesta instanceof RichiestaSoccorsoProxy && !((RichiestaSoccorsoProxy) richiesta).isModified()) {
                    return; // Nessuna modifica da salvare
                }
                updateRichiestaSoccorso.setString(1, richiesta.getStato());
                updateRichiestaSoccorso.setInt(2, richiesta.getKey());

                if (updateRichiestaSoccorso.executeUpdate() == 1) {
                    // se abbiamo un proxy, resettiamo il suo attributo dirty
                    if (richiesta instanceof RichiestaSoccorsoProxy) {
                        ((RichiestaSoccorsoProxy) richiesta).setModified(false);
                    }
                }
            } else { // INSERT
                // Forziamo lo stato di default per le nuove richieste
                richiesta.setStato("da convalidare");

                insertRichiestaSoccorso.setString(1, richiesta.getStato());
                insertRichiestaSoccorso.setBytes(2, richiesta.getFoto());
                insertRichiestaSoccorso.setString(3, richiesta.getTokenConvalida());
                insertRichiestaSoccorso.setString(4, richiesta.getIp());
                insertRichiestaSoccorso.setString(5, richiesta.getDescrizione());
                insertRichiestaSoccorso.setString(6, richiesta.getCoordinate());

                if (richiesta.getDataOraInvio() != null) {
                    insertRichiestaSoccorso.setTimestamp(7, java.sql.Timestamp.valueOf(richiesta.getDataOraInvio()));
                } else {
                    insertRichiestaSoccorso.setTimestamp(7, new java.sql.Timestamp(System.currentTimeMillis()));
                }

                insertRichiestaSoccorso.setString(8, richiesta.getEmail_segnalante());
                insertRichiestaSoccorso.setString(9, richiesta.getSegnalante());

                // rappresenta il numero di righe inserite o modificate all'interno del db
                if (insertRichiestaSoccorso.executeUpdate() == 1) {
                    // per leggere la chiave generata dal database per il record appena inserito, 
                    // usiamo il metodo getGeneratedKeys sullo statement.
                    try (ResultSet keys = insertRichiestaSoccorso.getGeneratedKeys()) {
                        // il valore restituito è un ResultSet (tabella di risultati) con un record
                        // per ciascuna chiave generata
                        if (keys.next()) {
                            // i campi del record sono le componenti della chiave
                            // va a leggere il nuovo ID generato dal db
                            int key = keys.getInt(1);
                            // aggiorniamo la chiave in caso di inserimento
                            richiesta.setKey(key);
                            // inseriamo il nuovo oggetto nella cache
                            dataLayer.getCache().add(RichiestaSoccorso.class, richiesta);
                        }
                    }
                    // se abbiamo un proxy, resettiamo il suo attributo dirty
                    if (richiesta instanceof RichiestaSoccorsoProxy) {
                        ((RichiestaSoccorsoProxy) richiesta).setModified(false);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to store RichiestaSoccorso", ex);
        }
    }

    @Override
    public RichiestaSoccorso getRichiestaByStringaConvalida(String stringa) throws DataException {
        try {
            selectRichiestaByStringaConvalida.setString(1, stringa);
            try (ResultSet rs = selectRichiestaByStringaConvalida.executeQuery()) {
                if (rs.next()) {
                    int id_richiesta_soccorso = rs.getInt("id_richiesta_soccorso");
                    // controllo se ha già in memoria un oggetto di tipo RichiestaSoccorso
                    if (dataLayer.getCache().has(RichiestaSoccorso.class, id_richiesta_soccorso)) {
                        return (RichiestaSoccorso) dataLayer.getCache().get(RichiestaSoccorso.class,
                                id_richiesta_soccorso);
                    } else {
                        // altrimenti va a prenderlo dal db (evitando l'N+1 query problem)
                        RichiestaSoccorso r = createRichiestaSoccorso(rs);
                        // non dimentichiamo anche qui la cache!
                        dataLayer.getCache().add(RichiestaSoccorso.class, r);
                        return r;
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load richiesta soccorso by stringa di convalida", ex);
        }
        return null;
    }

    @Override
    public List<RichiestaSoccorso> getRichiesteSoccorsoByStato(String stato) throws DataException {
        List<RichiestaSoccorso> result = new java.util.ArrayList<>();
        try {
            selectRichiesteByStato.setString(1, stato);
            try (ResultSet rs = selectRichiesteByStato.executeQuery()) {
                while (rs.next()) {
                    int id_richiesta_soccorso = rs.getInt("id_richiesta_soccorso");
                    RichiestaSoccorso r = null;
                    if (dataLayer.getCache().has(RichiestaSoccorso.class, id_richiesta_soccorso)) {
                        r = (RichiestaSoccorso) dataLayer.getCache().get(RichiestaSoccorso.class,
                                id_richiesta_soccorso);
                    } else {
                        r = createRichiestaSoccorso(rs);
                        dataLayer.getCache().add(RichiestaSoccorso.class, r);
                    }
                    result.add(r);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load richieste soccorso by stato", ex);
        }
        return result;
    }

    @Override
    public List<RichiestaSoccorso> getTutteRichiesteSoccorso() throws DataException {
        List<RichiestaSoccorso> result = new java.util.ArrayList<>();
        try (ResultSet rs = selectTutteRichieste.executeQuery()) {
            while (rs.next()) {
                int id_richiesta_soccorso = rs.getInt("id_richiesta_soccorso");
                RichiestaSoccorso r = null;
                if (dataLayer.getCache().has(RichiestaSoccorso.class, id_richiesta_soccorso)) {
                    r = (RichiestaSoccorso) dataLayer.getCache().get(RichiestaSoccorso.class, id_richiesta_soccorso);
                } else {
                    r = createRichiestaSoccorso(rs);
                    dataLayer.getCache().add(RichiestaSoccorso.class, r);
                }
                result.add(r);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load all richieste soccorso", ex);
        }
        return result;
    }

    @Override
    public RichiestaSoccorso getRichiestaSoccorso(int id_richiesta) throws DataException {
        try {
            selectRichiestaById.setInt(1, id_richiesta);
            try (ResultSet rs = selectRichiestaById.executeQuery()) {
                if (rs.next()) {
                    if (dataLayer.getCache().has(RichiestaSoccorso.class, id_richiesta)) {
                        return (RichiestaSoccorso) dataLayer.getCache().get(RichiestaSoccorso.class, id_richiesta);
                    } else {
                        RichiestaSoccorso r = createRichiestaSoccorso(rs);
                        dataLayer.getCache().add(RichiestaSoccorso.class, r);
                        return r;
                    }
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load richiesta soccorso by id", ex);
        }
        return null;
    }

    @Override
    public void destroy() throws DataException {
        // nel momento in cui chiudiamo il DB o l'applicazione, viene liberata tutta la
        // memoria allocata per le variabili contenenti le query precompilate
        try {
            if (insertRichiestaSoccorso != null) {
                insertRichiestaSoccorso.close();
            }
            if (updateRichiestaSoccorso != null) {
                updateRichiestaSoccorso.close();
            }
            if (selectRichiestaByStringaConvalida != null) {
                selectRichiestaByStringaConvalida.close();
            }
            if (selectRichiesteByStato != null) {
                selectRichiesteByStato.close();
            }
            if (selectTutteRichieste != null) {
                selectTutteRichieste.close();
            }
            if (selectRichiestaById != null) {
                selectRichiestaById.close();
            }
            if (cancellaRichiesteScadute != null) {
                cancellaRichiesteScadute.close();
            }
        } catch (SQLException ex) {
            throw new DataException("Errore durante la chiusura delle query nel data layer RichiestaSoccorso", ex);
        }
        // va a riprendere l'implementazione del metodo destroy in DAO e la esegue
        super.destroy();
    }

    @Override
    public void cancellaRichiesteScadute(int ore) throws DataException {
        try {
            cancellaRichiesteScadute.setInt(1, ore);
            int cancellate = cancellaRichiesteScadute.executeUpdate();
            System.out.println("[PULIZIA DB] Eliminate " + cancellate + " richieste scadute.");
        } catch (SQLException ex) {
            throw new DataException("Impossibile eliminare le richieste scadute", ex);
        }
    }
}
