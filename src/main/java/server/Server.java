package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
            clientSocket.close();

        } catch (IOException e) {
            System.err.println("Erro ao ler requisição: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Server server = new Server(8080);
        server.start();
    }
}
