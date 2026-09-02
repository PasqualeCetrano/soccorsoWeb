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

public class HomeAdminController extends SoccorsoWebBaseController {

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        try {
            SoccorsoWebDataLayer dl = (SoccorsoWebDataLayer) request.getAttribute("datalayer");

            // Calcolo del numero di richieste attive in attesa di essere gestite
            int numRichiesteAttive = 0;
            try {
                List<RichiestaSoccorso> richiesteAttive = dl.getRichiestaSoccorsoDAO().getRichiesteSoccorsoByStato("attiva");
                if (richiesteAttive != null) {
                    numRichiesteAttive = richiesteAttive.size();
                }
            } catch (DataException ex) {
                Logger.getLogger(HomeAdminController.class.getName()).log(Level.WARNING,
                        "Errore nel conteggio delle richieste attive per il badge di notifica", ex);
            }
            request.setAttribute("richieste_da_gestire", numRichiesteAttive);

            request.setAttribute("page_title", "Pannello di Controllo - SoccorsoWeb");

            // Attiviamo FreeMarker per caricare il menu con le 5 caselle
            TemplateResult res = new TemplateResult(getServletContext());
            res.activate("admin/home_admin.html", request, response);

        } catch (TemplateManagerException ex) {
            Logger.getLogger(HomeAdminController.class.getName()).log(Level.SEVERE, null, ex);
            handleError(ex, request, response);
        }
    }
}
