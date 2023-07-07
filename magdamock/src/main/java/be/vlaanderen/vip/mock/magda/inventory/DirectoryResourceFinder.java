package be.vlaanderen.vip.mock.magda.inventory;

import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.util.*;

public class DirectoryResourceFinder extends AbstractResourceFinder {
    private final File dir;
    
    DirectoryResourceFinder(File dir) {
        if(!dir.isDirectory()) {
            throw new IllegalArgumentException("'%s' is expected to be a directory".formatted(dir));
        }
        this.dir = dir;
    }

    @Override
    public InputStream loadSimulatorResource(String type, String resource) {
        try {
            var relativePath = "%s/%s".formatted(type, resource);
            if(containsPathTraversal(relativePath)) {
                return null;
            }

            return new FileInputStream(new File(dir, relativePath));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public List<ServiceDirectory> listServicesDirectories(String type) {
        if(containsPathTraversal(type)) {
            return Collections.emptyList();
        }

        var typeDir = new File(dir, type);
        if(typeDir.exists()) {
            return getServiceDirectories(typeDir);
        }
        return Collections.emptyList();
    }

    @Override
    public List<CaseFile> listCaseFiles(String type, String subpath) {
        var relativePath = "%s/%s".formatted(type, subpath);
        if(containsPathTraversal(relativePath)) {
            return Collections.emptyList();
        }

        var typeDir = new File(dir, relativePath);
        if(typeDir.exists()) {
            return getCaseFiles(typeDir);
        }
        return Collections.emptyList();
    }

    @Override
    public void close() {}

    private List<ServiceDirectory> getServiceDirectories(File dir) {
        return Arrays.stream(Objects.requireNonNull(dir.listFiles()))
                     .filter(File::isDirectory)
                     .<ServiceDirectory>map(FileServiceDirectory::new)
                     .sorted(Comparator.comparing(ServiceDirectory::service))
                     .toList();
    }

    private List<CaseFile> getCaseFiles(File dir) {
        return Arrays.stream(Objects.requireNonNull(dir.listFiles()))
                .filter(File::isFile)
                .<CaseFile>map(FileCaseFile::new)
                .sorted(Comparator.comparing(CaseFile::name))
                .toList();
    }
    
    public static DirectoryResourceFinder create(File dir) {
        return new DirectoryResourceFinder(dir);
    }
    
    public static DirectoryResourceFinder create(String path) {
        return new DirectoryResourceFinder(new File(path));
    }
    
    public record FileServiceDirectory(File dir) implements ServiceDirectory { 
        
        public String service() {
            return dir.getName();
        }
        
        public List<VersionDirectory> versions() {
            return Arrays.stream(Objects.requireNonNull(dir.listFiles()))
                         .filter(File::isDirectory)
                         .<VersionDirectory>map(FileVersionDirectory::new)
                         .sorted(Comparator.comparing(VersionDirectory::version))
                         .toList();
        }
        
    }
    
    public record FileVersionDirectory(File file) implements VersionDirectory {
        
        public String version() {
            return file.getName();
        }
        
        public List<CaseFile> cases() {
            return Arrays.stream(Objects.requireNonNull(file.listFiles()))
                         .filter(File::isFile)
                         .filter(this::isCaseFile)
                         .<CaseFile>map(FileCaseFile::new)
                         .sorted(Comparator.comparing(CaseFile::name))
                         .toList();
        }
        
        private boolean isCaseFile(File file) {
            return CASE_FILE_EXTENSION.contains(FilenameUtils.getExtension(file.getName()));
        }
        
    }
    
    public record FileCaseFile(File file) implements CaseFile {
        
        public String name() {
            return FilenameUtils.getBaseName(file.getName());
        }
        
    }
}
