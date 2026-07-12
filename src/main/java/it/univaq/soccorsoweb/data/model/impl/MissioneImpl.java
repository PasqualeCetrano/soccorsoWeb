package it.univaq.soccorsoweb.data.model.impl;

import it.univaq.framework.data.DataItemImpl;
import it.univaq.soccorsoweb.data.model.Aggiornamento;
import it.univaq.soccorsoweb.data.model.Materiale;
import it.univaq.soccorsoweb.data.model.Mezzo;
import it.univaq.soccorsoweb.data.model.Missione;
import it.univaq.soccorsoweb.data.model.RichiestaSoccorso;
import it.univaq.soccorsoweb.data.model.Squadra;
import it.univaq.soccorsoweb.data.model.Utente;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MissioneImpl extends DataItemImpl<Integer> implements Missione {

    private String posizione;
    private String obiettivo;
    private Integer livelloSuccesso;
    private String commenti;
    private LocalDateTime inizio;
    private LocalDateTime fine;
    private RichiestaSoccorso richiestaSoccorso;
    private Utente amministratore;
    private Squadra squadra;
    private List<Mezzo> mezzi;
    private List<Materiale> materiali;
    private List<Aggiornamento> aggiornamenti;

    public MissioneImpl() {
        this.posizione = "";
        this.obiettivo = "";
        this.livelloSuccesso = null;
        this.commenti = "";
        this.inizio = null;
        this.fine = null;
        this.richiestaSoccorso = null;
        this.amministratore = null;
        this.squadra = null;
        this.mezzi = new ArrayList<>();
        this.materiali = new ArrayList<>();
        this.aggiornamenti = new ArrayList<>();
    }

    @Override
    public String getPosizione() {
        return posizione;
    }

    @Override
    public void setPosizione(String posizione) {
        this.posizione = posizione;
    }

    @Override
    public String getObiettivo() {
        return obiettivo;
    }

    @Override
    public void setObiettivo(String obiettivo) {
        this.obiettivo = obiettivo;
    }

    @Override
    public Integer getLivelloSuccesso() {
        return livelloSuccesso;
    }

    @Override
    public void setLivelloSuccesso(Integer livelloSuccesso) {
        this.livelloSuccesso = livelloSuccesso;
    }

    @Override
    public String getCommenti() {
        return commenti;
    }

    @Override
    public void setCommenti(String commenti) {
        this.commenti = commenti;
    }

    @Override
    public LocalDateTime getInizio() {
        return inizio;
    }

    @Override
    public void setInizio(LocalDateTime inizio) {
        this.inizio = inizio;
    }

    @Override
    public LocalDateTime getFine() {
        return fine;
    }

    @Override
    public void setFine(LocalDateTime fine) {
        this.fine = fine;
    }

    @Override
    public RichiestaSoccorso getRichiestaSoccorso() {
        return richiestaSoccorso;
    }

    @Override
    public void setRichiestaSoccorso(RichiestaSoccorso richiestaSoccorso) {
        this.richiestaSoccorso = richiestaSoccorso;
    }

    @Override
    public Utente getAmministratore() {
        return amministratore;
    }

    @Override
    public void setAmministratore(Utente amministratore) {
        this.amministratore = amministratore;
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
    public List<Mezzo> getMezzi() {
        return mezzi;
    }

    @Override
    public void setMezzi(List<Mezzo> mezzi) {
        this.mezzi = mezzi;
    }

    @Override
    public void addMezzo(Mezzo mezzo) {
        if (this.mezzi == null) {
            this.mezzi = new ArrayList<>();
        }
        this.mezzi.add(mezzo);
    }

    @Override
    public List<Materiale> getMateriali() {
        return materiali;
    }

    @Override
    public void setMateriali(List<Materiale> materiali) {
        this.materiali = materiali;
    }

    @Override
    public void addMateriale(Materiale materiale) {
        if (this.materiali == null) {
            this.materiali = new ArrayList<>();
        }
        this.materiali.add(materiale);
    }

    @Override
    public List<Aggiornamento> getAggiornamenti() {
        return aggiornamenti;
    }

    @Override
    public void setAggiornamenti(List<Aggiornamento> aggiornamenti) {
        this.aggiornamenti = aggiornamenti;
    }

    @Override
    public void addAggiornamento(Aggiornamento aggiornamento) {
        if (this.aggiornamenti == null) {
            this.aggiornamenti = new ArrayList<>();
        }
        this.aggiornamenti.add(aggiornamento);
    }
}
