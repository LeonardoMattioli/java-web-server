package server;

import java.util.HashMap;
import java.util.Map;

public class Router {

    private final Map<String, Handler> rotas;

    public Router() {
        this.rotas = new HashMap<>();
    }

    public void register(String method, String path, Handler handler) {
        String chave = method.toUpperCase() + ":" + path;
        rotas.put(chave, handler);
        System.out.println("Rota registrada: " + chave);
    }

    public Handler resolve(HttpRequest request) {
        String chave = request.getMethod().toUpperCase() + ":" + request.getPath();
        return rotas.get(chave);
    }
}