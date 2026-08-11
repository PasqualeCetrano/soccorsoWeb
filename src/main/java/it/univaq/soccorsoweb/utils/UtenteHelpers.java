package it.univaq.soccorsoweb.utils;

import java.security.SecureRandom;

public class UtenteHelpers {

    private static final String CARATTERI_PASSWORD = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    private static final int LUNGHEZZA_PASSWORD = 10;

    /**
     * Genera un'email aziendale standard a partire dal nome e dal cognome.
     * Es. "Mario", "Rossi" -> "mario.rossi@soccorsoweb.it"
     */
    public static String generaEmail(String nome, String cognome) {
        if (nome == null || nome.trim().isEmpty() || cognome == null || cognome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome e cognome sono obbligatori per generare l'email");
        }
        
        // Puliamo le stringhe da spazi e le mettiamo in minuscolo
        String nomePulito = nome.trim().toLowerCase().replaceAll("\\s+", "");
        String cognomePulito = cognome.trim().toLowerCase().replaceAll("\\s+", "");
        
        return nomePulito + "." + cognomePulito + "@soccorsoweb.it";
    }

    /**
     * Genera una password casuale sicura di 10 caratteri, contenente lettere, numeri e simboli.
     */
    public static String generaPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(LUNGHEZZA_PASSWORD);
        
        for (int i = 0; i < LUNGHEZZA_PASSWORD; i++) {
            int indiceCasuale = random.nextInt(CARATTERI_PASSWORD.length());
            password.append(CARATTERI_PASSWORD.charAt(indiceCasuale));
        }
        
        return password.toString();
    }
}
