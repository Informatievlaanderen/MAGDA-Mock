package be.vlaanderen.vip.mock.magda.inventory;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DirectoryResourceLoaderTest {

    @Test
    void isNotOpenAfterClosing() throws IOException {
        var finder = DirectoryResourceLoader.fromFileUri(composeRootUri("%s/bar", "resourceloader/simpledir"));

        assertTrue(finder.isOpen());
        finder.close();
        assertFalse(finder.isOpen());
    }

    private URI composeRootUri(String formatString, String resource) {
        return URI.create(formatString.formatted(Objects.requireNonNull(getClass().getClassLoader().getResource(resource))));
    }
}