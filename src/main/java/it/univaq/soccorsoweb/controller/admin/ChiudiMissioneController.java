package it.univaq.soccorsoweb.controller.admin;

import it.univaq.framework.data.DataException;
import it.univaq.framework.view.TemplateManagerException;
import it.univaq.framework.view.TemplateResult;
import it.univaq.soccorsoweb.application.SoccorsoWebDataLayer;
import it.univaq.soccorsoweb.controller.SoccorsoWebBaseController;
import it.univaq.soccorsoweb.data.model.Missione;
import it.univaq.soccorsoweb.data.model.RichiestaSoccorso;
import it.univaq.soccorsoweb.data.model.Utente;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

//questo controller si occupa di far visualizzare la pagina per la chiusura di una missione e anche
//di completare la richiesta di chiusura di aggiornamento con l inserimento dei dati a database
public class ChiudiMissioneController extends SoccorsoWebBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response)
            throws DataException, TemplateManagerException, IOException {

        SoccorsoWebDataLayer dl = (SoccorsoWebDataLayer) request.getAttribute("datalayer");
        String idMissioneParam = request.getParameter("id_missione");

        if (idMissioneParam != null && !idMissioneParam.isEmpty()) {
            try {
                int idMissione = Integer.parseInt(idMissioneParam);

                // Recuperiamo la missione dal DB
                Missione missione = dl.getMissioneDAO().getMissione(idMissione);

                // La missione può essere chiusa solo se esiste ed è ancora in corso (fine è
                // nullo)
                if (missione != null && missione.getFine() == null) {
                    request.setAttribute("missione", missione);
                    request.setAttribute("page_title", "Chiudi Missione #" + idMissione + " - SoccorsoWeb");

                    TemplateResult res = new TemplateResult(getServletContext());
                    res.activate("admin/chiudi_missione.html", request, response);
                    return;
                }
            } catch (NumberFormatException ex) {
                Logger.getLogger(ChiudiMissioneController.class.getName()).log(Level.WARNING,
                        "ID Missione non valido per chiusura: " + idMissioneParam, ex);
            }
        }

        // Se la missione non esiste o è già chiusa, reindirizziamo al monitoraggio
        response.sendRedirect("missioni");
    }

    private void action_chiudi(HttpServletRequest request, HttpServletResponse response)
            throws DataException, IOException {

        SoccorsoWebDataLayer dl = (SoccorsoWebDataLayer) request.getAttribute("datalayer");
        String idMissioneParam = request.getParameter("id_missione");
        String livelloSuccessoParam = request.getParameter("livello_successo");
        String commento = request.getParameter("commento");

        if (idMissioneParam != null && !idMissioneParam.isEmpty() && livelloSuccessoParam != null) {
            try {
                int idMissione = Integer.parseInt(idMissioneParam);
                int livelloSuccesso = Integer.parseInt(livelloSuccessoParam);

                // Recuperiamo la missione dal DB
                Missione missione = dl.getMissioneDAO().getMissione(idMissione);

                if (missione != null && missione.getFine() == null) {
                    // Impostiamo i dati di chiusura
                    missione.setFine(LocalDateTime.now());
                    missione.setLivelloSuccesso(livelloSuccesso);
                    missione.setCommenti(commento != null ? commento : "");

                    // Recuperiamo l'amministratore loggato dalla sessione
                    HttpSession s = request.getSession(false);
                    if (s != null && s.getAttribute("userid") != null) {
                        int userId = (Integer) s.getAttribute("userid");
                        Utente admin = dl.getUtenteDAO().getUtente(userId);
                        missione.setAmministratore(admin);
                    }

                    // Salviamo le modifiche sulla missione nel database
                    dl.getMissioneDAO().storeMissione(missione);

                    // Aggiorniamo anche lo stato della richiesta di soccorso associata su "chiuso"
                    RichiestaSoccorso richiesta = missione.getRichiestaSoccorso();
                    if (richiesta != null) {
                        richiesta.setStato("chiuso");
                        dl.getRichiestaSoccorsoDAO().storeRichiestaSoccorso(richiesta);
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(ChiudiMissioneController.class.getName()).log(Level.SEVERE,
                        "Errore durante la chiusura della missione", ex);
            }
        }

        // Reindirizziamo alla dashboard missioni
        response.sendRedirect("missioni");
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            action_chiudi(request, response);
        } else {
            action_default(request, response);
        }
    }
}
