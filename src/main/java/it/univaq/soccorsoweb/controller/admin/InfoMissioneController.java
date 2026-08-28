package it.univaq.soccorsoweb.controller.admin;

import it.univaq.framework.data.DataException;
import it.univaq.framework.view.TemplateManagerException;
import it.univaq.framework.view.TemplateResult;
import it.univaq.soccorsoweb.application.SoccorsoWebDataLayer;
import it.univaq.soccorsoweb.controller.SoccorsoWebBaseController;
import it.univaq.soccorsoweb.data.model.Missione;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InfoMissioneController extends SoccorsoWebBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) 
            throws DataException, TemplateManagerException, IOException {
        
        SoccorsoWebDataLayer dl = (SoccorsoWebDataLayer) request.getAttribute("datalayer");
        String idMissioneParam = request.getParameter("id_missione");

        if (idMissioneParam != null && !idMissioneParam.isEmpty()) {
            try {
                int idMissione = Integer.parseInt(idMissioneParam);
                
                // 1. Recupero della missione dal DB
                Missione missione = dl.getMissioneDAO().getMissione(idMissione);

                if (missione != null) {
                    // 2. Passaggio dati al request attribute per FreeMarker
                    request.setAttribute("missione", missione);
                    request.setAttribute("page_title", "Dettagli Missione #" + idMissione + " - SoccorsoWeb");

                    // 3. Caricamento del template
                    TemplateResult res = new TemplateResult(getServletContext());
                    res.activate("admin/info_missione.html", request, response);
                    return;
                }
            } catch (NumberFormatException ex) {
                Logger.getLogger(InfoMissioneController.class.getName()).log(Level.WARNING, "ID Missione non valido: " + idMissioneParam, ex);
            }
        }

        // Se la missione non esiste o l'ID è errato, torniamo alla lista delle missioni
        response.sendRedirect("missioni");
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        action_default(request, response);
    }
}
