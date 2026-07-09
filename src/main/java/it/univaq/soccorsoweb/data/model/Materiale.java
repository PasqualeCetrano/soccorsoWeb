package it.univaq.soccorsoweb.data.model;

import it.univaq.framework.data.DataItem;

/**
 * Interfaccia dell'entità Materiale.
 * Rispecchia fedelmente lo schema logico (id_materiale, nome, descrizione).
 */
public interface Materiale extends DataItem<Integer> {

    String getNome();

    void setNome(String nome);

    String getDescrizione();

    void setDescrizione(String descrizione);
}
