package it.univaq.soccorsoweb.controller;

import it.univaq.framework.result.TemplateManagerException;
import it.univaq.framework.result.TemplateResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HomeAdminController extends SoccorsoWebBaseController {

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        try {
            // "Prepariamo il pacco" per FreeMarker (nessun dato specifico del DB in questa
            // pagina)
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
