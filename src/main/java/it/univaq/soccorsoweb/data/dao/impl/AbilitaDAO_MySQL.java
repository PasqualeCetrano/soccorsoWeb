package it.univaq.soccorsoweb.data.dao.impl;

import it.univaq.framework.data.DAO;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataLayer;
import it.univaq.soccorsoweb.data.dao.AbilitaDAO;
import it.univaq.soccorsoweb.data.model.Abilita;
import it.univaq.soccorsoweb.data.model.Utente;
import it.univaq.soccorsoweb.data.model.impl.proxy.AbilitaProxy;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class AbilitaDAO_MySQL extends DAO implements AbilitaDAO {

    private PreparedStatement selectAbilitaByUtente;
    private PreparedStatement insertAbilita;
    private PreparedStatement selectAbilita;
    private PreparedStatement insertAbilitaUtente;

    @Override
    public void init() throws DataException {
        try {
            super.init();

            selectAbilitaByUtente = connection.prepareStatement(
                    "SELECT a.* FROM Abilita a JOIN Possiede p ON a.id_abilita = p.fk_id_abilita WHERE p.fk_id_utente = ?");
            selectAbilita = connection.prepareStatement("SELECT * FROM Abilita");
            insertAbilita = connection.prepareStatement("INSERT INTO Abilita (descrizione) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS);
            insertAbilitaUtente = connection
                    .prepareStatement("INSERT IGNORE INTO Possiede (fk_id_utente, fk_id_abilita) VALUES (?, ?)");

        } catch (SQLException ex) {
            throw new DataException("Error initializing newspaper data layer", ex);
        }
    }

    public AbilitaDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public Abilita createAbilita() {
        return new AbilitaProxy(getDataLayer());
    }

    private AbilitaProxy createAbilita(ResultSet rs) throws DataException {
        AbilitaProxy a = (AbilitaProxy) createAbilita();
        try {
            a.setKey(rs.getInt("id_abilita"));
            a.setDescrizione(rs.getString("descrizione"));
        } catch (SQLException ex) {
            throw new DataException("Unable to create Abilita object from ResultSet", ex);
        }
        return a;
    }

    @Override
    public List<Abilita> getAbilitaByUtente(Utente utente) throws DataException {
        List<Abilita> result = new java.util.ArrayList<>();
        try {
            selectAbilitaByUtente.setInt(1, utente.getKey());
            try (ResultSet rs = selectAbilitaByUtente.executeQuery()) {
                while (rs.next()) {
                    int id_abilita = rs.getInt("id_abilita");
                    Abilita a = null;
                    // cerco l'abilità nella cache
                    if (dataLayer.getCache().has(Abilita.class, id_abilita)) {
                        a = (Abilita) dataLayer.getCache().get(Abilita.class, id_abilita);
                    } else {
                        a = createAbilita(rs);
                        dataLayer.getCache().add(Abilita.class, a);
                    }
                    result.add(a);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load abilita by utente", ex);
        }
        return result;
    }

    @Override
    public void storeAbilita(Abilita abilita) throws DataException {
        try {
            if (abilita.getKey() != null && abilita.getKey() > 0) {
                // Come per l'utente, gestiamo solo l'inserimento
                throw new DataException(
                        "Aggiornamento non supportato: il metodo storeAbilita permette solo l'inserimento di nuove abilità.");
            }

            insertAbilita.setString(1, abilita.getDescrizione());

            if (insertAbilita.executeUpdate() == 1) {
                try (ResultSet keys = insertAbilita.getGeneratedKeys()) {
                    if (keys.next()) {
                        int key = keys.getInt(1);
                        abilita.setKey(key);
                        dataLayer.getCache().add(Abilita.class, abilita);
                    }
                }
                if (abilita instanceof AbilitaProxy) {
                    ((AbilitaProxy) abilita).setModified(false);
                }
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to store abilita", ex);
        }
    }

    @Override // restitutice tutte le abilita presenti nel db
    public List<Abilita> getAbilita() throws DataException {
        List<Abilita> result = new java.util.ArrayList<>();
        try (ResultSet rs = selectAbilita.executeQuery()) {
            while (rs.next()) {
                int id_abilita = rs.getInt("id_abilita");
                Abilita a = null;

                if (dataLayer.getCache().has(Abilita.class, id_abilita)) {
                    a = (Abilita) dataLayer.getCache().get(Abilita.class, id_abilita);
                } else {
                    a = createAbilita(rs);
                    dataLayer.getCache().add(Abilita.class, a);
                }
                result.add(a);
            }
        } catch (SQLException ex) {
            throw new DataException("Unable to load abilita", ex);
        }
        return result;
    }

    @Override // permette all untente di aggiungere un abilita al suo profilo anche non
              // esistente(se non esiste la salva prima nella tabella e poi nell'associazione)
    public void aggiungiAbilitaUtente(Utente utente, Abilita abilita) throws DataException {
        try {
            // Se l'abilità è nuova e non ha ancora un ID, la salviamo prima nel DB
            if (abilita.getKey() == null) {
                storeAbilita(abilita);
            }

            insertAbilitaUtente.setInt(1, utente.getKey());
            insertAbilitaUtente.setInt(2, abilita.getKey());
            insertAbilitaUtente.executeUpdate();

        } catch (SQLException ex) {
            throw new DataException("Unable to add abilita to utente", ex);
        }
    }

    @Override
    public void destroy() throws DataException {
        try {
            if (selectAbilitaByUtente != null) {
                selectAbilitaByUtente.close();
            }
            if (insertAbilita != null) {
                insertAbilita.close();
            }
            if (selectAbilita != null) {
                selectAbilita.close();
            }
            if (insertAbilitaUtente != null) {
                insertAbilitaUtente.close();
            }
        } catch (SQLException ex) {
            throw new DataException("Errore durante la chiusura delle query nel data layer Abilita", ex);
        }
        super.destroy();
    }
}