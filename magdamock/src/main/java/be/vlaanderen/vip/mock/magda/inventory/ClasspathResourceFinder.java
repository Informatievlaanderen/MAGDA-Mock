package be.vlaanderen.vip.mock.magda.inventory;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

@Slf4j
public class ClasspathResourceFinder extends AbstractResourceFinder {
    private final ResourceLoader resourceLoader;

    ClasspathResourceFinder(String root, Class<?> cls) throws URISyntaxException, IOException {
        this.resourceLoader = ResourceLoader.fromResource(root, cls.getClassLoader());
    }

    @Override
    public InputStream loadSimulatorResource(String type, String resource) {
        var relativePath = "%s/%s".formatted(type, resource);
        if(containsPathTraversal(relativePath)) {
            return null;
        }

        return resourceLoader.getResourceAsStream(relativePath);
    }

    @Override
    public List<ServiceDirectory> listServicesDirectories(String type) {
        if(containsPathTraversal(type)) {
            return Collections.emptyList();
        }

        try(var stream = ClasspathResourceFinder
                .listDirectories(resourceLoader.getResourceAsPath(type))
                .<ServiceDirectory>map(ResourceServiceDirectory::new)
                .sorted(Comparator.comparing(ServiceDirectory::service))) {
            return stream.toList();
        }
    }

    @Override
    public List<CaseFile> listCaseFiles(String type, String subpath) {
        var relativePath = "%s/%s".formatted(type, subpath);
        if(containsPathTraversal(relativePath)) {
            return Collections.emptyList();
        }

        try(var stream = ClasspathResourceFinder
                .listFiles(resourceLoader.getResourceAsPath(relativePath))
                .<CaseFile>map(ResourceCaseFile::new)
                .sorted(Comparator.comparing(CaseFile::name))) {
            return stream.toList();
        }
    }

    public static ClasspathResourceFinder create(String root) throws URISyntaxException, IOException {
        return create(root, ClasspathResourceFinder.class);
    }

    public static ClasspathResourceFinder create(String root, Class<?> cls) throws URISyntaxException, IOException {
        return new ClasspathResourceFinder(root, cls);
    }

    @Override
    public void close() throws IOException {
        resourceLoader.close();
    }

    private record ResourceServiceDirectory(Path path) implements ServiceDirectory {
        
        public String service() {
            return path.getFileName().toString();
        }
        
        public List<VersionDirectory> versions() {
            try(var stream = ClasspathResourceFinder.listDirectories(path)
                    .<VersionDirectory>map(ResourceVersionDirectory::new)
                    .sorted(Comparator.comparing(VersionDirectory::version))) {
                return stream.toList();
            }
        }
    }
    
    public record ResourceVersionDirectory(Path path) implements VersionDirectory {
        
        public String version() {
            return path.getFileName().toString();
        }
        
        public List<CaseFile> cases() {
            try(var stream = ClasspathResourceFinder.listFiles(path)
                    .filter(ClasspathResourceFinder::isCaseFile)
                    .<CaseFile>map(ResourceCaseFile::new)
                    .sorted(Comparator.comparing(CaseFile::name))) {
                return stream.toList();
            }
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
            log.warn("Failed to get child paths for '%s'".formatted(path), e);
            return Stream.empty();
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

    private static boolean isCaseFile(Path path) {
        return CASE_FILE_EXTENSION.contains(FilenameUtils.getExtension(path.getFileName().toString()));
    }
}