package it.univaq.soccorsoweb.controller.admin;

import it.univaq.framework.view.TemplateManagerException;
import it.univaq.framework.view.TemplateResult;
import it.univaq.soccorsoweb.controller.SoccorsoWebBaseController;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HomeAdminController extends SoccorsoWebBaseController {

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        try {
            // "Prepariamo il pacco" per FreeMarker (nessun dato specifico del DB in questa
            // pagina)
            // Impostiamo il titolo dinamico che FreeMarker inserirà nel tag <title>
            // dell'HTML
            // in ogni controller c'è questa variabile in maniera tale che freeMarker quando
            // prende il file
            // ftl.html va a sostituire la variabile tra parentesi graffe con il valore che
            // viene
            // impostato nel controller per quella variabile
            // (ovvero il testo che si leggerà nella scheda del browser)
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
