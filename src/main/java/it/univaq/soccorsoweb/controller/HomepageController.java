package it.univaq.soccorsoweb.controller;

import it.univaq.framework.result.TemplateManagerException;
import it.univaq.framework.result.TemplateResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

public class HomepageController extends SoccorsoWebBaseController {

    private void action_anonymous(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        try {
            // Usiamo TemplateResult (FreeMarker) per caricare direttamente il bellissimo
            // file HTML che hai creato!
            TemplateResult res = new TemplateResult(getServletContext());

            // Esempio: possiamo passare variabili da Java a FreeMarker
            request.setAttribute("page_title", "SoccorsoWeb - Invia Segnalazione");

            // Controllo del parametro success per far apparire un messaggio di conferma!
            // confronta le stringhe carattere per carattere, nel caso entrambe contengano
            // 1, allora verranno considerate uguali
            if ("1".equals(request.getParameter("success"))) {
                request.setAttribute("richiesta_inviata", true);
                if (request.getParameter("token") != null) {
                    request.setAttribute("token_convalida", request.getParameter("token"));
                }
            }

            // Controllo del parametro validated per il successo finale
            if ("1".equals(request.getParameter("validated"))) {
                request.setAttribute("richiesta_validata", true);
            }

            // Attiva il template. Il percorso base dei template verrà definito nel web.xml
            // (è come se andasse a chiamare FreeMarker)
            res.activate("public/home_public.html", request, response);

        } catch (TemplateManagerException ex) {
            // Se c'è un errore nel caricare il file HTML, usa la gestione errori del
            // framework
            handleError(ex, request, response);
        }
    }

    private void action_logged(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        // Se l'utente è GIÀ loggato (è un admin o un operatore), la home pubblica non
        // serve a niente.
        // Lo smistiamo direttamente alla sua dashboard operativa in base al ruolo!
        if (checkRole(request, "amministratore")) {
            response.sendRedirect("admin/dashboard"); // Manderà alla Servlet della dashboard admin
        } else if (checkRole(request, "operatore")) {
            response.sendRedirect("operatore/dashboard"); // Manderà alla Servlet della dashboard operatore
        } else {
            response.sendRedirect("logout"); // Se c'è un errore nei ruoli, lo facciamo uscire per sicurezza
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Questo metodo fa da vigile urbano: smista il traffico in base alla presenza
        // del login
        // Recupera la sessione (senza forzarne la creazione)
        HttpSession s = request.getSession(false);

        // Se non c'è sessione, o la sessione non contiene il login dell'utente...
        if (s == null || s.getAttribute("userid") == null) {
            // Mostra la home pubblica con il form per segnalare un'emergenza
            action_anonymous(request, response);
        } else {
            // L'utente è già loggato! Portalo dentro l'area riservata
            action_logged(request, response);
        }
    }
}
