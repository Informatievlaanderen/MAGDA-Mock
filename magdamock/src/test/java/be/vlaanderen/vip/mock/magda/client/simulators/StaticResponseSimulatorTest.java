package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.exception.MagdaSendFailed;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class StaticResponseSimulatorTest {

    private MagdaDocument makeBewijsAanvraagRequest(String insz) {
        var request = mock(MagdaDocument.class);
        when(request.getValue("//Verzoek/Context/Naam")).thenReturn("GeefBewijs");
        when(request.getValue("//Verzoek/Context/Versie")).thenReturn("02.00.0000");
        when(request.getValue("//INSZ")).thenReturn("00071031644");

        return request;
    }

    @Test
    @SneakyThrows
    void respondsWithStaticResource() {
        var simulator = new StaticResponseSimulator("Persoon", "//INSZ");
        var request = makeBewijsAanvraagRequest("00071031644");

        var response = simulator.send(request);

        assertEquals("2014070108135743208300001H", response.getValue("//Bewijs/Leverancier/Bewijsreferte"));
    }

    @Nested
    class FinderBehaviorTests {

        private InputStream documentWithPropValue(String value) {
            return new ByteArrayInputStream(("<prop>" + value + "</prop>").getBytes());
        }

        @Test
        @SneakyThrows
        void finderLooksForTargetDocument_AtItsExpectedLocation() {
            var finder = mock(ResourceFinder.class);
            var mockDocumentA = documentWithPropValue("foo");
            var mockDocumentB = documentWithPropValue("bar");
            var mockDocumentC = documentWithPropValue("baz");
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/00071031644.xml")).thenReturn(mockDocumentA);
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/notfound.xml")).thenReturn(mockDocumentB);
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/success.xml")).thenReturn(mockDocumentC);

            var simulator = new StaticResponseSimulator("Persoon", "//INSZ");
            simulator.setFinder(finder);

            var response = simulator.send(makeBewijsAanvraagRequest("00071031644"));

            assertNotNull(response);
            assertEquals("foo", response.getValue("//prop"));
        }

        @Test
        @SneakyThrows
        void finderLooksForNotFoundDocument_IfTargetDocumentIsNotThere() {
            var finder = mock(ResourceFinder.class);
            var mockDocumentA = documentWithPropValue("foo");
            var mockDocumentB = documentWithPropValue("bar");
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/notfound.xml")).thenReturn(mockDocumentA);
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/success.xml")).thenReturn(mockDocumentB);

            var simulator = new StaticResponseSimulator("Persoon", "//INSZ");
            simulator.setFinder(finder);

            var response = simulator.send(makeBewijsAanvraagRequest("00071031644"));

            assertNotNull(response);
            assertEquals("foo", response.getValue("//prop"));
        }

        @Test
        @SneakyThrows
        void finderLooksForSuccessDocument_IfNeitherTargetNorNotFoundDocumentAreThere() {
            var finder = mock(ResourceFinder.class);
            var mockDocument = documentWithPropValue("foo");
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/notfound.xml")).thenReturn(null);
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/success.xml")).thenReturn(mockDocument);

            var simulator = new StaticResponseSimulator("Persoon", "//INSZ");
            simulator.setFinder(finder);

            var response = simulator.send(makeBewijsAanvraagRequest("00071031644"));

            assertNotNull(response);
            assertEquals("foo", response.getValue("//prop"));
        }

        @Test
        @SneakyThrows
        void finderLooksForTargetDocument_OnLowestLevelFirst() {
            var finder = mock(ResourceFinder.class);
            var mockDocumentA = documentWithPropValue("foo");
            var mockDocumentB = documentWithPropValue("bar");
            var mockDocumentC = documentWithPropValue("baz");
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/00071031644.xml")).thenReturn(mockDocumentA);
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/00071031644.xml")).thenReturn(mockDocumentB);
            when(finder.loadSimulatorResource("Persoon", "00071031644.xml")).thenReturn(mockDocumentC);

            var simulator = new StaticResponseSimulator("Persoon", "//INSZ");
            simulator.setFinder(finder);

            var response = simulator.send(makeBewijsAanvraagRequest("00071031644"));

            assertNotNull(response);
            assertEquals("foo", response.getValue("//prop"));
        }

        @Test
        @SneakyThrows
        void finderLooksForTargetDocument_OnHigherLevel() {
            var finder = mock(ResourceFinder.class);
            var mockDocumentA = documentWithPropValue("foo");
            var mockDocumentB = documentWithPropValue("bar");
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/00071031644.xml")).thenReturn(mockDocumentA);
            when(finder.loadSimulatorResource("Persoon", "00071031644.xml")).thenReturn(mockDocumentB);

            var simulator = new StaticResponseSimulator("Persoon", "//INSZ");
            simulator.setFinder(finder);

            var response = simulator.send(makeBewijsAanvraagRequest("00071031644"));

            assertNotNull(response);
            assertEquals("foo", response.getValue("//prop"));
        }

        @Test
        @SneakyThrows
        void finderLooksForTargetDocument_OnHighestLevel() {
            var finder = mock(ResourceFinder.class);
            var mockDocument = documentWithPropValue("foo");
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource("Persoon", "00071031644.xml")).thenReturn(mockDocument);

            var simulator = new StaticResponseSimulator("Persoon", "//INSZ");
            simulator.setFinder(finder);

            var response = simulator.send(makeBewijsAanvraagRequest("00071031644"));

            assertNotNull(response);
            assertEquals("foo", response.getValue("//prop"));
        }

        @Test
        @SneakyThrows
        void finderLooksForTargetDocument_OnLevelsDeeperThanServiceAndVersion() {
            var finder = mock(ResourceFinder.class);
            var mockDocumentA = documentWithPropValue("foo");
            var mockDocumentB = documentWithPropValue("bar");
            var mockDocumentC = documentWithPropValue("baz");
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/deep/reallydeep/00071031644.xml")).thenReturn(mockDocumentA);
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/deep/00071031644.xml")).thenReturn(mockDocumentB);
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/00071031644.xml")).thenReturn(mockDocumentC);

            var simulator = new StaticResponseSimulator("Persoon", "//LV1", "//LV2", "//INSZ");
            simulator.setFinder(finder);

            var request = makeBewijsAanvraagRequest("00071031644");
            when(request.getValue("//LV1")).thenReturn("deep");
            when(request.getValue("//LV2")).thenReturn("reallydeep");
            var response = simulator.send(request);

            assertNotNull(response);
            assertEquals("foo", response.getValue("//prop"));
        }

        @Test
        @SneakyThrows
        void finderLooksNotFoundDocument_OnLowestLevelFirst() {
            var finder = mock(ResourceFinder.class);
            var mockDocumentA = documentWithPropValue("foo");
            var mockDocumentB = documentWithPropValue("bar");
            var mockDocumentC = documentWithPropValue("baz");
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource("Persoon", "00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/notfound.xml")).thenReturn(mockDocumentA);
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/notfound.xml")).thenReturn(mockDocumentB);
            when(finder.loadSimulatorResource("Persoon", "notfound.xml")).thenReturn(mockDocumentC);

            var simulator = new StaticResponseSimulator("Persoon", "//INSZ");
            simulator.setFinder(finder);

            var response = simulator.send(makeBewijsAanvraagRequest("00071031644"));

            assertNotNull(response);
            assertEquals("foo", response.getValue("//prop"));
        }

        @Test
        @SneakyThrows
        void finderLooksForNotFoundDocument_OnHigherLevel() {
            var finder = mock(ResourceFinder.class);
            var mockDocumentA = documentWithPropValue("foo");
            var mockDocumentB = documentWithPropValue("bar");
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource("Persoon", "00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/notfound.xml")).thenReturn(null);
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/notfound.xml")).thenReturn(mockDocumentA);
            when(finder.loadSimulatorResource("Persoon", "notfound.xml")).thenReturn(mockDocumentB);

            var simulator = new StaticResponseSimulator("Persoon", "//INSZ");
            simulator.setFinder(finder);

            var response = simulator.send(makeBewijsAanvraagRequest("00071031644"));

            assertNotNull(response);
            assertEquals("foo", response.getValue("//prop"));
        }

        @Test
        @SneakyThrows
        void finderLooksForNotFoundDocument_OnHighestLevel() {
            var finder = mock(ResourceFinder.class);
            var mockDocument = documentWithPropValue("foo");
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource("Persoon", "00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/notfound.xml")).thenReturn(null);
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/notfound.xml")).thenReturn(null);
            when(finder.loadSimulatorResource("Persoon", "notfound.xml")).thenReturn(mockDocument);

            var simulator = new StaticResponseSimulator("Persoon", "//INSZ");
            simulator.setFinder(finder);

            var response = simulator.send(makeBewijsAanvraagRequest("00071031644"));

            assertNotNull(response);
            assertEquals("foo", response.getValue("//prop"));
        }

        @Test
        @SneakyThrows
        void finderLooksForTargetEverywhereBeforeLookingForNotFound() {
            var finder = mock(ResourceFinder.class);
            var mockDocumentA = documentWithPropValue("foo");
            var mockDocumentB = documentWithPropValue("bar");
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/00071031644.xml")).thenReturn(mockDocumentA);
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/notfound.xml")).thenReturn(mockDocumentB);

            var simulator = new StaticResponseSimulator("Persoon", "//INSZ");
            simulator.setFinder(finder);

            var response = simulator.send(makeBewijsAanvraagRequest("00071031644"));

            assertNotNull(response);
            assertEquals("foo", response.getValue("//prop"));
        }

        @Test
        @SneakyThrows
        void finderFindsNothing() {
            var finder = mock(ResourceFinder.class);
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource("Persoon", "00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/notfound.xml")).thenReturn(null);
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/notfound.xml")).thenReturn(null);
            when(finder.loadSimulatorResource("Persoon", "notfound.xml")).thenReturn(null);
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/02.00.0000/success.xml")).thenReturn(null);
            when(finder.loadSimulatorResource("Persoon", "GeefBewijs/success.xml")).thenReturn(null);
            when(finder.loadSimulatorResource("Persoon", "success.xml")).thenReturn(null);

            var simulator = new StaticResponseSimulator("Persoon", "//INSZ");
            simulator.setFinder(finder);

            assertThrows(MagdaSendFailed.class, () -> simulator.send(makeBewijsAanvraagRequest("00071031644")));
        }
    }
}