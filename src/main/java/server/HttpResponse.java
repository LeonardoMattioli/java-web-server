package server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private final int statusCode;
    private final String statusMessage;
    private final Map<String, String> headers;
    private String body;

    public HttpResponse(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.headers = new HashMap<>();
        this.body = "";
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void setBody(String body) {
        this.body = body;
        addHeader("Content-Length", String.valueOf(body.length()));
    }

    public void send(OutputStream out) throws IOException {
        PrintWriter writer = new PrintWriter(out);

        writer.println("HTTP/1.1 " + statusCode + " " + statusMessage);

        headers.forEach((chave, valor) -> writer.println(chave + ": " + valor));

        writer.println("");

        writer.println(body);
        writer.flush();

        System.out.println("Resposta enviada: " + statusCode + " " + statusMessage);
    }
}