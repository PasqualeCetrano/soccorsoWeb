package it.univaq.soccorsoweb.controller;

import it.univaq.soccorsoweb.application.SoccorsoWebDataLayer;
import it.univaq.soccorsoweb.data.model.Utente;
import it.univaq.framework.data.DataException;
import it.univaq.framework.result.TemplateManagerException;
import it.univaq.framework.result.TemplateResult;
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

            // Se l'utente non sta usando HTTPS, lo avvisiamo tramite una variabile per il
            // template
            if (request.getAttribute("https-redirect") != null) {
                request.setAttribute("https_redirect_url", request.getAttribute("https-redirect"));
            }

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
                // In questo modo peschiamo anche l'hash (la password criptata) salvata a
                // sistema.
                Utente u = dl.getUtenteDAO().getUtenteByEmail(email);

                // 2. Verifichiamo due cose:
                // a) L'utente esiste davvero nel DB (u != null)
                // b) La password in chiaro appena digitata, una volta passata nell'algoritmo
                // PBKDF2,
                // combacia matematicamente con l'hash criptato che abbiamo pescato dal DB.
                // IMPORTANTE: Non facciamo MAI controlli sulle password tramite query SQL (es.
                // WHERE password = ?) per motivi di sicurezza!
                if (u != null && SecurityHelpers.checkPasswordHashPBKDF2(password, u.getPassword())) {

                    // Creiamo la sessione (Cookie) passando l'oggetto utente
                    SecurityHelpers.createSession(request, u);

                    // Smistamento logico (come richiesto) o ritorno al referrer
                    if (request.getParameter("referrer") != null && !request.getParameter("referrer").isEmpty()) {
                        response.sendRedirect(request.getParameter("referrer"));
                    } else {
                        // Se non c'è referrer, controlliamo il ruolo e lo indirizziamo alla dashboard
                        // corretta
                        if (checkRole(request, "amministratore")) {
                            response.sendRedirect("admin/dashboard");
                        } else if (checkRole(request, "operatore")) {
                            response.sendRedirect("operatore/dashboard");
                        } else {
                            response.sendRedirect(""); // Fallback alla home pubblica
                        }
                    }
                    return;
                }
            } catch (DataException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
                Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // Se arriviamo qui, il login ha fallito
        request.setAttribute("login_failed", true);
        action_default(request, response);
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        // Se nella request c'è il parametro 'email' (o un parametro hidden del submit),
        // stiamo inviando i dati di login
        if (request.getParameter("login") != null || request.getParameter("email") != null) {
            action_login(request, response);
        } else {
            // Primo arrivo sulla pagina di login
            // verifica tramite Tomcat all'interno del metodo checkHttps se la connessione è
            // sicura, ovver HTTPS, in caso contrario crea un reindirizzamento HTTPS da
            // applicare al template
            String https_redirect_url = SecurityHelpers.checkHttps(request);
            request.setAttribute("https-redirect", https_redirect_url);
            action_default(request, response);
        }

    }
}
