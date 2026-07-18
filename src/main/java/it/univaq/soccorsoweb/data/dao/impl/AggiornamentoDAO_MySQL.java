package it.univaq.soccorsoweb.data.dao.impl;

import it.univaq.framework.data.DAO;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataLayer;
import it.univaq.soccorsoweb.data.dao.AggiornamentoDAO;
import it.univaq.soccorsoweb.data.model.Aggiornamento;
import it.univaq.soccorsoweb.data.model.Missione;
import it.univaq.soccorsoweb.data.model.Utente;
import it.univaq.soccorsoweb.data.model.impl.proxy.AggiornamentoProxy;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class AggiornamentoDAO_MySQL extends DAO implements AggiornamentoDAO {

    private PreparedStatement insertAggiornamento;
    private PreparedStatement selectAggiornamentiByMissione;
    private PreparedStatement selectAggiornamentiByUtente;

    public AggiornamentoDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public void init() throws DataException {
        try {
            super.init();

            insertAggiornamento = connection.prepareStatement(
                    "INSERT INTO Aggiornamento (timestamp_agg, testo, fk_id_missione, fk_id_utente) VALUES (?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);

            selectAggiornamentiByMissione = connection.prepareStatement(
                    "SELECT * FROM Aggiornamento WHERE fk_id_missione = ? ORDER BY timestamp_agg DESC");

            selectAggiornamentiByUtente = connection.prepareStatement(
                    "SELECT * FROM Aggiornamento WHERE fk_id_utente = ? ORDER BY timestamp_agg DESC");

        } catch (SQLException ex) {
            throw new DataException("Error initializing aggiornamento data layer", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (insertAggiornamento != null)
                insertAggiornamento.close();
            if (selectAggiornamentiByMissione != null)
                selectAggiornamentiByMissione.close();
            if (selectAggiornamentiByUtente != null)
                selectAggiornamentiByUtente.close();
        } catch (SQLException ex) {
            // ignore
        }
        super.destroy();
    }

    @Override
    public Aggiornamento createAggiornamento() {
        return new AggiornamentoProxy(getDataLayer());
    }

    private AggiornamentoProxy createAggiornamento(ResultSet rs) throws DataException {
        AggiornamentoProxy a = (AggiornamentoProxy) createAggiornamento();
        try {
            a.setKey(rs.getInt("id_aggiornamento"));
            a.setTesto(rs.getString("testo"));
            a.setTimestampAgg(rs.getTimestamp("timestamp_agg").toLocalDateTime());
            a.setMissioneKey(rs.getInt("fk_id_missione"));
            a.setUtenteKey(rs.getInt("fk_id_utente"));
        } catch (SQLException ex) {
            throw new DataException("Unable to create Aggiornamento object from ResultSet", ex);
        }
        return a;
    }

    @Override
    public void storeAggiornamento(Aggiornamento aggiornamento) throws DataException {
        // Un aggiornamento può essere solo inserito, mai modificato
        if (aggiornamento.getKey() != null && aggiornamento.getKey() > 0) {
            throw new DataException("Un aggiornamento non può essere modificato dopo la creazione");
        }
        try {
            // Il timestamp viene preso al momento esatto dell'inserimento
            java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
            insertAggiornamento.setTimestamp(1, now);
            insertAggiornamento.setString(2, aggiornamento.getTesto());
            insertAggiornamento.setInt(3, aggiornamento.getMissione().getKey());
            insertAggiornamento.setInt(4, aggiornamento.getUtente().getKey());

            if (insertAggiornamento.executeUpdate() == 1) {
                try (ResultSet keys = insertAggiornamento.getGeneratedKeys()) {
                    if (keys.next()) {
                        aggiornamento.setKey(keys.getInt(1));
                        // Aggiorniamo anche il timestamp sull'oggetto in memoria
                        aggiornamento.setTimestampAgg(now.toLocalDateTime());
                        dataLayer.getCache().add(Aggiornamento.class, aggiornamento);
                    }
                }
            }
            if (aggiornamento instanceof AggiornamentoProxy) {
                ((AggiornamentoProxy) aggiornamento).setModified(false);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to store Aggiornamento", ex);
        }
    }

    @Override
    public List<Aggiornamento> getAggiornamentiByMissione(Missione missione) throws DataException {
        List<Aggiornamento> result = new ArrayList<>();
        try {
            selectAggiornamentiByMissione.setInt(1, missione.getKey());
            try (ResultSet rs = selectAggiornamentiByMissione.executeQuery()) {
                while (rs.next()) {
                    int id_aggiornamento = rs.getInt("id_aggiornamento");
                    Aggiornamento a = null;
                    if (dataLayer.getCache().has(Aggiornamento.class, id_aggiornamento)) {
                        a = (Aggiornamento) dataLayer.getCache().get(Aggiornamento.class, id_aggiornamento);
                    } else {
                        a = createAggiornamento(rs);
                        dataLayer.getCache().add(Aggiornamento.class, a);
                    }
                    result.add(a);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load aggiornamenti by missione", ex);
        }
        return result;
    }

    @Override // metodo facoltativo non richiesto dalle specifiche
    public List<Aggiornamento> getAggiornamentiByUtente(Utente utente) throws DataException {
        List<Aggiornamento> result = new ArrayList<>();
        try {
            selectAggiornamentiByUtente.setInt(1, utente.getKey());
            try (ResultSet rs = selectAggiornamentiByUtente.executeQuery()) {
                while (rs.next()) {
                    int id_aggiornamento = rs.getInt("id_aggiornamento");
                    Aggiornamento a = null;
                    if (dataLayer.getCache().has(Aggiornamento.class, id_aggiornamento)) {
                        a = (Aggiornamento) dataLayer.getCache().get(Aggiornamento.class, id_aggiornamento);
                    } else {
                        a = createAggiornamento(rs);
                        dataLayer.getCache().add(Aggiornamento.class, a);
                    }
                    result.add(a);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load aggiornamenti by utente", ex);
        }
        return result;
    }
}
