package be.vlaanderen.vip.mock.magda.inventory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.io.FilenameUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClasspathResourceFinder implements ResourceFinder {
    private String root;
    private ClassLoader loader;
    
    ClasspathResourceFinder(String root, Class<?> cls) {
        this.root = root;
        this.loader = cls.getClassLoader();
    }

    @Override
    public InputStream loadSimulatorResource(String type, String resource) {
        return loader.getResourceAsStream(root + "/" + type + "/" + resource);
    }

    @Override
    public List<ServiceDirectory> listServicesDirectories(String type) {
        return ClasspathResourceFinder.listDirectories(fromClasspathResource(root + "/" + type))
                                      .<ServiceDirectory>map(path -> new ResourceServiceDirectory(loader, path))
                                      .sorted(Comparator.comparing(ServiceDirectory::service))
                                      .toList();
    }

    public static ClasspathResourceFinder create(String root) {
        return create(root, ClasspathResourceFinder.class);
    }

    public static ClasspathResourceFinder create(String root, Class<?> cls) {
        return new ClasspathResourceFinder(root, cls);
    }
    
    private record ResourceServiceDirectory(ClassLoader loader, Path path) implements ServiceDirectory { 
        
        public String service() {
            return path.getFileName().toString();
        }
        
        public List<VersionDirectory> versions() {
            return ClasspathResourceFinder.listDirectories(path)
                                          .<VersionDirectory>map(path -> new ResourceVersionDirectory(loader, path))
                                          .sorted(Comparator.comparing(VersionDirectory::version))
                                          .toList();
        }
        
    }
    
    public record ResourceVersionDirectory(ClassLoader loader, Path path) implements VersionDirectory {
        
        public String version() {
            return path.getFileName().toString();
        }
        
        public List<CaseFile> cases() {
            return ClasspathResourceFinder.listFiles(path)
                                          .filter(ClasspathResourceFinder::isCaseFile)
                                          .<CaseFile>map(ResourceCaseFile::new)
                                          .sorted(Comparator.comparing(CaseFile::name))
                                          .toList();
        }
        
    }
    
    public record ResourceCaseFile(Path path) implements CaseFile {
        
        public String name() {
            return path.getFileName().toString();
        }
        
    }
    
    private static Stream<Path> getChildPaths(Path path) {
        try {
            return Files.walk(path, 1)
                        .filter(Predicate.not(p -> p.equals(path)));
        }
        catch(Exception e) {
            return Stream.empty();
        }
    }
    
    private Path fromClasspathResource(String resource) {
        try {
            var uri = loader.getResource(resource).toURI();
            return getPath(uri, resource);
        }
        catch (URISyntaxException e) {
            log.warn("Resource '%s' does not have a valid uri syntax".formatted(resource), e);
            return Path.of(resource);
        } catch (IOException e) {
            log.warn("Failed to create a new filesystem for '%'".formatted(resource), e);
            return Path.of(resource);
        }
    }
    
    private static Stream<Path> listDirectories(Path path) {
        return listFromDirectory(path, Files::isDirectory);
    }
    
    private static Stream<Path> listFiles(Path path) {
        return listFromDirectory(path, Files::isRegularFile);
    }
    
    private static Stream<Path> listFromDirectory(Path path, Predicate<Path> fileFilter) {
        return getChildPaths(path)
                .filter(fileFilter);
    }
    
    /**
     * Stuff gets weird when you get resources from a jar within a jar
     * Using FilesSystems fixes that (e.g: magdamock.jar/magda_simulator from inside magdaservice.jar)
     */
    private static Path getPath(URI uri, String resource) throws IOException {
        if(uri.getScheme().equals("jar")) {
            FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
            return fileSystem.getPath(resource);
        }
        return Paths.get(uri);
    }
    
    private static boolean isCaseFile(Path path) {
        return CASE_FILE_EXTENSION.contains(FilenameUtils.getExtension(path.getFileName().toString()));
    }
}
