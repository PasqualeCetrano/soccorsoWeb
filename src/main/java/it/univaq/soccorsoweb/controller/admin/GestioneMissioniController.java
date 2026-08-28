package it.univaq.soccorsoweb.controller.admin;

import it.univaq.framework.data.DataException;
import it.univaq.framework.view.TemplateManagerException;
import it.univaq.framework.view.TemplateResult;
import it.univaq.soccorsoweb.application.SoccorsoWebDataLayer;
import it.univaq.soccorsoweb.controller.SoccorsoWebBaseController;
import it.univaq.soccorsoweb.data.model.Missione;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class GestioneMissioniController extends SoccorsoWebBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) 
            throws DataException, TemplateManagerException, IOException {
        
        SoccorsoWebDataLayer dl = (SoccorsoWebDataLayer) request.getAttribute("datalayer");

        // 1. Recupero elenchi delle missioni (in corso e chiuse)
        List<Missione> missioniInCorso = dl.getMissioneDAO().getMissioniInCorso();
        List<Missione> missioniChiuse = dl.getMissioneDAO().getMissioniChiuse();

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
