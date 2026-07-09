package it.univaq.soccorsoweb.data.model;

import it.univaq.framework.data.DataItem;

/**
 * Interfaccia dell'entità Mezzo.
 * Rispecchia fedelmente lo schema logico (id_mezzo, nome, descrizione).
 */
public interface Mezzo extends DataItem<Integer> {

    String getNome();

    void setNome(String nome);

    String getDescrizione();

    void setDescrizione(String descrizione);
}
