package it.univaq.soccorsoweb.controller;

import it.univaq.soccorsoweb.application.SoccorsoWebDataLayer;
import it.univaq.soccorsoweb.data.model.RichiestaSoccorso;
import it.univaq.framework.data.DataException;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

// Aggiungiamo questa annotazione per permettere alla Servlet di leggere i file caricati dal form HTML
@MultipartConfig
public class InviaRichiestaController extends SoccorsoWebBaseController {

    private void action_invia_richiesta(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        SoccorsoWebDataLayer dl = (SoccorsoWebDataLayer) request.getAttribute("datalayer");

        try {
            // 1. Estrazione dei dati testuali dal form
            String segnalante = request.getParameter("segnalante");
            String emailSegnalante = request.getParameter("email_segnalante");
            String coordinate = request.getParameter("coordinate");
            String descrizione = request.getParameter("descrizione");

            // 2. Lettura dell'indirizzo IP dell'utente in modo invisibile
            String ip = request.getRemoteAddr();

            // 3. Creazione del modello RichiestaSoccorso tramite il DAO
            RichiestaSoccorso richiesta = dl.getRichiestaSoccorsoDAO().createRichiestaSoccorso();
            richiesta.setSegnalante(segnalante);
            richiesta.setEmail_segnalante(emailSegnalante);
            richiesta.setCoordinate(coordinate);
            richiesta.setDescrizione(descrizione);
            richiesta.setIp(ip);

            // SICUREZZA: Generiamo un token univoco di convalida (una stringa casuale
            // lunghissima).
            // Questo token viene generato e salvato nel DB ora, per poi essere inserito nel
            // link della Finta Email.
            // Permetterà alla "ConvalidaRichiestaController" di ritrovare questa specifica
            // richiesta in modo sicuro.
            richiesta.setTokenConvalida(UUID.randomUUID().toString());

            // 4. Gestione della Foto (Opzionale)
            Part fotoPart = request.getPart("foto");
            if (fotoPart != null && fotoPart.getSize() > 0) {
                try (InputStream is = fotoPart.getInputStream()) {
                    byte[] fotoBytes = is.readAllBytes();
                    richiesta.setFoto(fotoBytes);
                }
            }

            // 5. Salvataggio nel database! (Il DAO imposterà lo stato di default su "da
            // convalidare" e la data attuale)
            dl.getRichiestaSoccorsoDAO().storeRichiestaSoccorso(richiesta);

            // 6. una volta che l'utente ha inviato la richiesta, viene reindirizzato alla
            // homePage pubblica e grazie al + richiesta ecc dopo aver ricaricato la pagina
            // gli comparirà un pop-up per la convalidazione della richiesta (il pop-up

            response.sendRedirect("?success=1&token=" + richiesta.getTokenConvalida());

        } catch (DataException ex) {
            Logger.getLogger(InviaRichiestaController.class.getName()).log(Level.SEVERE,
                    "Errore nel salvataggio della richiesta", ex);
            // In caso di errore critico, rimandiamo alla home con un flag di errore
            response.sendRedirect("?error=1");
        }
    }

    @Override
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Se la richiesta è POST, significa che l'utente ha premuto il tasto Invia del
        // form
        if (request.getMethod().equalsIgnoreCase("POST")) {
            action_invia_richiesta(request, response);
        } else {
            // Se l'utente prova ad accedere a questa Servlet tramite URL (metodo GET), lo
            // rimandiamo alla homepage
            response.sendRedirect("");
        }
    }
}
