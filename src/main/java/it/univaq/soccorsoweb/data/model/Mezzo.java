package it.univaq.soccorsoweb.data.model;

import it.univaq.framework.data.DataItem;
import java.util.List;


public interface Mezzo extends DataItem<Integer> {

    String getNome();

    void setNome(String nome);

    String getDescrizione();

    void setDescrizione(String descrizione);

    // Storico delle missioni a cui questo mezzo ha preso parte
    List<Missione> getMissioni();

    void setMissioni(List<Missione> missioni);

    void addMissione(Missione missione);

}
