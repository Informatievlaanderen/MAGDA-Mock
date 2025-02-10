package be.vlaanderen.vip.mock.magda.client;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Collection;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SimulatorXmlStructuralConstraintTest {

    private static final String BASE_XML = "magda_simulator/";
    private static final Pattern INSZ_XML_PATTERN = Pattern.compile("^\\d{11}\\.xml$");

    @Test
    void allInszXmlsHaveAccordingGeefPersoonXml() {
        var geefPersoonInszFilenames = findInszXmlsInDirectoryRecursively(mainFile(BASE_XML + "Persoon/GeefPersoon"))
                .stream()
                .map(File::getName)
                .toList();

        var allInszFiles = findInszXmlsInDirectoryRecursively(mainFile(BASE_XML));

        var filesWithMissingPerson = allInszFiles.stream()
                .filter(inszFile -> !geefPersoonInszFilenames.contains(inszFile.getName()))
                .toList();

        assertTrue(
                filesWithMissingPerson.isEmpty(),
                () -> "Some of the response XMLs associated with an INSZ number do not contain an according GeefPersoon XML. (in total: %s)%n%s".formatted(
                        filesWithMissingPerson.size(),
                        filesWithMissingPerson.stream()
                                .map(File::getPath)
                                .sorted()
                                .collect(Collectors.joining("\n"))));
    }

    private File mainFile(String path) {
        return new File("src/main/resources/" + path);
    }

    private Collection<File> findInszXmlsInDirectoryRecursively(File directory) {
        return FileUtils.listFiles(
                directory,
                new RegexFileFilter(INSZ_XML_PATTERN),
                DirectoryFileFilter.INSTANCE);
    }
}