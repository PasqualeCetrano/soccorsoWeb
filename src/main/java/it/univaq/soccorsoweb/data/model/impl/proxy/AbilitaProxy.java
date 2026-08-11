package it.univaq.soccorsoweb.data.model.impl.proxy;

import it.univaq.soccorsoweb.data.model.impl.AbilitaImpl;
import it.univaq.framework.data.DataItemProxy;
import it.univaq.framework.data.DataLayer;

public class AbilitaProxy extends AbilitaImpl implements DataItemProxy {

    // con protected le variabili sono accessibili da tutte le classi all'interno
    // dello
    // stesso package, più tutte le sottoclassi che estendono dalle classi che
    // possiedono quelle variabili, anche se presenti in altri package
    protected boolean modified;
    protected DataLayer dataLayer;

    public AbilitaProxy(DataLayer d) {
        super();
        this.dataLayer = d;
        this.modified = false;
    }

    @Override
    public void setKey(Integer key) {
        // viene eseguito il metodo setKey in AbilitaImpl, poichè non contiene tale
        // metodo
        // viene eseguito il metodo setKey della superclasse DataItemImpl che imposta la
        // variabile key
        // inizialmente le chiavi degli oggetti Java sono null, per poi essere
        // modificate tramite questo metodo in quando l'ID viene generato
        // automaticamente dal DB
        super.setKey(key);
        this.modified = true;
    }

    @Override
    public void setDescrizione(String descrizione) {
        // cioè sostanzialmente molti metodi vanno a richiamare l'esecuzione
        // della classe madre, poichè il proxy ha come obiettivo solamente
        // quello di settare la modifica a true ed il lazy loading e quindi
        // non va ad intaccare l'obiettivo del metodo scritto nella classe madre
        super.setDescrizione(descrizione);
        this.modified = true;
    }

    // dopo che l'oggetto viene salvato nel DB tramite il DAO, la modifica per
    // questo oggetto viene reimpostata a false
    @Override
    public void setModified(boolean dirty) {
        this.modified = dirty;
    }

    // Chiesto dal DAO per sapere: "C'è qualcosa da salvare su DB?"
    @Override
    public boolean isModified() {
        return modified;
    }

}
