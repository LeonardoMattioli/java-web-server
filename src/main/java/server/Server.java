package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final int port;

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor rodando na porta " + port + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nova conexão recebida de: " +
                        clientSocket.getInetAddress().getHostAddress());

                lerRequisicao(clientSocket);
                enviarResposta(clientSocket);
                clientSocket.close();
            }

        } catch (IOException e) {
            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
        }
    }

    private void lerRequisicao(Socket clientSocket) {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream())
            );

            String linha;
            while ((linha = reader.readLine()) != null && !linha.isEmpty()) {
                System.out.println(linha);
            }

            System.out.println("--- fim da requisição ---");

        } catch (IOException e) {
            System.err.println("Erro ao ler requisição: " + e.getMessage());
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
