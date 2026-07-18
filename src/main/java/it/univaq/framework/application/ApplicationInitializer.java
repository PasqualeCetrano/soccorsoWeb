package it.univaq.framework.application;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import javax.sql.DataSource;

/**
 *
 * @author Giuseppe Della Penna
 * 
 *         Questa classe (ServletContextListener) viene eseguita all'avvio
 *         dell'applicazione per
 *         inizializzare e registrare nel contesto globale (Application Scope)
 *         le risorse condivise, come:
 *         - Il DataSource usato per ottenere le connessioni al database.
 *         - L'espressione regolare globale che definisce gli URL delle pagine
 *         protette.
 *         - La mappa che associa ciascun URL protetto ai relativi ruoli
 *         autorizzati ad accedervi.
 * 
 */
public class ApplicationInitializer implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent event) {

        DataSource ds = null;
        Pattern protect_pattern = null;
        Pattern role_access_pattern = null;
        List<String> all_access_patterns = new ArrayList<>();
        Map<Pattern, String[]> role_access_patterns = new HashMap<>();

        // init protection pattern
        String configuration = event.getServletContext().getInitParameter("security.protect.patterns");
        if (configuration != null && !configuration.isBlank()) {
            String[] rules = configuration.split("\\s*;\\s*");
            for (String rule : rules) {
                String[] patterns_roles = rule.split("\\s*=\\s*");
                String[] patterns = patterns_roles[0].split("\\s*,\\s*");
                all_access_patterns.addAll(Arrays.asList(patterns)); // inserisce la lista di url da proteggere
                if (patterns_roles.length > 1) {
                    role_access_pattern = Pattern
                            .compile(Arrays.stream(patterns).collect(Collectors.joining("$)|(?:", "(?:", "$)")));
                    String[] roles = patterns_roles[1].split("\\s*,\\s*");
                    role_access_patterns.put(role_access_pattern, roles); // mappa la lista degli url con i ruoli che ne
                                                                          // hanno accesso
                }
            }
        }

        if (!all_access_patterns.isEmpty()) {
            protect_pattern = Pattern
                    .compile(all_access_patterns.stream().collect(Collectors.joining("$)|(?:", "(?:", "$)"))); // espressione
                                                                                                               // regolare
                                                                                                               // che
                                                                                                               // raggruppa
                                                                                                               // gli
                                                                                                               // url
                                                                                                               // protetti
        } else {
            protect_pattern = Pattern.compile("a^"); // this regular expression does not match anything!
        }

        // init data source
        try {
            InitialContext ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("java:comp/env/" + event.getServletContext().getInitParameter("data.source"));
        } catch (NamingException ex) {
            Logger.getLogger(ApplicationInitializer.class.getName()).log(Level.SEVERE, null, ex);
        }

        event.getServletContext().setAttribute("protect_pattern", protect_pattern); // espressione regolare che
                                                                                    // raggruppa gli url protett
        event.getServletContext().setAttribute("role_access_patterns", role_access_patterns); // mappa la lista degli
                                                                                              // url con i ruoli che ne
                                                                                              // hanno accesso
        event.getServletContext().setAttribute("datasource", ds);
    }

}
