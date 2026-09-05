package it.univaq.soccorsoweb.application;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import javax.sql.DataSource;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SoccorsoWebTasksListener implements ServletContextListener {

    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        // All'avvio di Tomcat, creiamo il motore del Timer (con 1 solo Thread dedicato)
        scheduler = Executors.newSingleThreadScheduledExecutor();

        // Recuperiamo la connessione al database che l'altro Listener ha preparato
        DataSource ds = (DataSource) event.getServletContext().getAttribute("datasource");

        // Definiamo il "Lavoro" vero e proprio che dovrà essere ripetuto
        Runnable puliziaTask = () -> {
            // Usiamo il blocco try-with-resources così il Datalayer si chiude automaticamente
            try (SoccorsoWebDataLayer dl = new SoccorsoWebDataLayer(ds)) {
                dl.init(); // Inizializza i DAO
                
                // Ecco la chiamata al metodo che hai scritto al Passo 1! (Impostiamo 24 ore)
                dl.getRichiestaSoccorsoDAO().cancellaRichiesteScadute(24);
                System.out.println("[BACKGROUND TASK] Pulizia richieste 'da convalidare' eseguita.");
                
            } catch (Exception e) {
                System.err.println("[BACKGROUND ERRORE] Impossibile pulire il database: " + e.getMessage());
            }
        };

        //Avviamo il timer! 
        // si aspettano 10 minuti dall'avvio del server, poi si ripete l'operazione ogni 12 ore"
        scheduler.scheduleAtFixedRate(puliziaTask, 10, 12 * 60, TimeUnit.MINUTES);
        System.out.println("[BACKGROUND TASK] Timer di pulizia programmato con successo.");
    }

    //Quando il server si spegne, fermiamo il timer
    @Override
    public void contextDestroyed(ServletContextEvent event) {
        if (scheduler != null) {
            //andiamo a chiudere il thread dedicato per l'esecuzione in background
            scheduler.shutdownNow();
            System.out.println("[BACKGROUND TASK] Timer di pulizia arrestato.");
        }
    }
}
