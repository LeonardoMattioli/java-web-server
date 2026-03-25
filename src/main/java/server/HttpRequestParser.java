package server;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestParser {

    public HttpRequest parse(Socket clientSocket) throws IOException {
        InputStream input = clientSocket.getInputStream();

        // lê request line
        String requestLine = lerLinha(input);
        String[] partes = requestLine.split(" ");
        String method  = partes[0];
        String path    = partes[1];
        String version = partes[2];

        // lê headers
        Map<String, String> headers = new HashMap<>();
        String linha;
        while (!(linha = lerLinha(input)).isEmpty()) {
            int separador = linha.indexOf(":");
            String chave = linha.substring(0, separador).trim();
            String valor = linha.substring(separador + 1).trim();
            headers.put(chave, valor);
        }

        // lê body se Content-Length estiver presente
        String body = "";
        if (headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            byte[] bodyBytes = new byte[contentLength];
            int bytesLidos = 0;
            while (bytesLidos < contentLength) {
                int resultado = input.read(bodyBytes, bytesLidos, contentLength - bytesLidos);
                if (resultado == -1) break;
                bytesLidos += resultado;
            }
            body = new String(bodyBytes, 0, bytesLidos, StandardCharsets.UTF_8);
            System.out.println("Body recebido: " + body);
        }

        return new HttpRequest(method, path, version, headers, body);
    }

    private String lerLinha(InputStream input) throws IOException {
        StringBuilder sb = new StringBuilder();
        int b;
        while ((b = input.read()) != -1) {
            if (b == '\r') {
                input.read();
                break;
            }
            if (b == '\n') break;
            sb.append((char) b);
        }
        return sb.toString();
    }
}