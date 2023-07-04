package be.vlaanderen.vip.mock.magda.inventory;

import lombok.Getter;

import java.io.*;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Optional;

/**
 * Stuff gets weird when you get resources from a jar within a jar
 * Using FilesSystems fixes that (e.g: magdamock.jar/magda_simulator from inside magdaservice.jar)
 */
class JarResourceLoader implements ResourceLoader {

    private static class NestedFileSystem implements Closeable, AutoCloseable {

        @Getter
        private FileSystem fileSystem;
        private Optional<NestedFileSystem> parent;

        NestedFileSystem(FileSystem fileSystem, NestedFileSystem parent) {
            this.fileSystem = fileSystem;
            this.parent = Optional.of(parent);
        }

        NestedFileSystem(FileSystem fileSystem) {
            this.fileSystem = fileSystem;
            this.parent = Optional.empty();
        }

        @Override
        public void close() throws IOException {
            fileSystem.close();
            if(parent.isPresent()) {
                parent.get().close();
            }
        }
    }

    static ResourceLoader fromJarUri(URI rootUri, ClassLoader classLoader) throws IOException {
        // XXX close the WIP nestedfilesystem if an exception occurs

        var uriParts = rootUri.toString().split("!");
        var jarFileSystem = FileSystems.newFileSystem(URI.create(uriParts[0]), Collections.<String, Object>emptyMap());
        var nestedFileSystem = new NestedFileSystem(jarFileSystem);
        for(var i = 1; i < uriParts.length - 1; i++) {
            // XXX check that it ends on jar
            jarFileSystem = FileSystems.newFileSystem(jarFileSystem.getPath(uriParts[i]), Collections.<String, Object>emptyMap());
            nestedFileSystem = new NestedFileSystem(jarFileSystem, nestedFileSystem);
        }
        var rootDir = uriParts[uriParts.length - 1];

        return new JarResourceLoader(rootUri, classLoader, nestedFileSystem, rootDir);
    }

    // XXX check which attrs are needed
    private final URI rootUri;
    private final ClassLoader classLoader;
    private final NestedFileSystem nestedFileSystem;
    private final String rootDir;

    private JarResourceLoader(URI rootUri, ClassLoader classLoader, NestedFileSystem nestedFileSystem, String rootDir) {
        this.rootUri = rootUri;
        this.classLoader = classLoader;
        this.nestedFileSystem = nestedFileSystem;
        this.rootDir = rootDir;
    }

    @Override
    public InputStream getResourceAsStream(String resource) {
        try {
            return Files.newInputStream(nestedFileSystem.getFileSystem().getPath(rootDir + "/" + resource));
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public Path getResourceAsPath(String resource) {
        return nestedFileSystem.getFileSystem().getPath(rootDir + resource);
    }

    @Override
    public void close() throws IOException {
        nestedFileSystem.close();
    }
}