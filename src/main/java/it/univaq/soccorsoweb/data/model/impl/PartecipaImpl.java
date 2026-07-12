package it.univaq.soccorsoweb.data.model.impl;

import it.univaq.framework.data.DataItemImpl;
import it.univaq.soccorsoweb.data.model.Partecipa;
import it.univaq.soccorsoweb.data.model.Squadra;
import it.univaq.soccorsoweb.data.model.Utente;

public class PartecipaImpl extends DataItemImpl<Integer> implements Partecipa {

    private Squadra squadra;
    private Utente utente;
    private String ruolo;

    public PartecipaImpl() {
        this.squadra = null;
        this.utente = null;
        this.ruolo = "";
    }

    @Override
    public Squadra getSquadra() {
        return squadra;
    }

    @Override
    public void setSquadra(Squadra squadra) {
        this.squadra = squadra;
    }

    @Override
    public Utente getUtente() {
        return utente;
    }

    @Override
    public void setUtente(Utente utente) {
        this.utente = utente;
    }

    @Override
    public String getRuolo() {
        return ruolo;
    }

    @Override
    public void setRuolo(String ruolo) {
        this.ruolo = ruolo;
    }
}
