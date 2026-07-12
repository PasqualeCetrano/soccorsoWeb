package it.univaq.soccorsoweb.data.model;

import it.univaq.framework.data.DataItem;

public interface Telefono extends DataItem<Integer> {

    String getNumero();

    void setNumero(String numero);

    Utente getUtente();

    void setUtente(Utente utente);

}
