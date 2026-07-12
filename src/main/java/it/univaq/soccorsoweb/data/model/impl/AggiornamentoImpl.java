package it.univaq.soccorsoweb.data.model.impl;

import it.univaq.framework.data.DataItemImpl;
import it.univaq.soccorsoweb.data.model.Aggiornamento;
import it.univaq.soccorsoweb.data.model.Missione;
import it.univaq.soccorsoweb.data.model.Utente;
import java.time.LocalDateTime;

public class AggiornamentoImpl extends DataItemImpl<Integer> implements Aggiornamento {

    private LocalDateTime timestampAgg;
    private String testo;
    private Missione missione;
    private Utente utente;

    public AggiornamentoImpl() {
        this.timestampAgg = null;
        this.testo = "";
        this.missione = null;
        this.utente = null;
    }

    @Override
    public LocalDateTime getTimestampAgg() {
        return timestampAgg;
    }

    @Override
    public void setTimestampAgg(LocalDateTime timestampAgg) {
        this.timestampAgg = timestampAgg;
    }

    @Override
    public String getTesto() {
        return testo;
    }

    @Override
    public void setTesto(String testo) {
        this.testo = testo;
    }

    @Override
    public Missione getMissione() {
        return missione;
    }

    @Override
    public void setMissione(Missione missione) {
        this.missione = missione;
    }

    @Override
    public Utente getUtente() {
        return utente;
    }

    @Override
    public void setUtente(Utente utente) {
        this.utente = utente;
    }
}
