package it.univaq.soccorsoweb.controller.admin;

import it.univaq.framework.data.DataException;
import it.univaq.soccorsoweb.application.SoccorsoWebDataLayer;
import it.univaq.soccorsoweb.controller.SoccorsoWebBaseController;
import it.univaq.soccorsoweb.data.model.RichiestaSoccorso;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AnnullaRichiestaController extends SoccorsoWebBaseController {

    private void action_annulla(HttpServletRequest request, HttpServletResponse response)
            throws DataException, IOException {
        SoccorsoWebDataLayer dl = (SoccorsoWebDataLayer) request.getAttribute("datalayer");
        String idRichiestaParam = request.getParameter("id_richiesta");

        if (idRichiestaParam != null && !idRichiestaParam.isEmpty()) {
            try {
                int idRichiesta = Integer.parseInt(idRichiestaParam);
                RichiestaSoccorso richiesta = dl.getRichiestaSoccorsoDAO().getRichiestaSoccorso(idRichiesta);

                if (richiesta != null) {
                    // Impostiamo lo stato della richiesta su "annullata"
                    richiesta.setStato("annullata");

                    // Salviamo le modifiche nel database tramite il DAO
                    dl.getRichiestaSoccorsoDAO().storeRichiestaSoccorso(richiesta); // aggiorna la richiesta corrente
                                                                                    // come chiusa
                }
            } catch (NumberFormatException ex) {
                // ID non numerico, ignoriamo l'operazione
            }
        }

        // Reindirizziamo l'amministratore alla dashboard delle richieste
        response.sendRedirect("richieste");
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            action_annulla(request, response);
        } else {
            // Se vi accedono tramite GET, li rimandiamo semplicemente alla dashboard
            response.sendRedirect("richieste");
        }
    }
}
