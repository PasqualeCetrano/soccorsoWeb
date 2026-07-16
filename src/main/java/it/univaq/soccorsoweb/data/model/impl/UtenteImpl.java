package it.univaq.soccorsoweb.data.model.impl;

import it.univaq.framework.data.DataItemImpl;
import it.univaq.soccorsoweb.data.model.Abilita;
import it.univaq.soccorsoweb.data.model.Aggiornamento;
import it.univaq.soccorsoweb.data.model.Missione;
import it.univaq.soccorsoweb.data.model.Patente;
import it.univaq.soccorsoweb.data.model.Utente;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UtenteImpl extends DataItemImpl<Integer> implements Utente {

    private String nome;
    private String cognome;
    private String email;
    private String password;
    private String indirizzo;
    private String codiceFiscale;
    private LocalDate dataNascita;
    private String tipo;
    private Utente amministratoreCreatore;
    private Integer telefono;
    private List<Patente> patenti;
    private List<Abilita> abilita;
    private List<Aggiornamento> aggiornamenti;

    private List<Missione> missioniChiuse;
    private List<Missione> missioniPartecipate; // storico missioni

    public UtenteImpl() {
        super();
        this.nome = "";
        this.cognome = "";
        this.email = "";
        this.password = "";
        this.indirizzo = "";
        this.codiceFiscale = "";
        this.dataNascita = null;
        this.tipo = "";
        this.amministratoreCreatore = null;
        // Inizializzati a null per supportare il lazy loading del Proxy
        this.telefono = null;
        this.patenti = null;
        this.abilita = null;
        this.aggiornamenti = null;

        this.missioniChiuse = null;
        this.missioniPartecipate = null;
    }

    @Override
    public String getNome() {
        return nome;
    }

    @Override
    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public String getCognome() {
        return cognome;
    }

    @Override
    public void setCognome(String cognome) {
        this.cognome = cognome;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getIndirizzo() {
        return indirizzo;
    }

    @Override
    public void setIndirizzo(String indirizzo) {
        this.indirizzo = indirizzo;
    }

    @Override
    public String getCodiceFiscale() {
        return codiceFiscale;
    }

    @Override
    public void setCodiceFiscale(String codiceFiscale) {
        this.codiceFiscale = codiceFiscale;
    }

    @Override
    public LocalDate getDataNascita() {
        return dataNascita;
    }

    @Override
    public void setDataNascita(LocalDate dataNascita) {
        this.dataNascita = dataNascita;
    }

    @Override
    public String getTipo() {
        return tipo;
    }

    @Override
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public Utente getAmministratoreCreatore() {
        return amministratoreCreatore;
    }

    @Override
    public void setAmministratoreCreatore(Utente amministratore) {
        this.amministratoreCreatore = amministratore;
    }

    @Override
    public Integer getTelefono() {
        return telefono;
    }

    @Override
    public void setTelefono(Integer numero) {
        this.telefono = numero;
    }

    @Override
    public List<Patente> getPatenti() {
        return patenti;
    }

    @Override
    public void setPatenti(List<Patente> patenti) {
        this.patenti = patenti;
    }

    @Override
    public void addPatente(Patente patente) {
        if (this.patenti == null) {
            this.patenti = new ArrayList<>();
        }
        this.patenti.add(patente);
    }

    @Override
    public List<Abilita> getAbilita() {
        return abilita;
    }

    @Override
    public void setAbilita(List<Abilita> abilita) {
        this.abilita = abilita;
    }

    @Override
    public void addAbilita(Abilita abilita) {
        if (this.abilita == null) {
            this.abilita = new ArrayList<>();
        }
        this.abilita.add(abilita);
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

    @Override
    public List<Missione> getMissioniChiuse() {
        return missioniChiuse;
    }

    @Override
    public void setMissioniChiuse(List<Missione> missioniChiuse) {
        this.missioniChiuse = missioniChiuse;
    }

    @Override
    public void addMissioneChiusa(Missione missione) {
        if (this.missioniChiuse == null) {
            this.missioniChiuse = new ArrayList<>();
        }
        this.missioniChiuse.add(missione);
    }

    @Override
    public List<Missione> getMissioniPartecipate() {
        return missioniPartecipate;
    }

    @Override
    public void setMissioniPartecipate(List<Missione> missioniPartecipate) {
        this.missioniPartecipate = missioniPartecipate;
    }

    @Override
    public void addMissionePartecipata(Missione missione) {
        if (this.missioniPartecipate == null) {
            this.missioniPartecipate = new ArrayList<>();
        }
        this.missioniPartecipate.add(missione);
    }
}