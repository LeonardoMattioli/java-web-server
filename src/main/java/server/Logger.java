package server;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private static final DateTimeFormatter formatter = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void info(String message) {
        System.out.println("[" + agora() + "] INFO  " + message);
    }

    public static void error(String message) {
        System.err.println("[" + agora() + "] ERROR " + message);
    }

    public static void request(String method, String path, int status, long tempoMs) {
        System.out.println("[" + agora() + "] " + method + " " + path + " → " + status + " (" + tempoMs + "ms)");
    }

    private static String agora() {
        return LocalDateTime.now().format(formatter);
    }
}