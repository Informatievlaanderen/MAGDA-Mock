package be.vlaanderen.vip.mock.magda.inventory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

public class DirectoryResourceFinder implements ResourceFinder {
    private File dir;
    
    DirectoryResourceFinder(File dir) {
        if(!dir.isDirectory()) {
            throw new IllegalArgumentException("'%s' is expected to be a directory".formatted(dir));
        }
        this.dir = dir;
    }

    @Override
    public InputStream loadSimulatorResource(String type, String resource) {
        try {
            return new FileInputStream(new File(dir, type + "/" + resource));
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    public List<ServiceDirectory> listServicesDirectories(String type) {
        var typeDir = new File(dir, type);
        if(typeDir.exists()) {
            return getServiceDirectories(typeDir);
        }
        return Collections.emptyList();
    }
    
    private List<ServiceDirectory> getServiceDirectories(File dir) {
        return Arrays.stream(dir.listFiles())
                     .filter(File::isDirectory)
                     .<ServiceDirectory>map(FileServiceDirectory::new)
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
            return Arrays.asList(dir.listFiles())
                         .stream()
                         .filter(File::isDirectory)
                         .<VersionDirectory>map(FileVersionDirectory::new)
                         .toList();
        }
        
    }
    
    public record FileVersionDirectory(File file) implements VersionDirectory {
        
        public String version() {
            return file.getName();
        }
        
        public List<CaseFile> cases() {
            return Arrays.asList(file.listFiles())
                         .stream()
                         .filter(File::isFile)
                         .filter(this::isCaseFile)
                         .<CaseFile>map(FileCaseFile::new)
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
