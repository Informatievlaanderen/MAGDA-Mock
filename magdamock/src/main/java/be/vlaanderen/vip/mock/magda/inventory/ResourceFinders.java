package be.vlaanderen.vip.mock.magda.inventory;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class ResourceFinders {
    private static final String MAGDA_SIMULATOR = "magda_simulator";
    private static ClasspathResourceFinder classpathResourceFinder;

    private static ClasspathResourceFinder getClasspathResourceFinderInstance() throws URISyntaxException, IOException {
        if(classpathResourceFinder == null || !classpathResourceFinder.isOpen()) {
            classpathResourceFinder = ClasspathResourceFinder.create(MAGDA_SIMULATOR);
        }
        return classpathResourceFinder;
    }

    private ResourceFinders() {}
    
    public static ResourceFinder magdaSimulator() throws URISyntaxException, IOException {
        return getClasspathResourceFinderInstance();
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
