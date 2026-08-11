package it.univaq.soccorsoweb.application;

import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataLayer;
import it.univaq.soccorsoweb.data.model.*;
import it.univaq.soccorsoweb.data.dao.*;
import it.univaq.soccorsoweb.data.dao.impl.*;

import javax.sql.DataSource;
import java.sql.SQLException;

public class SoccorsoWebDataLayer extends DataLayer {

    public SoccorsoWebDataLayer(DataSource datasource) throws SQLException {
        super(datasource);
    }

    @Override
    public void init() throws DataException {
        // Registriamo tutti i DAO del nostro dominio
        registerDAO(Utente.class, new UtenteDAO_MySQL(this));
        registerDAO(Missione.class, new MissioneDAO_MySQL(this));
        registerDAO(Mezzo.class, new MezzoDAO_MySQL(this));
        registerDAO(Materiale.class, new MaterialeDAO_MySQL(this));
        registerDAO(Squadra.class, new SquadraDAO_MySQL(this));
        registerDAO(RichiestaSoccorso.class, new RichiestaSoccorsoDAO_MySQL(this));
        registerDAO(Abilita.class, new AbilitaDAO_MySQL(this));
        registerDAO(Aggiornamento.class, new AggiornamentoDAO_MySQL(this));
        registerDAO(Partecipa.class, new PartecipaDAO_MySQL(this));
        registerDAO(Patente.class, new PatenteDAO_MySQL(this));
    }

    // Helpers
    public UtenteDAO getUtenteDAO() { return (UtenteDAO) getDAO(Utente.class); }
    public MissioneDAO getMissioneDAO() { return (MissioneDAO) getDAO(Missione.class); }
    public MezzoDAO getMezzoDAO() { return (MezzoDAO) getDAO(Mezzo.class); }
    public MaterialeDAO getMaterialeDAO() { return (MaterialeDAO) getDAO(Materiale.class); }
    public SquadraDAO getSquadraDAO() { return (SquadraDAO) getDAO(Squadra.class); }
    public RichiestaSoccorsoDAO getRichiestaSoccorsoDAO() { return (RichiestaSoccorsoDAO) getDAO(RichiestaSoccorso.class); }
    public AbilitaDAO getAbilitaDAO() { return (AbilitaDAO) getDAO(Abilita.class); }
    public AggiornamentoDAO getAggiornamentoDAO() { return (AggiornamentoDAO) getDAO(Aggiornamento.class); }
    public PartecipaDAO getPartecipaDAO() { return (PartecipaDAO) getDAO(Partecipa.class); }
    public PatenteDAO getPatenteDAO() { return (PatenteDAO) getDAO(Patente.class); }
}
