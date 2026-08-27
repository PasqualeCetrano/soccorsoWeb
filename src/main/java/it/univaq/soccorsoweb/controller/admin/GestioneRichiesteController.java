package it.univaq.soccorsoweb.controller.admin;

import it.univaq.framework.data.DataException;
import it.univaq.framework.view.TemplateManagerException;
import it.univaq.framework.view.TemplateResult;
import it.univaq.soccorsoweb.application.SoccorsoWebDataLayer;
import it.univaq.soccorsoweb.controller.SoccorsoWebBaseController;
import it.univaq.soccorsoweb.data.model.RichiestaSoccorso;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

//serve a mostrare all'amministratore tutte le richieste di soccorso

public class GestioneRichiesteController extends SoccorsoWebBaseController {

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        try {
            SoccorsoWebDataLayer dl = (SoccorsoWebDataLayer) request.getAttribute("datalayer");

            // Recuperiamo tutte le emergenze dal database tramite il metodo del DAO!
            List<RichiestaSoccorso> richieste = dl.getRichiestaSoccorsoDAO().getTutteRichiesteSoccorso();

            // passiamo la lista richieste alla vista HTML (FreeMarker)
            request.setAttribute("richieste", richieste);
            request.setAttribute("page_title", "Gestione Richieste - SoccorsoWeb");

            // Attiviamo FreeMarker per caricare la tabella delle richieste
            TemplateResult res = new TemplateResult(getServletContext());
            res.activate("admin/dashboard_richieste.html", request, response);

        } catch (DataException | TemplateManagerException ex) {
            Logger.getLogger(GestioneRichiesteController.class.getName()).log(Level.SEVERE, null, ex);
            handleError(ex, request, response);
        }
    }
}
