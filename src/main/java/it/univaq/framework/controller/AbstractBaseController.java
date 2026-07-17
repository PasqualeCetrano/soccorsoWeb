package it.univaq.framework.controller;

import it.univaq.framework.data.DataLayer;
import it.univaq.framework.security.SecurityHelpers;
import it.univaq.framework.utils.ServletHelpers;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import javax.sql.DataSource;

/**
 *
 * @author Giuseppe Della Penna
 */
public abstract class AbstractBaseController extends HttpServlet {

    protected abstract void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;

    // create your own datalayer derived class
    protected abstract DataLayer createDataLayer(DataSource ds) throws ServletException;

    // override to init other information to offer to all the servlets
    protected void initRequest(HttpServletRequest request, DataLayer dl) {
        String completeRequestURL = request.getRequestURL()
                + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        request.setAttribute("thispageurl", completeRequestURL);
        request.setAttribute("datalayer", dl);
    }

    // override to enforce your policy and/or change the login url
    protected void accessCheckLoginFailed(HttpServletRequest request, HttpServletResponse response)
            throws UnsupportedEncodingException, IOException {
        String completeRequestURL = request.getRequestURL()
                + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
        response.sendRedirect("login?referrer=" + URLEncoder.encode(completeRequestURL, "UTF-8"));
    }

    protected void accessCheckRolesFailed(HttpServletRequest request, HttpServletResponse response)
            throws UnsupportedEncodingException, IOException {
        handleError("Your roles do not grant access to this resource!", request, response);
    }

    // override to provide your login information in the request
    protected void accessCheckSuccessful(HttpServletRequest request, HttpServletResponse response)
            throws UnsupportedEncodingException, IOException {
        HttpSession s = request.getSession(false); // recupera la sessione per prelevare i dati e inserirli nella
                                                   // richiesta in modo da non toccare gli elementi della sessione che
                                                   // riesdono sul server ma usare esclusivamente gli elementi che ho
                                                   // inserito nella richiesta
        if (s != null) { // se la sessione esiste
            Map<String, Object> li = new HashMap<>(); // contenitore di informazioni login
            request.setAttribute("logininfo", li);
            li.put("session-start-ts", s.getAttribute("session-start-ts"));
            li.put("username", s.getAttribute("username"));
            li.put("userid", s.getAttribute("userid"));
            li.put("roles", s.getAttribute("roles"));
            li.put("ip", s.getAttribute("ip"));
        }
    }

    //////////////////////////////////////////////// Metodo base che prepara le
    //////////////////////////////////////////////// risorse (connessione,
    //////////////////////////////////////////////// sicurezza) per la richiesta e
    //////////////////////////////////////////////// poi delega l'elaborazione al
    //////////////////////////////////////////////// metodo processRequest del
    //////////////////////////////////////////////// controller specifico.
    private void processBaseRequest(HttpServletRequest request, HttpServletResponse response) {
        // check the session data
        HttpSession s = SecurityHelpers.checkSession(request); // controlla la sessione e inserisce tutti gli attributi
                                                               // della sessione
        // creating the datalayer opens the actual (per-request) connection to the
        // shared datasource
        try (DataLayer datalayer = createDataLayer((DataSource) getServletContext().getAttribute("datasource"))) {
            datalayer.init(); // inizializza il datalayer caricando tutti i e i dao
            initRequest(request, datalayer);
            // check the access rules for this resource
            if (hasLoggedAccess(request, response)) { // controlla se la pagina a cui vogliamo accedere è protetta
                if (s != null) { // controlla se l'utente ha fatto il login
                    if (!checkAccessRoles(request, response)) { // controlla se l'utente ha i ruoli per accedere alla
                                                                // pagina
                        accessCheckRolesFailed(request, response); // pagina di errore se l'utente non ha i ruoli per
                                                                   // accedere alla pagina
                        return;
                    }
                } else {
                    accessCheckLoginFailed(request, response);
                    return;
                }
            }
            accessCheckSuccessful(request, response);
            processRequest(request, response);
        } catch (Exception ex) {
            handleError(ex, request, response);
        }
    }

    protected boolean hasLoggedAccess(HttpServletRequest request, HttpServletResponse response)
            throws UnsupportedEncodingException, IOException {
        String uri = request.getRequestURI();
        Pattern protect = (Pattern) getServletContext().getAttribute("protect_pattern"); // recupera tutti gli url
                                                                                         // protetti tramite una regular
                                                                                         // expression
        return protect.matcher(uri).find(); // controlla se l'url della richiesta è tra quelli protetti
    }

    protected boolean checkAccessRoles(HttpServletRequest request, HttpServletResponse response)
            throws UnsupportedEncodingException, IOException {
        HttpSession s = request.getSession(false); // recupera la sessione
        String uri = request.getRequestURI(); // recupera l url della richiesta
        Map<Pattern, String[]> role_access_patterns = (Map<Pattern, String[]>) getServletContext()
                .getAttribute("role_access_patterns"); // recupera la lista degli url con i ruoli che ne hanno accesso
        List<String> allowed_roles = role_access_patterns.entrySet().stream()
                .flatMap((entry) -> ((entry.getKey().matcher(uri).find()) ? Arrays.stream(entry.getValue())
                        : Stream.empty())) // prende solo i ruoli che hanno accesso all url
                .distinct().toList(); // prende solo i ruoli che hanno accesso all url

        return (allowed_roles.isEmpty()
                || (s != null && allowed_roles.stream().filter(((List<String>) s.getAttribute("roles"))::contains)
                        .findAny().isPresent())); // controlla se l'utente ha uno dei ruoli che hanno accesso all url
    }

    // helper to check if the current user has a particular role, useful to further
    // restrict to a role
    // only particular actions of a controller
    protected boolean checkRole(HttpServletRequest request, String role) {
        HttpSession s = request.getSession(false);
        return (s != null && (((List<String>) s.getAttribute("roles")).contains(role)));
    }

    protected void handleError(String message, HttpServletRequest request, HttpServletResponse response) {
        ServletHelpers.handleError(message, request, response, getServletContext());
    }

    protected void handleError(Exception exception, HttpServletRequest request, HttpServletResponse response) {
        ServletHelpers.handleError(exception, request, response, getServletContext());
    }

    protected void handleError(HttpServletRequest request, HttpServletResponse response) {
        ServletHelpers.handleError(request, response, getServletContext());
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processBaseRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processBaseRequest(request, response);
    }

}
