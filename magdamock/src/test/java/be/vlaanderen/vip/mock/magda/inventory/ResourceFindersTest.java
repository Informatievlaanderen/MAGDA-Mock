package be.vlaanderen.vip.mock.magda.inventory;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

class ResourceFindersTest {

    @Test
    void magdaSimulatorIsAlwaysTheSameObjectWhileOpen() throws URISyntaxException, IOException {
        try(var magdaSimulator1 = ResourceFinders.magdaSimulator();
            var magdaSimulator2 = ResourceFinders.magdaSimulator()) {
            assertTrue(magdaSimulator1 == magdaSimulator2);
        }
    }

    @Test
    void magdaSimulatorIsReconstructedAfterClosing() throws URISyntaxException, IOException {
        var magdaSimulator1 = ResourceFinders.magdaSimulator();
        magdaSimulator1.close();

        try(var magdaSimulator2 = ResourceFinders.magdaSimulator()) {
            assertFalse(magdaSimulator1 == magdaSimulator2);
            assertInstanceOf(ClasspathResourceFinder.class, magdaSimulator2); // first see if we have a ClasspathResourceLoader, so that it even makes sense to ask it if it's open...
            assertTrue(((ClasspathResourceFinder) magdaSimulator2).isOpen());
        }
    }
}