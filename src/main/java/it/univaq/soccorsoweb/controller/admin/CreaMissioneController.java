package it.univaq.soccorsoweb.controller.admin;

import it.univaq.framework.data.DataException;
import it.univaq.framework.view.TemplateManagerException;
import it.univaq.framework.view.TemplateResult;
import it.univaq.soccorsoweb.application.SoccorsoWebDataLayer;
import it.univaq.soccorsoweb.controller.SoccorsoWebBaseController;
import it.univaq.soccorsoweb.data.model.RichiestaSoccorso;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreaMissioneController extends SoccorsoWebBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) 
            throws DataException, TemplateManagerException, IOException {
        
        SoccorsoWebDataLayer dl = (SoccorsoWebDataLayer) request.getAttribute("datalayer");
        String idRichiestaParam = request.getParameter("id_richiesta");

        if (idRichiestaParam != null && !idRichiestaParam.isEmpty()) {
            try {
                int idRichiesta = Integer.parseInt(idRichiestaParam);
                RichiestaSoccorso richiesta = dl.getRichiestaSoccorsoDAO().getRichiestaSoccorso(idRichiesta);

                if (richiesta != null) {
                    request.setAttribute("richiesta", richiesta);
                    request.setAttribute("page_title", "Inizializza Nuova Missione - SoccorsoWeb");

                    TemplateResult res = new TemplateResult(getServletContext());
                    res.activate("admin/crea_missione.html", request, response);
                    return;
                }
            } catch (NumberFormatException ex) {
                Logger.getLogger(CreaMissioneController.class.getName()).log(Level.WARNING, "ID Richiesta non valido: " + idRichiestaParam, ex);
            }
        }

        // Se l'ID non è valido o la richiesta non esiste, torniamo al pannello generale
        response.sendRedirect("richieste");
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        action_default(request, response);
    }
}
