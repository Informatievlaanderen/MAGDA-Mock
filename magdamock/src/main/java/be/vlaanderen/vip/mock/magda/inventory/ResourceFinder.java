package be.vlaanderen.vip.mock.magda.inventory;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ResourceFinder {

    private static final String MAGDA_SIMULATOR = "magda_simulator/";

    private final Class<?> base;

    public InputStream loadSimulatorResource(String type, String testResource) {
        String path = "/" + MAGDA_SIMULATOR + type + "/" + testResource;
        return base.getResourceAsStream(path);
    }

    public List<TestcaseService> listServices(String type) throws IOException {
        var result = new ArrayList<TestcaseService>();
        var services = enumerateDirectories(MAGDA_SIMULATOR + type + "/");
        for (var service : services) {
            var versions = enumerateDirectories(MAGDA_SIMULATOR + type + "/" + service + "/");
            for (var version : versions) {
                result.add(new TestcaseService(service, version));
            }
        }
        return result;
    }

    public List<String> listCases(String type, TestcaseService service) throws IOException {
        return enumerateFiles(MAGDA_SIMULATOR + type + "/" + service.getService() + "/" + service.getVersion() + "/");
    }

    private List<String> enumerate(String directory) throws IOException {
        try (InputStream content = base.getClassLoader().getResourceAsStream(directory)) {
            if (content != null) {
                return IOUtils.readLines(content, StandardCharsets.UTF_8);
            } else {
                return new ArrayList<String>();
            }
        }
    }

    private List<String> enumerateFiles(String directory) throws IOException {
        return enumerate(directory).stream().filter(ResourceFinder::isFile).collect(Collectors.toList());
    }

    private List<String> enumerateDirectories(String directory) throws IOException {
        return enumerate(directory).stream().filter(name -> !isFile(name)).collect(Collectors.toList());
    }

    private static boolean isFile(String filename) {
        String extension = FilenameUtils.getExtension(filename);
        return extension.equals("xml") || extension.equals("json") || extension.equals("txt") || extension.equals("pdf") || extension.equals("jpg");
    }

}
