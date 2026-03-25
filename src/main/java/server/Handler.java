package server;

import java.io.IOException;

public interface Handler {
    void handle(HttpRequest request, HttpResponse response) throws IOException;
}