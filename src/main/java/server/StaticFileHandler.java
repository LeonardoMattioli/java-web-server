package server;

import java.io.File;

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
}