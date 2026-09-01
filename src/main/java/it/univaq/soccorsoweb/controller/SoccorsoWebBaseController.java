package it.univaq.soccorsoweb.controller;

import it.univaq.framework.controller.AbstractBaseController;
import it.univaq.framework.data.DataLayer;
import it.univaq.soccorsoweb.application.SoccorsoWebDataLayer;
import jakarta.servlet.ServletException;
import javax.sql.DataSource;
import java.sql.SQLException;

public abstract class SoccorsoWebBaseController extends AbstractBaseController {
    // prende il DataSource da AbstractBaseController che lo prende dal contesto
    // attuale
    // dell'applicazione grazie ad ApplicationInitializer che lo legge dal web.xml
    @Override
    protected DataLayer createDataLayer(DataSource ds) throws ServletException {
        try {
            return new SoccorsoWebDataLayer(ds);
        } catch (SQLException ex) {
            throw new ServletException("Errore durante l'inizializzazione del data layer SoccorsoWeb", ex);
        }
    }
}
