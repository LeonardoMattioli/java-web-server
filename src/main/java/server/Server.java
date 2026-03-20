package server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private final int port;
    private final HttpRequestParser parser;
    private final StaticFileHandler fileHandler;

    public Server(int port) {
        this.port = port;
        this.parser = new HttpRequestParser();
        this.fileHandler = new StaticFileHandler("public");
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor rodando na porta " + port + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nova conexão recebida de: " +
                        clientSocket.getInetAddress().getHostAddress());

                try {
                    HttpRequest request = parser.parse(clientSocket);
                    System.out.println(request);

                    File arquivo = fileHandler.resolve(request.getPath());

                    if (!fileHandler.arquivoExiste(arquivo)) {
                        System.out.println("Arquivo não encontrado: " + arquivo.getAbsolutePath());
                        HttpResponse response = new HttpResponse(404, "Not Found");
                        response.addHeader("Content-Type", "text/html");
                        response.setBody("""
                                <html>
                                    <body>
                                        <h1>404 - Pagina nao encontrada</h1>
                                        <p>O recurso solicitado nao existe neste servidor.</p>
                                    </body>
                                </html>
                                """);
                        response.send(clientSocket.getOutputStream());
                    } else {
                        byte[] conteudo = fileHandler.lerArquivo(arquivo);
                        String mimeType = fileHandler.detectarMimeType(arquivo);

                        HttpResponse response = new HttpResponse(200, "OK");
                        response.addHeader("Content-Type", mimeType);
                        response.setBody(new String(conteudo));
                        response.send(clientSocket.getOutputStream());
                    }

                } catch (IOException e) {
                    System.err.println("Erro ao processar requisição: " + e.getMessage());
                } finally {
                    clientSocket.close();
                }
            }

        } catch (IOException e) {
            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Server server = new Server(8080);
        server.start();
    }
}
