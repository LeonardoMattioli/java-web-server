package server;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class StaticFileHandler {

    private final String publicPath;
    private final Map<String, String> mimeTypes;

    public StaticFileHandler(String publicPath) {
        this.publicPath = publicPath;
        this.mimeTypes = new HashMap<>();
        mimeTypes.put("html", "text/html");
        mimeTypes.put("css",  "text/css");
        mimeTypes.put("js",   "application/javascript");
        mimeTypes.put("png",  "image/png");
        mimeTypes.put("jpg",  "image/jpeg");
        mimeTypes.put("ico",  "image/x-icon");
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

    public String detectarMimeType(File arquivo) {
        String nome = arquivo.getName();
        int ponto = nome.lastIndexOf(".");

        if (ponto == -1) {
            return "application/octet-stream";
        }

        String extensao = nome.substring(ponto + 1).toLowerCase();
        return mimeTypes.getOrDefault(extensao, "application/octet-stream");
    }
}