package be.vlaanderen.vip.mock.magda.inventory;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ResourceFinderDeprecatedTest {
//    @InjectMocks
//    private ResourceFinderDeprecated finder;
//    
//    
//    @SneakyThrows
//    @Test
//    void findsPersoonServices() {
//        var services = finder.listServices("Persoon");
//        assertThat(services).hasSize(4);
//        assertThat(services).contains(new TestcaseService("GeefBewijs", "02.00.0000"));
//        assertThat(services).contains(new TestcaseService("GeefPasfoto", "02.00.0000"));
//        assertThat(services).contains(new TestcaseService("GeefPersoon", "02.00.0000"));
//        assertThat(services).contains(new TestcaseService("GeefPersoon", "02.02.0000"));
//    }
//
//    @SneakyThrows
//    @Test
//    void findsPersoonBewijzen() {
//        var cases = finder.listCases("Persoon", new TestcaseService("GeefBewijs", "02.00.0000"));
//        assertThat(cases).hasSize(2);
//        assertThat(cases).contains("91010100144.xml");
//        assertThat(cases).contains("notfound.xml");
//
//    }
//
//    @SneakyThrows
//    @Test
//    void findsOndernemingervices() {
//        var services = finder.listServices("Onderneming");
//        assertThat(services).hasSize(2);
//        assertThat(services).contains(new TestcaseService("GeefOnderneming", "02.00.0000"));
//        assertThat(services).contains(new TestcaseService("GeefOndernemingVKBO", "02.00.0000"));
//    }
//
//    @SneakyThrows
//    @Test
//    void loadSimulatorResource_findsResourcesInMagdaSimulatorDirectory() {
//        var simulatorResource = finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/00071031644.xml");
//
//        assertNotNull(simulatorResource);
//        assertEquals("XE:193:25162:8280", MagdaDocument.fromStream(simulatorResource).getValue("//Ontvanger/Referte"));
//    }
//
//    @SneakyThrows
//    @Test
//    void loadSimulatorResource_returnsNullIfResourceDoesntExist() {
//        var simulatorResource = finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/bogey.xml");
//
//        assertNull(simulatorResource);
//    }
}
