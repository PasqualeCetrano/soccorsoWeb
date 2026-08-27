package it.univaq.soccorsoweb.controller.operatore;

import it.univaq.framework.data.DataException;
import it.univaq.framework.view.TemplateManagerException;
import it.univaq.framework.view.TemplateResult;
import it.univaq.soccorsoweb.application.SoccorsoWebDataLayer;
import it.univaq.soccorsoweb.controller.SoccorsoWebBaseController;
import it.univaq.soccorsoweb.data.model.Missione;
import it.univaq.soccorsoweb.data.model.Utente;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MissioniOperatoreController extends SoccorsoWebBaseController {

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        try {
            HttpSession s = request.getSession(false);
            if (s != null) {
                int userId = (Integer) s.getAttribute("userid");
                SoccorsoWebDataLayer dl = (SoccorsoWebDataLayer) request.getAttribute("datalayer");
                Utente operatore = dl.getUtenteDAO().getUtente(userId);

                // Dividiamo le missioni in corso da quelle chiuse/storico da controllare se ci
                // sono gia le qyery
                List<Missione> missioni = operatore.getMissioniPartecipate();
                List<Missione> missioniInCorso = new ArrayList<>();
                List<Missione> missioniChiuse = new ArrayList<>();

                for (Missione m : missioni) {
                    if (m.getFine() == null) {
                        missioniInCorso.add(m);
                    } else {
                        missioniChiuse.add(m);
                    }
                }

                request.setAttribute("missioni_in_corso", missioniInCorso);
                request.setAttribute("missioni_chiuse", missioniChiuse);
            }

            request.setAttribute("page_title", "Le Mie Missioni - SoccorsoWeb");

            TemplateResult res = new TemplateResult(getServletContext());
            res.activate("operatore/missioni_operatore.html", request, response);

        } catch (DataException | TemplateManagerException ex) {
            Logger.getLogger(MissioniOperatoreController.class.getName()).log(Level.SEVERE, null, ex);
            handleError(ex, request, response);
        }
    }
}
