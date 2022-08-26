package be.vlaanderen.vip.mock.magda.inventory;

import lombok.SneakyThrows;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TestCasesTest {
    @SneakyThrows
    @Test
    void enumerateResources() {
        listDirectory("magda_simulator/");
    }

    private void listDirectory(String directory) throws IOException {
        List<String> files = IOUtils.readLines(TestCasesTest.class.getClassLoader().getResourceAsStream(directory), StandardCharsets.UTF_8);
        for (var file : files) {
            String extension = FilenameUtils.getExtension(file);
            if (extension.equals("xml") || extension.equals("json") || extension.equals("pdf")) {
                System.out.println("File => " + file);
            } else {
                System.out.println("Dir => " + directory + file + "/");
                listDirectory(directory + file + "/");
            }
        }
    }

    @SneakyThrows
    @Test
    void enumerateResources2() {
        listDirectory2("magda_simulator/");
    }

    private void listDirectory2(String directory) throws IOException {
        var files = TestCasesTest.class.getClassLoader().getResources(directory);
        while (files.hasMoreElements()) {
            var file = files.nextElement();
            String extension = FilenameUtils.getExtension(file.getFile());
            if (extension.equals("xml") || extension.equals("json") || extension.equals("pdf")) {
                System.out.println("File => " + file);
            } else {
                System.out.println("Dir => " + file);
                listDirectory2(file.getFile());
            }
        }
    }
}
