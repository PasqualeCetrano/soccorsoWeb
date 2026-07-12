package it.univaq.soccorsoweb.data.model.impl;

import it.univaq.framework.data.DataItemImpl;
import it.univaq.soccorsoweb.data.model.Patente;

public class PatenteImpl extends DataItemImpl<Integer> implements Patente {

    private String tipo;

    public PatenteImpl() {
        this.tipo = "";
    }

    @Override
    public String getTipo() {
        return tipo;
    }

    @Override
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
