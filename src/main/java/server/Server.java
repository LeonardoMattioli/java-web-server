package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {

    private final int port;
    private final HttpRequestParser parser;

    public Server(int port) {
        this.port = port;
        this.parser = new HttpRequestParser();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor rodando na porta " + port + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nova conexão recebida de: " +
                        clientSocket.getInetAddress().getHostAddress());

                HttpRequest request = parser.parse(clientSocket);
                System.out.println(request);

                enviarResposta(clientSocket);
                clientSocket.close();
            }

        } catch (IOException e) {
            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
        }
    }

    private void enviarResposta(Socket clientSocket) {
        try {
            String body = """
                    <html>
                        <body>
                            <h1>Java Web Server</h1>
                            <p>Servidor funcionando!</p>
                        </body>
                    </html>
                    """;

            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
            writer.println("HTTP/1.1 200 OK");
            writer.println("Content-Type: text/html");
            writer.println("Content-Length: " + body.length());
            writer.println("");
            writer.println(body);
            writer.flush();

            System.out.println("Resposta enviada com sucesso!");

        } catch (IOException e) {
            System.err.println("Erro ao enviar resposta: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Server server = new Server(8080);
        server.start();
    }
}
