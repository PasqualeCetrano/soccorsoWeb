//questa classe si preoccupa di mostrare la home e di controllare se è stata inoltrata una richiesta con successo , in tal caso mostreera un messaggio di avvenuto successo

package it.univaq.soccorsoweb.controller;

import it.univaq.framework.view.TemplateManagerException;
import it.univaq.framework.view.TemplateResult;
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

            // passiamo il titolo della pagina al template
            request.setAttribute("page_title", "SoccorsoWeb - Invia Segnalazione");

            // Controllo del parametro success per far apparire un messaggio di conferma!
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

            // Controllo del parametro error (proveniente da inviarichiestacontroller) per
            // far apparire un messaggio di errore flood!
            if ("flood".equals(request.getParameter("error"))) {
                request.setAttribute("errore_flood", true);
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

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Mostra sempre la home pubblica con il form per segnalare un'emergenza
        action_anonymous(request, response);
    }
}
