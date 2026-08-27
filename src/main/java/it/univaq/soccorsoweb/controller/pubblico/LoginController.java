package it.univaq.soccorsoweb.controller.pubblico;

import it.univaq.soccorsoweb.application.SoccorsoWebDataLayer;
import it.univaq.soccorsoweb.controller.SoccorsoWebBaseController;
import it.univaq.soccorsoweb.data.model.Utente;
import it.univaq.framework.data.DataException;
import it.univaq.framework.view.TemplateManagerException;
import it.univaq.framework.view.TemplateResult;
import it.univaq.framework.security.SecurityHelpers;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LoginController extends SoccorsoWebBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        try {
            TemplateResult res = new TemplateResult(getServletContext());
            request.setAttribute("page_title", "SoccorsoWeb - Login");

            // Manteniamo il referrer originale per il redirect post-login
            if (request.getParameter("referrer") != null) {
                request.setAttribute("referrer", request.getParameter("referrer"));
            }

            // Attiviamo il template del login
            res.activate("public/login.html", request, response);

        } catch (TemplateManagerException ex) {
            handleError(ex, request, response);
        }
    }

    private void action_login(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        // Leggiamo email e password inviati dal form
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        SoccorsoWebDataLayer dl = (SoccorsoWebDataLayer) request.getAttribute("datalayer");

        if (email != null && !email.isEmpty() && password != null && !password.isEmpty()) {
            try {
                // 1. Recuperiamo TUTTI i dati dell'utente dal Database cercando solo tramite
                // l'email.
                Utente u = dl.getUtenteDAO().getUtenteByEmail(email);

                // 2. Verifichiamo se l'utente esiste e la password combacia con l'hash PBKDF2
                if (u != null && SecurityHelpers.checkPasswordHashPBKDF2(password, u.getPassword())) {

                    // Creiamo la sessione (Cookie) passando l'oggetto utente
                    SecurityHelpers.createSession(request, u);

                    // Smistamento logico o ritorno al referrer
                    if (request.getParameter("referrer") != null && !request.getParameter("referrer").isEmpty()) {
                        response.sendRedirect(request.getParameter("referrer"));
                    } else {
                        // Se non c'è referrer, controlliamo il ruolo e lo indirizziamo alla dashboard
                        // corretta
                        if (checkRole(request, "amministratore")) {
                            response.sendRedirect("admin/home");
                        } else if (checkRole(request, "operatore")) {
                            response.sendRedirect("operatore/dashboard");
                        } else {
                            response.sendRedirect("homepage"); // Fallback alla home pubblica
                        }
                    }
                    return;
                }
            } catch (DataException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // Se arriviamo qui, il login ha fallito
        request.setAttribute("login_failed", true); // serve per generare il messaggio di errore in caso di login
                                                    // fallito
        action_default(request, response);
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // Se nella request c'è il parametro 'email' (o un parametro hidden del submit),
        // stiamo inviando i dati di login
        if (request.getParameter("email") != null) {
            action_login(request, response);
        } else {
            // Primo arrivo sulla pagina di login
            // Verifichiamo se la connessione è sicura (HTTPS). Se non lo è, reindirizziamo
            // direttamente.
            String https_redirect_url = SecurityHelpers.checkHttps(request);
            if (https_redirect_url != null) {
                response.sendRedirect(https_redirect_url);
                return;
            }
            action_default(request, response);
        }

    }
}
