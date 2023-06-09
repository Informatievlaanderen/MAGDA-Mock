package be.vlaanderen.vip.mock.magda.inventory;

import java.io.Closeable;
import java.io.InputStream;
import java.util.List;

public interface ResourceFinder extends Closeable, AutoCloseable {
    List<String> CASE_FILE_EXTENSION = List.of(
            "xml", "json", "pdf", "jpg");
    
    InputStream loadSimulatorResource(String type, String resource);
    
    List<ServiceDirectory> listServicesDirectories(String type);

    List<CaseFile> listCaseFiles(String type, String subpath);

    interface ServiceDirectory {
        
        String service();
        
        List<VersionDirectory> versions();
        
    }
    
    interface VersionDirectory {
        
        String version();
        
        List<CaseFile> cases();
        
    }
    
    interface CaseFile {
        
        String name();
        
    }
    
}
