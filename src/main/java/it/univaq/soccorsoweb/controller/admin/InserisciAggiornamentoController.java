package it.univaq.soccorsoweb.controller.admin;

import it.univaq.framework.data.DataException;
import it.univaq.framework.view.TemplateManagerException;
import it.univaq.framework.view.TemplateResult;
import it.univaq.soccorsoweb.application.SoccorsoWebDataLayer;
import it.univaq.soccorsoweb.controller.SoccorsoWebBaseController;
import it.univaq.soccorsoweb.data.model.Aggiornamento;
import it.univaq.soccorsoweb.data.model.Missione;
import it.univaq.soccorsoweb.data.model.Partecipa;
import it.univaq.soccorsoweb.data.model.Utente;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InserisciAggiornamentoController extends SoccorsoWebBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response) 
            throws DataException, TemplateManagerException, IOException {
        
        SoccorsoWebDataLayer dl = (SoccorsoWebDataLayer) request.getAttribute("datalayer");
        String idMissioneParam = request.getParameter("id_missione");

        if (idMissioneParam != null && !idMissioneParam.isEmpty()) {
            try {
                int idMissione = Integer.parseInt(idMissioneParam);
                
                // Recuperiamo la missione dal DB
                Missione missione = dl.getMissioneDAO().getMissione(idMissione);

                // L'aggiornamento si può aggiungere solo se la missione esiste ed è in corso (fine nullo)
                if (missione != null && missione.getFine() == null) {
                    request.setAttribute("missione", missione);
                    request.setAttribute("page_title", "Inserisci Aggiornamento - SoccorsoWeb");

                    // Recuperiamo l'utente correntemente loggato per passarlo al form
                    HttpSession s = request.getSession(false);
                    if (s != null && s.getAttribute("userid") != null) {
                        int userId = (Integer) s.getAttribute("userid");
                        Utente utente = dl.getUtenteDAO().getUtente(userId);
                        request.setAttribute("utente", utente);
                    }

                    TemplateResult res = new TemplateResult(getServletContext());
                    res.activate("admin/inserisci_aggiornamento.html", request, response);
                    return;
                }
            } catch (NumberFormatException ex) {
                Logger.getLogger(InserisciAggiornamentoController.class.getName()).log(Level.WARNING, "ID Missione non valido per aggiornamento: " + idMissioneParam, ex);
            }
        }

        // Se errore, torniamo alle missioni
        response.sendRedirect("missioni");
    }

    private void action_inserisci(HttpServletRequest request, HttpServletResponse response) 
            throws DataException, IOException {
        
        SoccorsoWebDataLayer dl = (SoccorsoWebDataLayer) request.getAttribute("datalayer");
        String idMissioneParam = request.getParameter("id_missione");
        String testo = request.getParameter("testo");

        int idMissione = 0;
        if (idMissioneParam != null && !idMissioneParam.isEmpty() && testo != null && !testo.trim().isEmpty()) {
            try {
                idMissione = Integer.parseInt(idMissioneParam);

                // Recuperiamo la missione dal DB
                Missione missione = dl.getMissioneDAO().getMissione(idMissione);

                if (missione != null && missione.getFine() == null) {
                    // Recuperiamo l'utente loggato (autore)
                    HttpSession s = request.getSession(false);
                    Utente utente = null;
                    if (s != null && s.getAttribute("userid") != null) {
                        int userId = (Integer) s.getAttribute("userid");
                        utente = dl.getUtenteDAO().getUtente(userId);
                    }

                    if (utente != null) {
                        // Creazione ed inserimento dell'aggiornamento
                        Aggiornamento agg = dl.getAggiornamentoDAO().createAggiornamento();
                        agg.setTesto(testo);
                        agg.setMissione(missione);
                        agg.setUtente(utente);

                        // Salva nel DB (questo assegna anche il timestamp corrente)
                        dl.getAggiornamentoDAO().storeAggiornamento(agg);

                        // Simulazione invio email a tutti gli operatori della squadra
                        if (missione.getSquadra() != null && missione.getSquadra().getPartecipazioni() != null) {
                            System.out.println("====== SIMULAZIONE INVIO E-MAIL AGGIORNAMENTO ======");
                            for (Partecipa p : missione.getSquadra().getPartecipazioni()) {
                                if (p.getUtente() != null) {
                                    System.out.println("Invio e-mail a: " + p.getUtente().getEmail());
                                    System.out.println("Oggetto: [SoccorsoWeb] Nuovi sviluppi per la Missione #" + idMissione);
                                    System.out.println("Messaggio:\n" + testo);
                                    System.out.println("-------------------------------------------------");
                                }
                            }
                            System.out.println("=================================================");
                        }
                    }
                }
            } catch (Exception ex) {
                Logger.getLogger(InserisciAggiornamentoController.class.getName()).log(Level.SEVERE, "Errore durante il salvataggio dell'aggiornamento", ex);
            }
        }

        // Reindirizziamo l'amministratore ai dettagli della missione
        if (idMissione > 0) {
            response.sendRedirect("info_missione?id_missione=" + idMissione);
        } else {
            response.sendRedirect("missioni");
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            action_inserisci(request, response);
        } else {
            action_default(request, response);
        }
    }
}
