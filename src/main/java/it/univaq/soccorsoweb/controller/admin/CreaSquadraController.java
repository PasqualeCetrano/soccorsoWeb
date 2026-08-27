package it.univaq.soccorsoweb.controller.admin;

import it.univaq.framework.data.DataException;
import it.univaq.framework.view.TemplateManagerException;
import it.univaq.framework.view.TemplateResult;
import it.univaq.soccorsoweb.application.SoccorsoWebDataLayer;
import it.univaq.soccorsoweb.controller.SoccorsoWebBaseController;
import it.univaq.soccorsoweb.data.model.Materiale;
import it.univaq.soccorsoweb.data.model.Mezzo;
import it.univaq.soccorsoweb.data.model.Utente;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CreaSquadraController extends SoccorsoWebBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) 
            throws DataException, TemplateManagerException, IOException {
        
        SoccorsoWebDataLayer dl = (SoccorsoWebDataLayer) request.getAttribute("datalayer");
        
        // Riceviamo i dati inseriti nel form dello step precedente
        String idRichiestaParam = request.getParameter("id_richiesta");
        String posizione = request.getParameter("posizione");
        String obiettivo = request.getParameter("obiettivo");

        if (idRichiestaParam != null && !idRichiestaParam.isEmpty()) {
            try {
                // Recuperiamo le risorse disponibili da mostrare nelle caselle di scelta
                List<Utente> operatori = dl.getUtenteDAO().getOperatoriDisponibili();
                List<Mezzo> mezzi = dl.getMezzoDAO().getMezziDisponibili();
                List<Materiale> materiali = dl.getMaterialeDAO().getMaterialiDisponibili();

                // Passiamo le risorse e i dati ereditati al template FreeMarker
                request.setAttribute("id_richiesta", idRichiestaParam);
                request.setAttribute("posizione", posizione != null ? posizione : "");
                request.setAttribute("obiettivo", obiettivo != null ? obiettivo : "");
                
                request.setAttribute("operatori", operatori);
                request.setAttribute("mezzi", mezzi);
                request.setAttribute("materiali", materiali);

                request.setAttribute("page_title", "Configura Squadra e Risorse - SoccorsoWeb");

                TemplateResult res = new TemplateResult(getServletContext());
                res.activate("admin/crea_squadra_materiali_mezzi.html", request, response);
                return;
            } catch (NumberFormatException ex) {
                Logger.getLogger(CreaSquadraController.class.getName()).log(Level.WARNING, "Errore nella compilazione delle risorse per la missione", ex);
            }
        }

        // Se qualcosa va storto, torniamo al pannello generale delle richieste
        response.sendRedirect("richieste");
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        action_default(request, response);
    }
}
