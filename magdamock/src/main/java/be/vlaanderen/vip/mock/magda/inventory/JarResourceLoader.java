package be.vlaanderen.vip.mock.magda.inventory;

import jakarta.annotation.Nullable;
import lombok.Getter;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

/**
 * Stuff gets weird when you get resources from a jar within a jar
 * Using FilesSystems fixes that (e.g: magdamock.jar/magda_simulator from inside magdaservice.jar)
 */
class JarResourceLoader implements ResourceLoader {

    private static class NestedFileSystem implements Closeable, AutoCloseable {

        @Getter
        private final FileSystem fileSystem;
        @Nullable
        private final NestedFileSystem parent;

        NestedFileSystem(FileSystem fileSystem, @Nullable NestedFileSystem parent) {
            this.fileSystem = fileSystem;
            this.parent = parent;
        }

        NestedFileSystem(FileSystem fileSystem) {
            this(fileSystem, null);
        }

        @Override
        public void close() throws IOException {
            fileSystem.close();
            if(parent != null) {
                parent.close();
            }
        }
    }

    static ResourceLoader fromJarUri(URI rootUri) throws IOException {
        var uriParts = rootUri.toString().split("!");
        var jarFileSystem = FileSystems.newFileSystem(URI.create(uriParts[0]), Collections.<String, Object>emptyMap());
        var nestedFileSystem = new NestedFileSystem(jarFileSystem);

        try {
            for(var i = 1; i < uriParts.length - 1; i++) {
                jarFileSystem = FileSystems.newFileSystem(jarFileSystem.getPath(uriParts[i]), Collections.<String, Object>emptyMap());
                nestedFileSystem = new NestedFileSystem(jarFileSystem, nestedFileSystem);
            }
            var rootDir = uriParts[uriParts.length - 1];

            return new JarResourceLoader(nestedFileSystem, rootDir);
        } catch(Exception e) {
            jarFileSystem.close();
            nestedFileSystem.close();

            throw e;
        }
    }

    private final NestedFileSystem nestedFileSystem;
    private final String rootDir;

    private JarResourceLoader(NestedFileSystem nestedFileSystem, String rootDir) {
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
        return nestedFileSystem.getFileSystem().getPath(rootDir + "/" + resource);
    }

    @Override
    public void close() throws IOException {
        nestedFileSystem.close();
    }
}