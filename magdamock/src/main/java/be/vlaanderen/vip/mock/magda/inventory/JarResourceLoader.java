package be.vlaanderen.vip.mock.magda.inventory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Path;

class JarResourceLoader implements ResourceLoader {

    static ResourceLoader fromJarUri(URI rootUri, ClassLoader classLoader) {
        return new JarResourceLoader(rootUri, classLoader);
    }

    private final URI rootUri;
    private final ClassLoader classLoader;

    private JarResourceLoader(URI rootUri, ClassLoader classLoader) {
        this.rootUri = rootUri;
        this.classLoader = classLoader;
    }

    @Override
    public InputStream getResourceAsStream(String resource) {
        return null; // XXX
    }

    @Override
    public Path getResourceAsPath(String resource) {
        return null; // XXX
    }

    @Override
    public void close() throws IOException {
        // XXX
    }
}