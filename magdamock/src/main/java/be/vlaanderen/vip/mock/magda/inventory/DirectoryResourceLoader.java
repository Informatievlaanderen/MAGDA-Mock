package be.vlaanderen.vip.mock.magda.inventory;

import org.springframework.web.util.UriUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

class DirectoryResourceLoader implements ResourceLoader {

    static ResourceLoader fromFileUri(URI rootUri) {
        return new DirectoryResourceLoader(URI.create(rootUri.toString() + "/"));
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
        return new File(rootUri.resolve(UriUtils.encodePath(resource, StandardCharsets.UTF_8)));
    }

    @Override
    public void close() {}
}