package be.vlaanderen.vip.mock.magda.inventory;

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

public interface ResourceFinder {
    static final List<String> CASE_FILE_EXTENSION = Arrays.asList(
            "xml", "json", "pdf", "jpg");
    
    InputStream loadSimulatorResource(String type, String resource);
    
    List<ServiceDirectory> listServicesDirectories(String type);
    
    public record ServiceDirectory(File dir) { 
        
        public String service() {
            return dir.getName();
        }
        
        public List<VersionDirectory> versions() {
            return Arrays.asList(dir.listFiles())
                         .stream()
                         .filter(File::isDirectory)
                         .map(VersionDirectory::new)
                         .toList();
        }
        
    }
    
    public record VersionDirectory(File file) {
        
        public String version() {
            return file.getName();
        }
        
        public List<CaseFile> cases() {
            return Arrays.asList(file.listFiles())
                         .stream()
                         .filter(File::isFile)
                         .filter(this::isCaseFile)
                         .map(CaseFile::new)
                         .toList();
        }
        
        private boolean isCaseFile(File file) {
            return CASE_FILE_EXTENSION.contains(FilenameUtils.getExtension(file.getName()));
        }
        
    }
    
    public record CaseFile(File file) {
        
        public String name() {
            return FilenameUtils.getBaseName(file.getName());
        }
        
    }
    
}
