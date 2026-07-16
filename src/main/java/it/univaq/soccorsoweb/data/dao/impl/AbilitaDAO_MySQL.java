package it.univaq.soccorsoweb.data.dao.impl;

import it.univaq.framework.data.DAO;
import it.univaq.framework.data.DataException;
import it.univaq.framework.data.DataLayer;
import it.univaq.soccorsoweb.data.dao.AbilitaDAO;
import it.univaq.soccorsoweb.data.model.Abilita;
import it.univaq.soccorsoweb.data.model.Utente;
import java.util.List;

public class AbilitaDAO_MySQL extends DAO implements AbilitaDAO {

    public AbilitaDAO_MySQL(DataLayer d) {
        super(d);
    }

    @Override
    public Abilita createAbilita() {

        throw new UnsupportedOperationException("Unimplemented method 'createAbilita'");
    }

    @Override
    public List<Abilita> getAbilitaByUtente(Utente utente) throws DataException {

        throw new UnsupportedOperationException("Unimplemented method 'getAbilitaByUtente'");
    }

    @Override
    public void storeAbilita(Abilita abilita) throws DataException {

        throw new UnsupportedOperationException("Unimplemented method 'storeAbilita'");
    }

    @Override
    public List<Abilita> getAbilita() throws DataException {

        throw new UnsupportedOperationException("Unimplemented method 'getAbilita'");
    }

}