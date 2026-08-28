package it.univaq.soccorsoweb.controller.admin;

import it.univaq.framework.data.DataException;
import it.univaq.soccorsoweb.application.SoccorsoWebDataLayer;
import it.univaq.soccorsoweb.controller.SoccorsoWebBaseController;
import it.univaq.soccorsoweb.data.model.Materiale;
import it.univaq.soccorsoweb.data.model.Mezzo;
import it.univaq.soccorsoweb.data.model.Missione;
import it.univaq.soccorsoweb.data.model.Partecipa;
import it.univaq.soccorsoweb.data.model.RichiestaSoccorso;
import it.univaq.soccorsoweb.data.model.Squadra;
import it.univaq.soccorsoweb.data.model.Utente;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

//questo controller si attiva quando clicco attiva missione nel riquadro della richiesta
public class SalvaMissioneController extends SoccorsoWebBaseController {

    private void action_salva_missione(HttpServletRequest request, HttpServletResponse response)
            throws DataException, IOException {

        SoccorsoWebDataLayer dl = (SoccorsoWebDataLayer) request.getAttribute("datalayer");

        // Lettura parametri generali della missione
        String idRichiestaParam = request.getParameter("id_richiesta");
        String posizione = request.getParameter("posizione");
        String obiettivo = request.getParameter("obiettivo");

        // Lettura delle risorse selezionate
        String caposquadraParam = request.getParameter("caposquadra");
        String[] operatoriParams = request.getParameterValues("operatori");
        String[] mezziParams = request.getParameterValues("mezzi");
        String[] materialiParams = request.getParameterValues("materiali");

        if (idRichiestaParam != null && !idRichiestaParam.isEmpty() && caposquadraParam != null
                && !caposquadraParam.isEmpty()) {
            try {
                int idRichiesta = Integer.parseInt(idRichiestaParam);
                int idCaposquadra = Integer.parseInt(caposquadraParam);

                // 1. Recupero richiesta originaria dal DB
                RichiestaSoccorso richiesta = dl.getRichiestaSoccorsoDAO().getRichiestaSoccorso(idRichiesta);

                if (richiesta != null) {
                    // 2. Creazione e salvataggio della Missione
                    Missione missione = dl.getMissioneDAO().createMissione();
                    missione.setPosizione(posizione != null ? posizione : "");
                    missione.setObiettivo(obiettivo != null ? obiettivo : "");
                    missione.setInizio(LocalDateTime.now());
                    missione.setRichiestaSoccorso(richiesta);

                    // Questo inserisce il record missione e ne imposta l'ID generato
                    dl.getMissioneDAO().storeMissione(missione);

                    // 3. Creazione e salvataggio della Squadra
                    Squadra squadra = dl.getSquadraDAO().createSquadra();
                    squadra.setMissione(missione);
                    squadra.setCodice("SQ_" + missione.getKey());
                    dl.getSquadraDAO().storeSquadra(squadra);

                    // Colleghiamo la squadra creata alla missione e risalviamo
                    missione.setSquadra(squadra);

                    // 4. Assegnazione Caposquadra alla Squadra (tabella Partecipa)
                    Partecipa caposquadraP = dl.getPartecipaDAO().createPartecipa(); // creo la partecipazione del
                                                                                     // caposquadra
                    caposquadraP.setSquadra(squadra); // associo la partecipazione alla squadra
                    Utente capoU = dl.getUtenteDAO().getUtente(idCaposquadra); // recupero il caposquadra
                    caposquadraP.setUtente(capoU); // associo la partecipazione al caposquadra
                    caposquadraP.setRuolo("caposquadra"); // imposto il ruolo del caposquadra
                    dl.getPartecipaDAO().storePartecipa(caposquadraP); // salvo la partecipazione

                    // 5. Assegnazione degli altri Operatori alla Squadra (tabella Partecipa)
                    if (operatoriParams != null) {
                        for (String opIdStr : operatoriParams) { // operatoriParams contiene gli id di tutti gli
                                                                 // operatori selezionati
                            int opId = Integer.parseInt(opIdStr); // converte l'id dell'operatore in intero
                            // Evitiamo di reinserire il caposquadra come operatore semplice se selezionato
                            // in entrambi
                            if (opId != idCaposquadra) {
                                Partecipa operatoreP = dl.getPartecipaDAO().createPartecipa(); // creo la partecipazione
                                                                                               // dell'operatore
                                operatoreP.setSquadra(squadra); // associo la partecipazione alla squadra
                                Utente opU = dl.getUtenteDAO().getUtente(opId); // recupero l'operatore
                                operatoreP.setUtente(opU); // associo la partecipazione all'operatore
                                operatoreP.setRuolo("operatore"); // imposto il ruolo dell'operatore
                                dl.getPartecipaDAO().storePartecipa(operatoreP); // salvo la partecipazione
                            }
                        }
                    }

                    // 6. Assegnazione dei Mezzi alla Missione (tabella Impiega_Mezzo)
                    if (mezziParams != null) {
                        for (String mezzoIdStr : mezziParams) { // mezziParams contiene gli id di tutti i mezzi
                                                                // selezionati
                            int mezzoId = Integer.parseInt(mezzoIdStr); // converte l'id del mezzo in intero
                            Mezzo mezzo = dl.getMezzoDAO().getMezzo(mezzoId); // recupero il mezzo
                            if (mezzo != null) {
                                dl.getMissioneDAO().storeImpiegaMezzo(missione, mezzo); // associo il mezzo alla
                                                                                        // missione
                            }
                        }
                    }

                    // 7. Assegnazione dei Materiali alla Missione (tabella Impiega_Materiale)
                    if (materialiParams != null) {
                        for (String matIdStr : materialiParams) {
                            int matId = Integer.parseInt(matIdStr);
                            Materiale materiale = dl.getMaterialeDAO().getMateriale(matId);
                            if (materiale != null) {
                                dl.getMissioneDAO().storeImpiegaMateriale(missione, materiale);
                            }
                        }
                    }

                    // 8. infine aggiorniamo lo stato della richiesta originaria su "in_corso" dopo
                    // che tutto è stato creato
                    richiesta.setStato("in_corso");
                    dl.getRichiestaSoccorsoDAO().storeRichiestaSoccorso(richiesta);
                }

            } catch (Exception ex) {
                // In caso di errore, logghiamo l'eccezione
                ex.printStackTrace();
            }
        }

        // Reindirizziamo l'amministratore alla dashboard generale
        response.sendRedirect("richieste");
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            action_salva_missione(request, response);
        } else {
            response.sendRedirect("richieste");
        }
    }
}
