package it.univaq.soccorsoweb.data.model.impl;

import it.univaq.framework.data.DataItemImpl;
import it.univaq.soccorsoweb.data.model.Missione;
import it.univaq.soccorsoweb.data.model.Partecipa;
import it.univaq.soccorsoweb.data.model.Squadra;
import java.util.ArrayList;
import java.util.List;

public class SquadraImpl extends DataItemImpl<Integer> implements Squadra {

    private String codice;
    private Missione missione;
    private List<Partecipa> partecipazioni; // serve per tenera traccia del ruolo degli utenti che fanno parte di questa
                                            // squadra

    public SquadraImpl() {
        this.codice = "";
        this.missione = null;
        // Inizializzato a null per supportare il lazy loading del Proxy
        this.partecipazioni = null;
    }

    @Override
    public String getCodice() {
        return codice;
    }

    @Override
    public void setCodice(String codice) {
        this.codice = codice;
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
    public List<Partecipa> getPartecipazioni() {
        return partecipazioni;
    }

    @Override
    public void setPartecipazioni(List<Partecipa> partecipazioni) {
        this.partecipazioni = partecipazioni;
    }

    @Override
    public void addPartecipazione(Partecipa partecipazione) {
        if (this.partecipazioni == null) {
            this.partecipazioni = new ArrayList<>();
        }
        this.partecipazioni.add(partecipazione);
    }
}
