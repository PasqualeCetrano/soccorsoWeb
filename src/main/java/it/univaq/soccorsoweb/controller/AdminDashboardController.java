package it.univaq.soccorsoweb.controller;

import it.univaq.framework.data.DataException;
import it.univaq.framework.result.TemplateManagerException;
import it.univaq.framework.result.TemplateResult;
import it.univaq.soccorsoweb.application.SoccorsoWebDataLayer;
import it.univaq.soccorsoweb.data.model.RichiestaSoccorso;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminDashboardController extends SoccorsoWebBaseController {

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            SoccorsoWebDataLayer dl = (SoccorsoWebDataLayer) request.getAttribute("datalayer");
            
            // Recuperiamo tutte le emergenze dal database tramite il metodo del DAO!
            List<RichiestaSoccorso> richieste = dl.getRichiestaSoccorsoDAO().getTutteRichieste();
            
            // "Prepariamo il pacco" passando la lista alla vista HTML (FreeMarker)
            request.setAttribute("richieste", richieste);
            request.setAttribute("page_title", "Dashboard Amministratore - SoccorsoWeb");

            // Attiviamo FreeMarker
            TemplateResult res = new TemplateResult(getServletContext());
            res.activate("admin/dashboard.html", request, response);
            
        } catch (DataException | TemplateManagerException ex) {
            Logger.getLogger(AdminDashboardController.class.getName()).log(Level.SEVERE, null, ex);
            handleError(ex, request, response);
        }
    }
}
