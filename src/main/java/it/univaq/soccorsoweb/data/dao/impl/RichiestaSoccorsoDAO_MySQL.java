package it.univaq.soccorsoweb.data.dao.impl;

import it.univaq.framework.data.DAO;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataLayer;
import it.univaq.soccorsoweb.data.dao.RichiestaSoccorsoDAO;
import it.univaq.soccorsoweb.data.model.RichiestaSoccorso;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class RichiestaSoccorsoDAO_MySQL extends DAO implements RichiestaSoccorsoDAO {

    private PreparedStatement insertRichiestaSoccorso; // da parte di un untente sulla home
    private PreparedStatement updateRichiestaSoccorso; // da parte di amministratore, cambia stato richiesta
    private PreparedStatement selectRichiestaByStringaConvalida;
    private PreparedStatement selectRichiesteByStato;
    private PreparedStatement selectTutteRichieste;
    private PreparedStatement selectRichiestaById;

    public RichiestaSoccorsoDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();
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

        } catch (SQLException ex) {
            throw new DataException("Error initializing richiesta soccorso data layer", ex);
        }
    }

    @Override
    public RichiestaSoccorso createRichiestaSoccorso() {
        throw new UnsupportedOperationException("Unimplemented method 'createRichiestaSoccorso'");
    }

    @Override
    public void storeRichiestaSoccorso(RichiestaSoccorso richiesta) throws DataException {
        throw new UnsupportedOperationException("Unimplemented method 'storeRichiestaSoccorso'");
    }

    @Override
    public RichiestaSoccorso getRichiestaByStringaConvalida(String stringa) throws DataException {
        throw new UnsupportedOperationException("Unimplemented method 'getRichiestaByStringaConvalida'");
    }

    @Override
    public List<RichiestaSoccorso> getRichiesteSoccorsoByStato(String stato) throws DataException {
        throw new UnsupportedOperationException("Unimplemented method 'getRichiesteSoccorsoByStato'");
    }

    @Override
    public List<RichiestaSoccorso> getTutteRichiesteSoccorso() throws DataException {
        throw new UnsupportedOperationException("Unimplemented method 'getTutteRichiesteSoccorso'");
    }

    @Override
    public RichiestaSoccorso getRichiestaSoccorso(int id_richiesta) throws DataException {
        throw new UnsupportedOperationException("Unimplemented method 'getRichiestaSoccorso'");
    }
}
