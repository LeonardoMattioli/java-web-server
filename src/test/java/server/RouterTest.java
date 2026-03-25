package server;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;

class RouterTest {

    @Test
    @DisplayName("Deve registrar e resolver rota corretamente")
    void deveRegistrarEResolverRota() {
        Router router = new Router();
        Handler handler = (request, response) -> {};

        router.register("GET", "/hello", handler);

        HttpRequest request = new HttpRequest("GET", "/hello", "HTTP/1.1", new HashMap<>(), "");
        Handler resolved = router.resolve(request);

        assertNotNull(resolved);
        assertEquals(handler, resolved);
    }

    @Test
    @DisplayName("Deve retornar null para rota não registrada")
    void deveRetornarNullParaRotaNaoRegistrada() {
        Router router = new Router();

        HttpRequest request = new HttpRequest("GET", "/nao-existe", "HTTP/1.1", new HashMap<>(), "");
        Handler resolved = router.resolve(request);

        assertNull(resolved);
    }
}