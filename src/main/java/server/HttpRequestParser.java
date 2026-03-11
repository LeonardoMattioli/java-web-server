package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestParser {

    public HttpRequest parse(Socket clientSocket) throws IOException {
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(clientSocket.getInputStream())
        );

        String requestLine = reader.readLine();
        String[] partes = requestLine.split(" ");
        String method  = partes[0];
        String path    = partes[1];
        String version = partes[2];

        Map<String, String> headers = new HashMap<>();
        String linha;
        while ((linha = reader.readLine()) != null && !linha.isEmpty()) {
            int separador = linha.indexOf(":");
            String chave = linha.substring(0, separador).trim();
            String valor = linha.substring(separador + 1).trim();
            headers.put(chave, valor);
        }

        String body = "";

        return new HttpRequest(method, path, version, headers, body);
    }
}