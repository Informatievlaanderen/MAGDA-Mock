package be.vlaanderen.vip.mock.magda.inventory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class ResourceFinders {
    private static final String MAGDA_SIMULATOR = "magda_simulator";

    private ResourceFinders() {}
    
    public static ResourceFinder magdaSimulator() throws URISyntaxException, IOException {
        return ClasspathResourceFinder.create(MAGDA_SIMULATOR);
    }
    
    public static ResourceFinder directory(String directory) {
        return DirectoryResourceFinder.create(directory);
    }
    
    public static ResourceFinder directory(File directory) {
        return DirectoryResourceFinder.create(directory);
    }
    
    public static ResourceFinder combined(ResourceFinder...finders) {
        return MultiResourceFinder.create(finders);
    }
    
}
