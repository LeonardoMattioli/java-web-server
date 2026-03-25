package server;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {

    private final int statusCode;
    private final String statusMessage;
    private final Map<String, String> headers;
    private byte[] bodyBytes;

    public HttpResponse(int statusCode, String statusMessage) {
        this.statusCode = statusCode;
        this.statusMessage = statusMessage;
        this.headers = new HashMap<>();
        this.bodyBytes = new byte[0];
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void setBody(String body) {
        this.bodyBytes = body.getBytes(StandardCharsets.UTF_8);
        addHeader("Content-Length", String.valueOf(this.bodyBytes.length));
    }

    public void send(OutputStream out) throws IOException {
        StringBuilder cabecalho = new StringBuilder();

        cabecalho.append("HTTP/1.1 ").append(statusCode).append(" ").append(statusMessage).append("\r\n");

        headers.forEach((chave, valor) ->
                cabecalho.append(chave).append(": ").append(valor).append("\r\n")
        );

        cabecalho.append("\r\n");

        out.write(cabecalho.toString().getBytes(StandardCharsets.UTF_8));
        out.write(bodyBytes);
        out.flush();

        Logger.info("Resposta enviada: " + statusCode + " " + statusMessage);
    }

    public int getStatusCode() {
        return statusCode;
    }
}