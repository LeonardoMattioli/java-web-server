package server;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final int port;
    private final HttpRequestParser parser;
    private final StaticFileHandler fileHandler;
    private final ExecutorService executor;
    private final Router router;

    public Server(int port) {
        this.port = port;
        this.parser = new HttpRequestParser();
        this.fileHandler = new StaticFileHandler("public");
        this.executor = Executors.newFixedThreadPool(10);
        this.router = new Router();
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor rodando na porta " + port + "...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nova conexão recebida de: " +
                        clientSocket.getInetAddress().getHostAddress());

                executor.submit(() -> processarRequisicao(clientSocket));
            }

        } catch (IOException e) {
            System.err.println("Erro ao iniciar o servidor: " + e.getMessage());
        } finally {
            executor.shutdown();
        }
    }

    private void processarRequisicao(Socket clientSocket) {
        try {
            System.out.println("[" + Thread.currentThread().getName() + "] Processando requisição...");

            HttpRequest request = parser.parse(clientSocket);
            System.out.println(request);

            HttpResponse response = new HttpResponse(200, "OK");

            Handler handler = router.resolve(request);

            if (handler != null) {
                handler.handle(request, response);
            } else {
                File arquivo = fileHandler.resolve(request.getPath());

                if (!fileHandler.arquivoExiste(arquivo)) {
                    System.out.println("Arquivo não encontrado: " + arquivo.getAbsolutePath());
                    response = new HttpResponse(404, "Not Found");
                    response.addHeader("Content-Type", "text/html");
                    response.setBody("""
                        <html>
                            <body>
                                <h1>404 - Página não encontrada</h1>
                                <p>O recurso solicitado não existe neste servidor.</p>
                            </body>
                        </html>
                        """);
                } else {
                    byte[] conteudo = fileHandler.lerArquivo(arquivo);
                    String mimeType = fileHandler.detectarMimeType(arquivo);
                    response.addHeader("Content-Type", mimeType);
                    response.setBody(new String(conteudo));
                }
            }

            response.send(clientSocket.getOutputStream());

        } catch (IOException e) {
            System.err.println("Erro ao processar requisição: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Erro ao fechar socket: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server(8080);
        server.start();
    }
}
