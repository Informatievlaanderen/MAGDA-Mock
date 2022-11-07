package be.vlaanderen.vip.mock.magda.inventory;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ResourceFinderTest {

    @SneakyThrows
    @Test
    void findsPersoonServices() {
        var finder = new ResourceFinder(ResourceFinderTest.class);
        var services = finder.listServices("Persoon");
        assertThat(services).hasSize(4);
        assertThat(services).contains(new TestcaseService("GeefBewijs", "02.00.0000"));
        assertThat(services).contains(new TestcaseService("GeefPasfoto", "02.00.0000"));
        assertThat(services).contains(new TestcaseService("GeefPersoon", "02.00.0000"));
        assertThat(services).contains(new TestcaseService("GeefPersoon", "02.02.0000"));
    }

    @SneakyThrows
    @Test
    void findsPersoonBewijzen() {
        var finder = new ResourceFinder(ResourceFinderTest.class);
        var cases = finder.listCases("Persoon", new TestcaseService("GeefBewijs", "02.00.0000"));
        assertThat(cases).hasSize(2);
        assertThat(cases).contains("91010100144.xml");
        assertThat(cases).contains("notfound.xml");

    }

    @SneakyThrows
    @Test
    void findsOndernemingervices() {
        var finder = new ResourceFinder(ResourceFinderTest.class);
        var services = finder.listServices("Onderneming");
        assertThat(services).hasSize(2);
        assertThat(services).contains(new TestcaseService("GeefOnderneming", "02.00.0000"));
        assertThat(services).contains(new TestcaseService("GeefOndernemingVKBO", "02.00.0000"));
    }
}
