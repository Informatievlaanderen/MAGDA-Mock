package be.vlaanderen.vip.mock.magda.inventory;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Objects;

public interface ResourceLoader extends Closeable, AutoCloseable {

    static ResourceLoader fromRootUri(URI rootUri) throws IOException {
        if(rootUri.getScheme().equals("jar")) {
            return JarResourceLoader.fromJarUri(rootUri);
        } else if(rootUri.getScheme().equals("file")) {
            return DirectoryResourceLoader.fromFileUri(rootUri);
        } else {
            throw new IllegalArgumentException("Can't create ResourceLoader from root URI with unsupported scheme: " + rootUri);
        }
    }

    static ResourceLoader fromResource(String rootPath, ClassLoader classLoader) throws URISyntaxException, IOException {
        return fromRootUri(Objects.requireNonNull(classLoader.getResource(rootPath)).toURI());
    }

    InputStream getResourceAsStream(String resource);

    Path getResourceAsPath(String resource);
}
