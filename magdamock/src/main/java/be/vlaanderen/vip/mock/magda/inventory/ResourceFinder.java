package be.vlaanderen.vip.mock.magda.inventory;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public interface ResourceFinder {
    static final List<String> CASE_FILE_EXTENSION = Arrays.asList(
            "xml", "json", "pdf", "jpg");
    
    InputStream loadSimulatorResource(String type, String resource);
    
    List<ServiceDirectory> listServicesDirectories(String type);
    
    public interface ServiceDirectory { 
        
        String service();
        
        List<VersionDirectory> versions();
        
    }
    
    public interface VersionDirectory {
        
        public String version();
        
        public List<CaseFile> cases();
        
    }
    
    public interface CaseFile {
        
        public String name();
        
    }
    
}
