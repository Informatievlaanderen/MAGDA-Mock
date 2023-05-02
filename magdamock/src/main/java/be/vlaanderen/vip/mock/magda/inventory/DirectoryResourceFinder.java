package be.vlaanderen.vip.mock.magda.inventory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
                     .map(ServiceDirectory::new)
                     .toList();
    }
    
    public static DirectoryResourceFinder create(File dir) {
        return new DirectoryResourceFinder(dir);
    }
    
    public static DirectoryResourceFinder create(String path) {
        return new DirectoryResourceFinder(new File(path));
    }

}
