package it.univaq.soccorsoweb.data.model;

import it.univaq.framework.data.DataItem;

public interface Patente extends DataItem<Integer> {

    String getTipo();

    void setTipo(String tipo);

}
