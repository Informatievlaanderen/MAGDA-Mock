package be.vlaanderen.vip.mock.magda.inventory;

import java.io.*;
import java.net.URI;
import java.nio.file.Path;

class DirectoryResourceLoader implements ResourceLoader {

    static ResourceLoader fromFileUri(URI rootUri) {
        return new DirectoryResourceLoader(rootUri);
    }

    private final URI rootUri;

    private DirectoryResourceLoader(URI rootUri) {
        this.rootUri = rootUri;
    }

    @Override
    public InputStream getResourceAsStream(String resource) {
        try {
            return new FileInputStream(resourceAsFile(resource));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public Path getResourceAsPath(String resource) {
        return resourceAsFile(resource).toPath();
    }

    private File resourceAsFile(String resource) {
        return new File(URI.create(rootUri.toString() + "/" + resource));
    }

    @Override
    public void close() {}
}