package it.univaq.soccorsoweb.data.model.impl;

import it.univaq.framework.data.DataItemImpl;
import it.univaq.soccorsoweb.data.model.Abilita;

public class AbilitaImpl extends DataItemImpl<Integer> implements Abilita {

    private String descrizione;

    public AbilitaImpl() {

        this.descrizione = "";
    }

    @Override
    public String getDescrizione() {
        return descrizione;
    }

    @Override
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
}