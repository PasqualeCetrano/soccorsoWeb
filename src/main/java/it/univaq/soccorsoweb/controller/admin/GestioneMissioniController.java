package it.univaq.soccorsoweb.controller.admin;

import it.univaq.framework.data.DataException;
import it.univaq.framework.view.TemplateManagerException;
import it.univaq.framework.view.TemplateResult;
import it.univaq.soccorsoweb.application.SoccorsoWebDataLayer;
import it.univaq.soccorsoweb.controller.SoccorsoWebBaseController;
import it.univaq.soccorsoweb.data.model.Materiale;
import it.univaq.soccorsoweb.data.model.Mezzo;
import it.univaq.soccorsoweb.data.model.Missione;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GestioneMissioniController extends SoccorsoWebBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) 
            throws DataException, TemplateManagerException, IOException {
        
        SoccorsoWebDataLayer dl = (SoccorsoWebDataLayer) request.getAttribute("datalayer");

        String idMezzoParam = request.getParameter("id_mezzo");
        String idMaterialeParam = request.getParameter("id_materiale");

        List<Missione> missioniInCorso = new ArrayList<>();
        List<Missione> missioniChiuse = new ArrayList<>();

        if (idMezzoParam != null && !idMezzoParam.isEmpty()) {
            try {
                int idMezzo = Integer.parseInt(idMezzoParam);
                Mezzo mezzo = dl.getMezzoDAO().getMezzo(idMezzo);
                if (mezzo != null) {
                    List<Missione> tutte = dl.getMissioneDAO().getMissioniByMezzo(mezzo);
                    for (Missione m : tutte) {
                        if (m.getFine() == null) {
                            missioniInCorso.add(m);
                        } else {
                            missioniChiuse.add(m);
                        }
                    }
                    request.setAttribute("filtro_tipo", "Mezzo");
                    request.setAttribute("filtro_nome", mezzo.getNome());
                }
            } catch (NumberFormatException ex) {
                // ignore
            }
        } else if (idMaterialeParam != null && !idMaterialeParam.isEmpty()) {
            try {
                int idMateriale = Integer.parseInt(idMaterialeParam);
                Materiale materiale = dl.getMaterialeDAO().getMateriale(idMateriale);
                if (materiale != null) {
                    List<Missione> tutte = dl.getMissioneDAO().getMissioniByMateriale(materiale);
                    for (Missione m : tutte) {
                        if (m.getFine() == null) {
                            missioniInCorso.add(m);
                        } else {
                            missioniChiuse.add(m);
                        }
                    }
                    request.setAttribute("filtro_tipo", "Materiale");
                    request.setAttribute("filtro_nome", materiale.getNome());
                }
            } catch (NumberFormatException ex) {
                // ignore
            }
        } else {
            // 1. Recupero globale delle missioni (in corso e chiuse)
            missioniInCorso = dl.getMissioneDAO().getMissioniInCorso();
            missioniChiuse = dl.getMissioneDAO().getMissioniChiuse();
        }

        // 2. Passaggio dati al request attribute per FreeMarker
        request.setAttribute("missioni_in_corso", missioniInCorso);
        request.setAttribute("missioni_chiuse", missioniChiuse);
        request.setAttribute("page_title", "Monitoraggio Missioni - SoccorsoWeb");

        // 3. Caricamento del template
        TemplateResult res = new TemplateResult(getServletContext());
        res.activate("admin/missioni.html", request, response);
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        action_default(request, response);
    }
}
