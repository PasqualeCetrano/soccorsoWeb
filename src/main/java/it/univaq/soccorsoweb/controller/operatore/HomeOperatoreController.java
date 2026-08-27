package it.univaq.soccorsoweb.controller.operatore;

import it.univaq.framework.data.DataException;
import it.univaq.framework.view.TemplateManagerException;
import it.univaq.framework.view.TemplateResult;
import it.univaq.soccorsoweb.application.SoccorsoWebDataLayer;
import it.univaq.soccorsoweb.controller.SoccorsoWebBaseController;
import it.univaq.soccorsoweb.data.model.Utente;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HomeOperatoreController extends SoccorsoWebBaseController {

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        try {
            HttpSession s = request.getSession(false);
            if (s != null) {
                // Recuperiamo l'ID dell'utente dalla sessione che verra usato dal dao per
                // ottenere
                // tutte le informazioni di un utente, come il nome, la patente ecc..
                int userId = (Integer) s.getAttribute("userid");

                // Chiediamo al Database i dati dell'utente tramite il DAO
                SoccorsoWebDataLayer dl = (SoccorsoWebDataLayer) request.getAttribute("datalayer");
                Utente operatore = dl.getUtenteDAO().getUtente(userId);

                // Passiamo l'oggetto operatore al template FreeMarker
                request.setAttribute("operatore", operatore);
            }

            request.setAttribute("page_title", "Area Operatore - SoccorsoWeb");

            // Attiviamo FreeMarker per caricare il pannello operatore
            TemplateResult res = new TemplateResult(getServletContext());
            res.activate("operatore/home_operatore.html", request, response);

        } catch (DataException | TemplateManagerException ex) {
            Logger.getLogger(HomeOperatoreController.class.getName()).log(Level.SEVERE, null, ex);
            handleError(ex, request, response);
        }
    }
}
