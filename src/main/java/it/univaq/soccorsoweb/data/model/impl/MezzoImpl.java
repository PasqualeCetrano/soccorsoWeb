package it.univaq.soccorsoweb.data.model.impl;

import it.univaq.framework.data.DataItemImpl;
import it.univaq.soccorsoweb.data.model.Mezzo;
import it.univaq.soccorsoweb.data.model.Missione;
import java.util.ArrayList;
import java.util.List;

public class MezzoImpl extends DataItemImpl<Integer> implements Mezzo {

    private String nome;
    private String descrizione;
    // serve per tenere traccia dello storico delle missioni di un mezzo, volendo si
    // sarebbe anche potuto evitare di mettere questa lista dentro Mezzo ed
    // effettuare
    // una query nella tabella Missioni andando a prendere solo quelle con un certo
    // mezzo, ma la query sarebbe stata eseguita ogni volta che si sarebbe voluto
    // vedere
    // lo storico di un Mezzo, mentre con l'introduzione della lista di Missioni in
    // Mezzo la lista viene caricata solamente una volta.
    private List<Missione> missioni;

    public MezzoImpl() {
        this.nome = "";
        this.descrizione = "";
        this.missioni = new ArrayList<>();
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
    public String getDescrizione() {
        return descrizione;
    }

    @Override
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    @Override
    public List<Missione> getMissioni() {
        return missioni;
    }

    @Override // sostituisce la lista di missioni con quella passata
    public void setMissioni(List<Missione> missioni) {
        this.missioni = missioni;
    }

    @Override // aggiunge una missione allo storico
    public void addMissione(Missione missione) {
        if (this.missioni == null) {
            this.missioni = new ArrayList<>();
        }
        this.missioni.add(missione);
    }
}
