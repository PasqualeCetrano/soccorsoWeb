package it.univaq.soccorsoweb.data.model.impl;

import it.univaq.framework.data.DataItemImpl;
import it.univaq.soccorsoweb.data.model.Telefono;
import it.univaq.soccorsoweb.data.model.Utente;

public class TelefonoImpl extends DataItemImpl<Integer> implements Telefono {

    private String numero;
    private Utente utente;

    public TelefonoImpl() {
        this.numero = "";
        this.utente = null;
    }

    @Override
    public String getNumero() {
        return numero;
    }

    @Override
    public void setNumero(String numero) {
        this.numero = numero;
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
