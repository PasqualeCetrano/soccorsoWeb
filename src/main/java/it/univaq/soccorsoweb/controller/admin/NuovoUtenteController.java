package it.univaq.soccorsoweb.controller.admin;

import it.univaq.framework.data.DataException;
import it.univaq.framework.security.SecurityHelpers;
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
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NuovoUtenteController extends SoccorsoWebBaseController {

    private void action_default(HttpServletRequest request, HttpServletResponse response)
            throws DataException, TemplateManagerException, IOException {

        SoccorsoWebDataLayer dl = (SoccorsoWebDataLayer) request.getAttribute("datalayer");

        // 1. Recupero di tutte le patenti e abilitazioni disponibili a sistema
        List<Patente> patenti = dl.getPatenteDAO().getPatenti();
        List<Abilita> abilitazioni = dl.getAbilitaDAO().getAbilita();

        // 2. Passaggio dati al request attribute per FreeMarker
        request.setAttribute("patenti_sistema", patenti);
        request.setAttribute("abilitazioni_sistema", abilitazioni);
        request.setAttribute("page_title", "Nuovo Utente - SoccorsoWeb");

        // 3. Caricamento del template
        TemplateResult res = new TemplateResult(getServletContext());
        res.activate("admin/nuovo_utente.html", request, response);
    }

    private void action_crea(HttpServletRequest request, HttpServletResponse response)
            throws DataException, IOException {

        SoccorsoWebDataLayer dl = (SoccorsoWebDataLayer) request.getAttribute("datalayer");
        HttpSession session = request.getSession(false);

        String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");
        String codiceFiscale = request.getParameter("codiceFiscale");
        String email = request.getParameter("email");
        String ruolo = request.getParameter("ruolo");

        String dataNascita = request.getParameter("dataNascita");
        String telefono = request.getParameter("telefono");
        String indirizzo = request.getParameter("indirizzo");

        String[] patentiSelezionate = request.getParameterValues("patenti");
        String[] abilitazioniSelezionate = request.getParameterValues("abilitazioni");

        if (nome != null && cognome != null && codiceFiscale != null && email != null && ruolo != null
                && session != null) {
            try {
                // Recupero l'amministratore che sta creando questo utente
                int idAdmin = (Integer) session.getAttribute("userid");
                Utente admin = dl.getUtenteDAO().getUtente(idAdmin);

                // Generazione di una password temporanea sicura (8 caratteri)
                String passwordGenerata = UUID.randomUUID().toString().substring(0, 8);

                // Creazione e popolamento del nuovo utente
                Utente nuovoUtente = dl.getUtenteDAO().createUtente();
                nuovoUtente.setNome(nome.trim());
                nuovoUtente.setCognome(cognome.trim());
                nuovoUtente.setCodiceFiscale(codiceFiscale.trim().toUpperCase());
                nuovoUtente.setEmail(email.trim().toLowerCase());
                // Nel database salviamo l'hash calcolato con PBKDF2
                nuovoUtente.setPassword(SecurityHelpers.getPasswordHashPBKDF2(passwordGenerata));
                nuovoUtente.setTipo(ruolo.equalsIgnoreCase("Amministratore") ? "amministratore" : "operatore");
                nuovoUtente.setAmministratoreCreatore(admin);

                // Impostazione data di nascita
                if (dataNascita != null && !dataNascita.isBlank()) {
                    nuovoUtente.setDataNascita(LocalDate.parse(dataNascita));
                } else {
                    nuovoUtente.setDataNascita(LocalDate.of(1990, 1, 1));
                }

                // Impostazione numero di telefono
                if (telefono != null && !telefono.isBlank()) {
                    nuovoUtente.setTelefono(telefono.trim()); // trim ripulisce il numero dagli spazi inizio-fine
                } else {
                    nuovoUtente.setTelefono("0000000000"); // metto di default se non inserito
                }

                // Impostazione indirizzo
                if (indirizzo != null && !indirizzo.isBlank()) {
                    nuovoUtente.setIndirizzo(indirizzo.trim());
                } else {
                    nuovoUtente.setIndirizzo("Non specificato");
                }

                // 1. Salvataggio Utente nel Database (viene generata e assegnata la chiave
                // primaria)
                dl.getUtenteDAO().storeUtente(nuovoUtente);

                // 2. Associazione Patenti (se presenti)
                if (patentiSelezionate != null && patentiSelezionate.length > 0) {
                    List<Patente> tutteLePatenti = dl.getPatenteDAO().getPatenti();
                    for (String idStr : patentiSelezionate) {
                        int pId = Integer.parseInt(idStr);
                        for (Patente p : tutteLePatenti) {
                            if (p.getKey() == pId) {
                                dl.getPatenteDAO().aggiungiPatenteUtente(nuovoUtente, p);
                                break;
                            }
                        }
                    }
                }

                // 3. Associazione Abilitazioni (se presenti)
                if (abilitazioniSelezionate != null && abilitazioniSelezionate.length > 0) {
                    List<Abilita> tutteLeAbilitazioni = dl.getAbilitaDAO().getAbilita();
                    for (String idStr : abilitazioniSelezionate) {
                        int aId = Integer.parseInt(idStr);
                        for (Abilita a : tutteLeAbilitazioni) {
                            if (a.getKey() == aId) {
                                dl.getAbilitaDAO().aggiungiAbilitaUtente(nuovoUtente, a);
                                break;
                            }
                        }
                    }
                }

                // 4. Simulazione Invio E-Mail all'Utente Creato
                System.out.println("====== SIMULAZIONE INVIO E-MAIL CREAZIONE ACCOUNT ======");
                System.out.println("Invio e-mail a: " + nuovoUtente.getEmail());
                System.out.println("Oggetto: Benvenuto in SoccorsoWeb - Le tue credenziali di accesso");
                System.out.println("Messaggio:");
                System.out.println("Ciao " + nuovoUtente.getNome() + " " + nuovoUtente.getCognome() + ",");
                System.out.println("il tuo account su SoccorsoWeb è stato appena creato con successo.");
                System.out.println("Ecco le tue credenziali per il primo accesso:");
                System.out.println("- Email: " + nuovoUtente.getEmail());
                System.out.println("- Password temporanea: " + passwordGenerata);
                System.out.println("Ti invitiamo a conservarle con cura.");
                System.out.println("-------------------------------------------------");
                System.out.println("=================================================");

            } catch (Exception ex) {
                Logger.getLogger(NuovoUtenteController.class.getName()).log(Level.SEVERE,
                        "Errore durante la creazione dell'utente", ex);
            }
        }

        // Ritorno alla pagina principale di gestione utenti
        response.sendRedirect("utenti");
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String path = request.getServletPath();

        if ("POST".equalsIgnoreCase(request.getMethod()) && path.contains("CreaUtenteServlet")) {
            action_crea(request, response);
        } else {
            action_default(request, response);
        }
    }
}
