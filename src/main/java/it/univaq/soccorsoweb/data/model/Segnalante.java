package it.univaq.soccorsoweb.data.model;

import it.univaq.framework.data.DataItem;

public interface Segnalante extends DataItem<Integer> {

    String getNome();

    void setNome(String nome);

    String getEmail();

    void setEmail(String email);
}
