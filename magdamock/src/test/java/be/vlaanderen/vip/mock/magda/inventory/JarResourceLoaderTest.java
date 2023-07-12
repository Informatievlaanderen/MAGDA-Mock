package be.vlaanderen.vip.mock.magda.inventory;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

class JarResourceLoaderTest {

    @Test
    void isNotOpenAfterClosing() throws IOException {
        var finder = JarResourceLoader.fromJarUri(composeRootUri("jar:%s!/bar", "resourceloader/simple.jar"));

        assertTrue(finder.isOpen());
        finder.close();
        assertFalse(finder.isOpen());
    }

    private URI composeRootUri(String formatString, String resource) {
        return URI.create(formatString.formatted(Objects.requireNonNull(getClass().getClassLoader().getResource(resource))));
    }
}