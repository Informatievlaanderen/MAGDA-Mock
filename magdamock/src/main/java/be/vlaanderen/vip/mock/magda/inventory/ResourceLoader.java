package be.vlaanderen.vip.mock.magda.inventory;

import java.io.Closeable;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.Objects;

public interface ResourceLoader extends Closeable, AutoCloseable {

    static ResourceLoader fromRootUri(URI rootUri, ClassLoader classLoader) {
        if(rootUri.getScheme().equals("jar")) {
            return JarResourceLoader.fromJarUri(rootUri, classLoader);
        } else if(rootUri.getScheme().equals("file")) {
            return DirectoryResourceLoader.fromPath(rootUri.toString(), classLoader);
        } else {
            throw new IllegalArgumentException("Can't create ResourceLoader from root URI with unsupported scheme: " + rootUri);
        }
    }

    static ResourceLoader fromResource(String rootPath, ClassLoader classLoader) { // XXX test?
        try {
            return fromRootUri(Objects.requireNonNull(classLoader.getResource(rootPath)).toURI(), classLoader);
        } catch(URISyntaxException e) {
            return null; // XXX what to do in this case?
        }
    }

    InputStream getResourceAsStream(String resource);

    Path getResourceAsPath(String resource);
}
