package it.univaq.soccorsoweb.controller.operatore;

import it.univaq.framework.data.DataException;
import it.univaq.framework.view.TemplateManagerException;
import it.univaq.framework.view.TemplateResult;
import it.univaq.soccorsoweb.application.SoccorsoWebDataLayer;
import it.univaq.soccorsoweb.controller.SoccorsoWebBaseController;
import it.univaq.soccorsoweb.data.model.Abilita;
import it.univaq.soccorsoweb.data.model.Patente;
import it.univaq.soccorsoweb.data.model.Utente;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AggiungiCompetenzeController extends SoccorsoWebBaseController {

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws Exception {

        HttpSession s = request.getSession(false);
        if (s == null) {
            response.sendRedirect("../login");
            return;
        }

        SoccorsoWebDataLayer dl = (SoccorsoWebDataLayer) request.getAttribute("datalayer");
        int userId = (Integer) s.getAttribute("userid"); // estraggo l id dell utente loggato dalla sessione

        if ("POST".equalsIgnoreCase(request.getMethod())) {
            action_post(request, response, dl, userId); // per inviare i dati
        } else {
            action_get(request, response, dl); // per visualizzare la pagina
        }
    }

    private void action_get(HttpServletRequest request, HttpServletResponse response, SoccorsoWebDataLayer dl)
            throws DataException, TemplateManagerException {

        // Recuperiamo tutte le patenti e le specializzazioni/abilità a sistema per
        // riempire le tendine
        List<Patente> elencoPatenti = dl.getPatenteDAO().getPatenti();
        List<Abilita> elencoAbilita = dl.getAbilitaDAO().getAbilita();

        request.setAttribute("elenco_patenti", elencoPatenti);
        request.setAttribute("elenco_abilita", elencoAbilita);
        request.setAttribute("page_title", "Aggiungi Competenze - SoccorsoWeb");
        
        // Aggiungiamo il parametro per capire quale sezione mostrare (patente o specializzazione)
        String sezioneScelta = request.getParameter("tipo");
        if (sezioneScelta != null) {
            request.setAttribute("sezione_scelta", sezioneScelta);
        }

        TemplateResult res = new TemplateResult(getServletContext());
        res.activate("operatore/utente_aggiungi_patente_abilita.html", request, response);
    }

    private void action_post(HttpServletRequest request, HttpServletResponse response, SoccorsoWebDataLayer dl,
            int userId)
            throws DataException, IOException {

        Utente operatore = dl.getUtenteDAO().getUtente(userId);
        String action = request.getParameter("action"); // viene inviato dalla pagina html come parametro nascosto

        try {
            if ("patente".equals(action)) {
                String tipoPatenteParam = request.getParameter("tipoPatente"); // legge il tipo di patente selezionata
                                                                               // da aggiungere
                if (tipoPatenteParam != null && !tipoPatenteParam.isEmpty()) { // controlla che il parametro non sia
                                                                               // vuoto
                    int idPatente = Integer.parseInt(tipoPatenteParam); // si fa perche l html restituisce una stringa
                    Patente patente = dl.getPatenteDAO().createPatente(); // recupera il dao patente e crea una patente
                    patente.setKey(idPatente);

                    // Associa la patente all'utente
                    dl.getPatenteDAO().aggiungiPatenteUtente(operatore, patente);
                }
            } else if ("specializzazione".equals(action)) {
                String tipoSpecializzazioneParam = request.getParameter("tipoSpecializzazione");
                if (tipoSpecializzazioneParam != null && !tipoSpecializzazioneParam.isEmpty()) {
                    int idSpecializzazione = Integer.parseInt(tipoSpecializzazioneParam);
                    Abilita abilita = dl.getAbilitaDAO().createAbilita();
                    abilita.setKey(idSpecializzazione);

                    // Associa l'abilità/specializzazione all'utente
                    dl.getAbilitaDAO().aggiungiAbilitaUtente(operatore, abilita);
                }
            }
        } catch (DataException ex) {
            // Se l'operatore tenta di aggiungere una competenza che possiede già, ignoriamo
            // il duplicato o gestiamo l'errore
            Logger.getLogger(AggiungiCompetenzeController.class.getName()).log(Level.WARNING,
                    "Competenza già associata o errore nel salvataggio", ex);
        }

        // Reindirizziamo l'operatore alla sua dashboard principale
        response.sendRedirect("dashboard");
    }
}
