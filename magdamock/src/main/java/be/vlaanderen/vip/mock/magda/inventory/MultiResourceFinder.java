package be.vlaanderen.vip.mock.magda.inventory;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class MultiResourceFinder implements ResourceFinder {
    private final List<ResourceFinder> finders;
    
    MultiResourceFinder(
            List<ResourceFinder> finders) {
        this.finders = finders;
    }

    @Override
    public InputStream loadSimulatorResource(String type, String resource) {
        return finders.stream()
                      .map(finder -> finder.loadSimulatorResource(type, resource))
                      .filter(Objects::nonNull)
                      .findFirst()
                      .orElse(null);
    }

    @Override
    public List<ServiceDirectory> listServicesDirectories(String type) {
        return finders.stream()
                      .map(finder -> finder.listServicesDirectories(type))
                      .flatMap(Collection::stream)
                      .toList();
    }
    
    public static MultiResourceFinder create(ResourceFinder... finders) {
        return create(Arrays.asList(finders));
    }
    
    public static MultiResourceFinder create(List<ResourceFinder> finders) {
        return new MultiResourceFinder(finders);
    }
}
