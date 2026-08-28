package it.univaq.soccorsoweb.controller.admin;

import it.univaq.framework.data.DataException;
import it.univaq.framework.view.TemplateManagerException;
import it.univaq.framework.view.TemplateResult;
import it.univaq.soccorsoweb.application.SoccorsoWebDataLayer;
import it.univaq.soccorsoweb.controller.SoccorsoWebBaseController;
import it.univaq.soccorsoweb.data.model.Mezzo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class GestioneMezziController extends SoccorsoWebBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) 
            throws DataException, TemplateManagerException, IOException {
        
        SoccorsoWebDataLayer dl = (SoccorsoWebDataLayer) request.getAttribute("datalayer");
        
        // 1. Recupero di tutti i mezzi dal database
        List<Mezzo> mezzi = dl.getMezzoDAO().getMezzi();

        // 2. Passaggio dati al request attribute per FreeMarker
        request.setAttribute("mezzi", mezzi);
        request.setAttribute("page_title", "Database Mezzi - SoccorsoWeb");

        // 3. Caricamento del template
        TemplateResult res = new TemplateResult(getServletContext());
        res.activate("admin/mezzi_database.html", request, response);
    }

    private void action_aggiungi(HttpServletRequest request, HttpServletResponse response) 
            throws DataException, IOException {
        
        SoccorsoWebDataLayer dl = (SoccorsoWebDataLayer) request.getAttribute("datalayer");
        String nome = request.getParameter("nome");
        String descrizione = request.getParameter("descrizione");

        if (nome != null && !nome.trim().isEmpty()) {
            // Creazione e popolamento del nuovo mezzo
            Mezzo mezzo = dl.getMezzoDAO().createMezzo();
            mezzo.setNome(nome.trim());
            mezzo.setDescrizione(descrizione != null ? descrizione.trim() : "");
            
            // Salvataggio nel database
            dl.getMezzoDAO().storeMezzo(mezzo);
        }

        // Ritorno alla pagina principale di gestione mezzi
        response.sendRedirect("mezzi_database");
    }

    private void action_elimina(HttpServletRequest request, HttpServletResponse response) 
            throws DataException, IOException {
        
        SoccorsoWebDataLayer dl = (SoccorsoWebDataLayer) request.getAttribute("datalayer");
        String idMezzoParam = request.getParameter("id_mezzo");

        if (idMezzoParam != null && !idMezzoParam.isEmpty()) {
            try {
                int idMezzo = Integer.parseInt(idMezzoParam);
                
                // Carichiamo il mezzo dal DB
                Mezzo mezzo = dl.getMezzoDAO().getMezzo(idMezzo);
                
                if (mezzo != null) {
                    // Eliminazione del mezzo
                    dl.getMezzoDAO().deleteMezzo(mezzo);
                }
            } catch (NumberFormatException ex) {
                // ignore
            }
        }

        // Ritorno alla pagina principale di gestione mezzi
        response.sendRedirect("mezzi_database");
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = request.getServletPath();
        
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            if (path.contains("AggiungiMezzoServlet")) {
                action_aggiungi(request, response);
            } else if (path.contains("EliminaMezzoServlet")) {
                action_elimina(request, response);
            } else {
                response.sendRedirect("mezzi_database");
            }
        } else {
            action_default(request, response);
        }
    }
}
