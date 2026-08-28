package it.univaq.soccorsoweb.controller.pubblico;

import it.univaq.framework.security.SecurityHelpers;
import it.univaq.soccorsoweb.controller.SoccorsoWebBaseController;
import java.io.IOException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class LogoutController extends SoccorsoWebBaseController {

    private void action_logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 1. Distruzione della Sessione
        // Cancella tutti i dati salvati nella RAM del server per questo utente (come
        // l'id, il ruolo, ecc.)
        // e invalida il Cookie sul suo browser. Da questo momento in poi, l'utente è un
        // semplice ospite.
        SecurityHelpers.disposeSession(request);

        // 2. Reindirizzamento dell'utente (Redirect)
        if (request.getParameter("referrer") != null) {
            // Se quando ha cliccato "Esci" gli avevamo passato il parametro "referrer"
            // nell'URL (ovvero l'indirizzo della pagina in cui si trovava prima di uscire),
            // lo rimandiamo esattamente lì. Poiché ora è un ospite, il BaseController molto
            // probabilmente
            // lo caccerà e gli chiederà di fare di nuovo il login, una volta effettuato il
            // login verrà reindirizzato
            // tramite il referrer nell'ultima pagina prima che premesse logout
            response.sendRedirect(request.getParameter("referrer"));
        } else {
            // Se non c'è nessun referrer, lo rimandiamo semplicemente alla radice del sito
            // (ovvero alla nostra Homepage pubblica, gestita dall'HomepageController).
            response.sendRedirect("homepage");
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Questo metodo viene invocato automaticamente dall'AbstractBaseController.
        // A differenza del LoginController, qui non ci sono controlli da fare:
        // chiunque chiami questa pagina (la Servlet di Logout), viene immediatamente
        // disconnesso.
        action_logout(request, response);
    }
}
