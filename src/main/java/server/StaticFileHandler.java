package server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class StaticFileHandler {

    private final String publicPath;

    public StaticFileHandler(String publicPath) {
        this.publicPath = publicPath;
    }

    public File resolve(String urlPath) {
        if (urlPath.equals("/")) {
            urlPath = "/index.html";
        }

        File arquivo = new File(publicPath + urlPath);
        System.out.println("Mapeando path: " + urlPath + " → " + arquivo.getAbsolutePath());

        return arquivo;
    }

    public byte[] lerArquivo(File arquivo) throws IOException {
        return Files.readAllBytes(arquivo.toPath());
    }
}