package it.univaq.soccorsoweb.controller.admin;

import it.univaq.framework.data.DataException;
import it.univaq.framework.view.TemplateManagerException;
import it.univaq.framework.view.TemplateResult;
import it.univaq.soccorsoweb.application.SoccorsoWebDataLayer;
import it.univaq.soccorsoweb.controller.SoccorsoWebBaseController;
import it.univaq.soccorsoweb.data.model.Materiale;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class GestioneMaterialiController extends SoccorsoWebBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) 
            throws DataException, TemplateManagerException, IOException {
        
        SoccorsoWebDataLayer dl = (SoccorsoWebDataLayer) request.getAttribute("datalayer");
        
        // 1. Recupero di tutti i materiali dal database
        List<Materiale> materiali = dl.getMaterialeDAO().getMateriali();

        // 2. Passaggio dati al request attribute per FreeMarker
        request.setAttribute("materiali", materiali);
        request.setAttribute("page_title", "Database Materiali - SoccorsoWeb");

        // 3. Caricamento del template
        TemplateResult res = new TemplateResult(getServletContext());
        res.activate("admin/materiali_database.html", request, response);
    }

    private void action_aggiungi(HttpServletRequest request, HttpServletResponse response) 
            throws DataException, IOException {
        
        SoccorsoWebDataLayer dl = (SoccorsoWebDataLayer) request.getAttribute("datalayer");
        String nome = request.getParameter("nome");
        String descrizione = request.getParameter("descrizione");

        if (nome != null && !nome.trim().isEmpty()) {
            // Creazione e popolamento del nuovo materiale
            Materiale materiale = dl.getMaterialeDAO().createMateriale();
            materiale.setNome(nome.trim());
            materiale.setDescrizione(descrizione != null ? descrizione.trim() : "");
            
            // Salvataggio nel database
            dl.getMaterialeDAO().storeMateriale(materiale);
        }

        // Ritorno alla pagina principale di gestione materiali
        response.sendRedirect("materiali_database");
    }

    private void action_elimina(HttpServletRequest request, HttpServletResponse response) 
            throws DataException, IOException {
        
        SoccorsoWebDataLayer dl = (SoccorsoWebDataLayer) request.getAttribute("datalayer");
        String idMaterialeParam = request.getParameter("id_materiale");

        if (idMaterialeParam != null && !idMaterialeParam.isEmpty()) {
            try {
                int idMateriale = Integer.parseInt(idMaterialeParam);
                
                // Carichiamo il materiale dal DB
                Materiale materiale = dl.getMaterialeDAO().getMateriale(idMateriale);
                
                if (materiale != null) {
                    // Eliminazione del materiale
                    dl.getMaterialeDAO().deleteMateriale(materiale);
                }
            } catch (NumberFormatException ex) {
                // ignore
            }
        }

        // Ritorno alla pagina principale di gestione materiali
        response.sendRedirect("materiali_database");
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = request.getServletPath();
        
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            if (path.contains("AggiungiMaterialeServlet")) {
                action_aggiungi(request, response);
            } else if (path.contains("EliminaMaterialeServlet")) {
                action_elimina(request, response);
            } else {
                response.sendRedirect("materiali_database");
            }
        } else {
            action_default(request, response);
        }
    }
}
