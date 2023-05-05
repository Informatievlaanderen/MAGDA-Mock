package be.vlaanderen.vip.mock.magda.inventory;

import java.io.File;

public class ResourceFinders {
    private static final String MAGDA_SIMULATOR = "magda_simulator";

    ResourceFinders() {}
    
    public static ResourceFinder magdaSimulator() {
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
