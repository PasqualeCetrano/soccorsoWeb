package it.univaq.soccorsoweb.application;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import javax.sql.DataSource;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SoccorsoWebTasksListener implements ServletContextListener {

    // rappresenta la dichiarazione di un gestore di azioni programmate nel tempo
    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        // All'avvio di Tomcat, creiamo il motore del Timer (con 1 solo Thread dedicato)
        scheduler = Executors.newSingleThreadScheduledExecutor();

        // Recuperiamo la connessione al database che l'altro Listener ha preparato
        DataSource ds = (DataSource) event.getServletContext().getAttribute("datasource");

        // Definiamo il "Lavoro" vero e proprio che dovrà essere ripetuto
        Runnable puliziaTask = () -> {
            // Usiamo il blocco try-with-resources così il Datalayer si chiude
            // automaticamente
            try (SoccorsoWebDataLayer dl = new SoccorsoWebDataLayer(ds)) {
                dl.init(); // Inizializza i DAO

                // Passando 24, diciamo al DAO di eliminare SOLO le richieste che si
                // trovano nello stato "da convalidare" da PIÙ di 24 ore.
                // Le richieste più recenti (es. create 5 o 15 ore fa) verranno perdonate e
                // mantenute.
                dl.getRichiestaSoccorsoDAO().cancellaRichiesteScadute(24);
                System.out.println("[BACKGROUND TASK] Pulizia richieste 'da convalidare' eseguita.");

            } catch (Exception e) {
                System.err.println("[BACKGROUND ERRORE] Impossibile pulire il database: " + e.getMessage());
            }
        };

        // quando viene effettuata la cancellazione
        // 10 = Attendi 10 minuti all'avvio del server prima di fare il primo giro.
        // 12 * 60 = Ripeti questo giro di pulizia ogni 12 ore (720 minuti).
        scheduler.scheduleAtFixedRate(puliziaTask, 10, 12 * 60, TimeUnit.MINUTES);
        System.out.println("[BACKGROUND TASK] Timer di pulizia programmato con successo.");
    }

    // Quando il server si spegne, fermiamo il timer
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        if (scheduler != null) {
            // andiamo a chiudere il thread dedicato per l'esecuzione in background
            scheduler.shutdownNow();
            System.out.println("[BACKGROUND TASK] Timer di pulizia arrestato.");
        }
    }
}
