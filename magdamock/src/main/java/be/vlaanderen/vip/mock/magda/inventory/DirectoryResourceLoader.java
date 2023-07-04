package be.vlaanderen.vip.mock.magda.inventory;

import java.io.*;
import java.net.URI;
import java.nio.file.Path;

class DirectoryResourceLoader implements ResourceLoader {

    static ResourceLoader fromPath(String rootPath, ClassLoader classLoader) {
        return new DirectoryResourceLoader(rootPath, classLoader);
    }

    // XXX check which attrs are needed
    private final String rootPath; // XXX it's actually a rootUri
    private final ClassLoader classLoader;

    private DirectoryResourceLoader(String rootPath, ClassLoader classLoader) {
        this.rootPath = rootPath;
        this.classLoader = classLoader;
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
        return new File(URI.create(rootPath + "/" + resource));
    }

    @Override
    public void close() throws IOException {
        // XXX
    }
}