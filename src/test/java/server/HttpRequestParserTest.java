package server;

import org.junit.jupiter.api.Test;
import java.io.*;
import java.net.Socket;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;

class HttpRequestParserTest {

    private Socket criarSocketFake(String requisicao) throws IOException {
        InputStream input = new ByteArrayInputStream(
            requisicao.getBytes()
        );
        Socket socket = new Socket() {
            @Override
            public InputStream getInputStream() {
                return input;
            }
        };
        return socket;
    }

    @Test
    @DisplayName("Deve parsear método, path e versão do GET request")
    void deveParsearGetRequest() throws IOException {
        String requisicao = "GET /index.html HTTP/1.1\r\nHost: localhost\r\n\r\n";
        Socket socket = criarSocketFake(requisicao);

        HttpRequestParser parser = new HttpRequestParser();
        HttpRequest request = parser.parse(socket);

        assertEquals("GET", request.getMethod());
        assertEquals("/index.html", request.getPath());
        assertEquals("HTTP/1.1", request.getVersion());
    }

    @Test
    @DisplayName("Deve parsear headers corretamente")
    void deveParsearHeaders() throws IOException {
        String requisicao = "GET / HTTP/1.1\r\nHost: localhost\r\nAccept: text/html\r\n\r\n";
        Socket socket = criarSocketFake(requisicao);

        HttpRequestParser parser = new HttpRequestParser();
        HttpRequest request = parser.parse(socket);

        assertEquals("localhost", request.getHeaders().get("Host"));
        assertEquals("text/html", request.getHeaders().get("Accept"));
    }

    @Test
    @DisplayName("Deve retornar body vazio para requisição GET")
    void deveRetornarBodyVazioParaGet() throws IOException {
        String requisicao = "GET / HTTP/1.1\r\nHost: localhost\r\n\r\n";
        Socket socket = criarSocketFake(requisicao);

        HttpRequestParser parser = new HttpRequestParser();
        HttpRequest request = parser.parse(socket);

        assertEquals("", request.getBody());
    }
}