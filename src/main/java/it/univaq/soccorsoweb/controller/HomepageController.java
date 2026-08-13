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
            // Usiamo TemplateResult (FreeMarker) per caricare direttamente il file HTML (è
            // come se accendessimo FreeMarker)
            TemplateResult res = new TemplateResult(getServletContext());

            // Esempio: possiamo passare variabili da Java a FreeMarker, ovvero il
            // Controller
            // prende la variabile page_title e gli assegna come valore "SoccorsoWeb - Invia
            // Segnalazione"
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

            // la Servlet dice a FreeMarker quale pagina deve far comparire all'utente,
            // mentre FreeMarker si occupa di modificare le parti dinamiche, ovvero i file
            // HTML
            // all'interno di templates, diventeranno del tipo ftl.html e conterranno delle
            // variabili tra parentesi graffe, come ad esempio la variabile ${page_title},
            // ${richiesta_inviata}, ${richiesta_validata}, ${token_convalida} che verranno
            // sostituite dai valori delle variabili che abbiamo precedentemente impostato
            // nel
            // controller e decidere quale parte della pagina far comparire all'utente
            // (è come se stessimo invocando FreeMarker)
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
        // userid è una variabile che contiene l'identificativo univoco dell'utente,
        // ovvero contiene il valore della sua chiave primaria nel database
        // se un utente non possiede la sessione, vuol dire che non ha neanche fatto il
        // Login, quindi il secondo controllo risulterebbe come una cosa inutile, però è
        // necessario in quanto a volte è possibile che il sistema crei una sessione per
        // l'utente ad esempio per mantenere il suo carrello(come amazon) ecc, quindi
        // possiamo avere che lui ha una sessione ma se non ha effettuato il login e
        // quindi
        // gli dee essere mostrata la pagina pubblica per la richiesta
        if (s == null || s.getAttribute("userid") == null) {
            // Mostra la home pubblica con il form per segnalare un'emergenza
            action_anonymous(request, response);
        } else {
            // L'utente è già loggato! Portalo dentro l'area riservata
            action_logged(request, response);
        }
    }
}
