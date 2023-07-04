package be.vlaanderen.vip.mock.magda.inventory;

import java.io.*;
import java.net.URI;
import java.nio.file.Path;

class DirectoryResourceLoader implements ResourceLoader {

    static ResourceLoader fromPath(String rootPath, ClassLoader classLoader) {
        return new DirectoryResourceLoader(rootPath, classLoader);
    }

    private final String rootPath; // XXX it's actually a rootUri
    private final ClassLoader classLoader;

    private DirectoryResourceLoader(String rootPath, ClassLoader classLoader) {
        this.rootPath = rootPath;
        this.classLoader = classLoader;
    }

    @Override
    public InputStream getResourceAsStream(String resource) {
        try {
            return new FileInputStream(new File(URI.create(rootPath + "/" + resource)));
        } catch (FileNotFoundException e) {
            return null;
        }
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