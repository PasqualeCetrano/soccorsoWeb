package it.univaq.soccorsoweb.data.model.impl;

import it.univaq.framework.data.DataItemImpl;
import it.univaq.soccorsoweb.data.model.Segnalante;

public class SegnalanteImpl extends DataItemImpl<Integer> implements Segnalante {

    private String nome;
    private String email;

    public SegnalanteImpl() {
        this.nome = "";
        this.email = "";
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
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }
}
