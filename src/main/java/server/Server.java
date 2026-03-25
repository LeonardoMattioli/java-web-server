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

        router.register("GET", "/hello", (request, response) -> {
            response.addHeader("Content-Type", "application/json");
            response.setBody("{\"message\": \"Hello, World!\", \"server\": \"Java Web Server\"}");
        });

        router.register("POST", "/echo", (request, response) -> {
            System.out.println("Body recebido no /echo: " + request.getBody());
            response.addHeader("Content-Type", "text/plain");
            response.setBody(request.getBody());
        });
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
        HttpResponse response = null;
        try {
            System.out.println("[" + Thread.currentThread().getName() + "] Processando requisição...");

            HttpRequest request;
            try {
                request = parser.parse(clientSocket);
            } catch (Exception e) {
                System.err.println("Requisição malformada: " + e.getMessage());
                response = ErrorHandler.badRequest("Requisição HTTP malformada.");
                response.send(clientSocket.getOutputStream());
                return;
            }

            System.out.println(request);
            response = new HttpResponse(200, "OK");

            Handler handler = router.resolve(request);

            if (handler != null) {
                try {
                    handler.handle(request, response);
                } catch (Exception e) {
                    System.err.println("Erro no handler: " + e.getMessage());
                    response = ErrorHandler.internalServerError("Erro interno ao processar a requisição.");
                }
            } else {
                File arquivo = fileHandler.resolve(request.getPath());

                if (!fileHandler.arquivoExiste(arquivo)) {
                    System.out.println("Arquivo não encontrado: " + arquivo.getAbsolutePath());
                    response = ErrorHandler.notFound(request.getPath());
                } else {
                    byte[] conteudo = fileHandler.lerArquivo(arquivo);
                    String mimeType = fileHandler.detectarMimeType(arquivo);
                    response.addHeader("Content-Type", mimeType);
                    response.setBody(new String(conteudo));
                }
            }

            response.send(clientSocket.getOutputStream());

        } catch (Exception e) {
            System.err.println("Erro inesperado: " + e.getMessage());
            try {
                response = ErrorHandler.internalServerError("Erro interno inesperado.");
                response.send(clientSocket.getOutputStream());
            } catch (IOException ignored) {}
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
