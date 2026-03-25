package server;

public class ErrorHandler {

    public static HttpResponse badRequest(String mensagem) {
        HttpResponse response = new HttpResponse(400, "Bad Request");
        response.addHeader("Content-Type", "text/html");
        response.setBody("""
                <html>
                    <body>
                        <h1>400 - Bad Request</h1>
                        <p>%s</p>
                    </body>
                </html>
                """.formatted(mensagem));
        return response;
    }

    public static HttpResponse notFound(String path) {
        HttpResponse response = new HttpResponse(404, "Not Found");
        response.addHeader("Content-Type", "text/html");
        response.setBody("""
                <html>
                    <body>
                        <h1>404 - Pagina nao encontrada</h1>
                        <p>O recurso '%s' nao existe neste servidor.</p>
                    </body>
                </html>
                """.formatted(path));
        return response;
    }

    public static HttpResponse internalServerError(String mensagem) {
        HttpResponse response = new HttpResponse(500, "Internal Server Error");
        response.addHeader("Content-Type", "text/html");
        response.setBody("""
                <html>
                    <body>
                        <h1>500 - Internal Server Error</h1>
                        <p>%s</p>
                    </body>
                </html>
                """.formatted(mensagem));
        return response;
    }
}