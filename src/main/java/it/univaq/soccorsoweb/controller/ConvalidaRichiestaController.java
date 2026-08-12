package it.univaq.soccorsoweb.controller;

import it.univaq.framework.data.DataException;
import it.univaq.soccorsoweb.application.SoccorsoWebDataLayer;
import it.univaq.soccorsoweb.data.model.RichiestaSoccorso;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConvalidaRichiestaController extends SoccorsoWebBaseController {

    private void action_convalida(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        SoccorsoWebDataLayer dl = (SoccorsoWebDataLayer) request.getAttribute("datalayer");
        String token = request.getParameter("token");
        
        try {
            // SICUREZZA E CONVALIDA: 
            // Usiamo la stringa segreta (il Token) letta dall'URL per ritrovare la richiesta esatta nel Database.
            // Non usiamo l'ID numerico (es. id=5) perché un malintenzionato potrebbe indovinarlo e attivare 
            // false richieste (es. provando id=6, id=7). Il token invece è lunghissimo, unico, e lo conosce 
            // solo chi ha fisicamente ricevuto la finta email!
            RichiestaSoccorso richiesta = dl.getRichiestaSoccorsoDAO().getRichiestaByStringaConvalida(token);
            
            if (richiesta != null && "da convalidare".equals(richiesta.getStato())) {
                // Trovata! La attiviamo ufficialmente
                richiesta.setStato("attiva");
                dl.getRichiestaSoccorsoDAO().storeRichiestaSoccorso(richiesta);
                
                // Rimandiamo l'utente alla home con il flag di successo
                response.sendRedirect("homepage?validated=1");
            } else {
                // Token errato o richiesta già validata
                response.sendRedirect("homepage?error=1");
            }
        } catch (DataException ex) {
            Logger.getLogger(ConvalidaRichiestaController.class.getName()).log(Level.SEVERE, "Errore nella convalida della richiesta", ex);
            response.sendRedirect("homepage?error=1");
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Questa Servlet è chiamata dal link nella finta email (metodo GET)
        if (request.getParameter("token") != null) {
            action_convalida(request, response);
        } else {
            // Accesso senza token: lo rimandiamo alla home
            response.sendRedirect("homepage");
        }
    }
}
