package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.mock.magda.MagdaDocumentBuilder;
import be.vlaanderen.vip.mock.magda.client.exceptions.MagdaMockException;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinders;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mock.Strictness;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.stream.Stream;

import static be.vlaanderen.vip.mock.magda.client.simulators.SOAPSimulatorBuilder.KEY_INSZ;
import static be.vlaanderen.vip.mock.magda.client.simulators.SOAPSimulatorBuilder.LED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PathBasedStaticResponseSimulatorTest {
    
    private static final Map<Object, Object> GIVE_PROOF_CONTEXT = Map.of("Naam", "GeefBewijs",
                                                                         "Versie", "02.00.0000");

    private MagdaDocument makeGeefBewijsRequest(String insz) {
        return MagdaDocumentBuilder.request(Map.of("Context", GIVE_PROOF_CONTEXT,
                                                   "Vragen", questionInsz(insz)));
    }
    
    private Map<Object, Object> questionInsz(String insz) {
        return Map.of("Vraag", Map.of("Inhoud", Map.of("INSZ", insz)));
    }

    @Test
    @SneakyThrows
    void respondsWithStaticResource() {
        var simulator = new PathBasedStaticResponseSimulator(ResourceFinders.magdaSimulator(), LED, KEY_INSZ);
        var request = makeGeefBewijsRequest("00671031676");

        var response = simulator.send(request);

        assertEquals("2014070108135743808300040H", response.getValue("//Bewijs/Leverancier/Bewijsreferte"));
    }

    @ParameterizedTest
    @MethodSource("throwsExceptionIfRequestContainsIllegalValues_parameters")
    @SneakyThrows
    void throwsExceptionIfRequestContainsIllegalValues(String serviceName, String serviceVersion, String insz) {
        var simulator = new PathBasedStaticResponseSimulator(ResourceFinders.magdaSimulator(), LED, KEY_INSZ);
        var request = MagdaDocumentBuilder.request(Map.of(
                "Context", Map.of(
                        "Naam", serviceName,
                        "Versie", serviceVersion),
                "Vragen", questionInsz(insz)));

        assertThrows(MagdaMockException.class, () -> simulator.send(request));
    }

    private static Stream<Arguments> throwsExceptionIfRequestContainsIllegalValues_parameters() {
        return Stream.of(
                Arguments.of("GeefBewijs/02.00.0000", "", "00671031647"),
                Arguments.of("..", "Persoon/GeefBewijs/02.00.0000", "00671031647"),
                Arguments.of(".", "GeefBewijs/02.00.0000", "00671031647"),
                Arguments.of("", "GeefBewijs/02.00.0000", "00671031647"),
                Arguments.of("GeefBewijs", "02.00.0000", "00671031647/../00671031647")
        );
    }

    @Nested
    class FinderBehaviorTests {
        @Mock(strictness = Strictness.LENIENT) 
        private ResourceFinder finder;

        private InputStream documentWithPropValue(String value) {
            return new ByteArrayInputStream(("<prop>" + value + "</prop>").getBytes());
        }

        @Test
        @SneakyThrows
        void finderLooksForTargetDocument_AtItsExpectedLocation() {
            var mockDocumentA = documentWithPropValue("foo");
            var mockDocumentB = documentWithPropValue("bar");
            var mockDocumentC = documentWithPropValue("baz");
            when(finder.loadSimulatorResource(LED, "GeefBewijs/02.00.0000/00071031644.xml")).thenReturn(mockDocumentA);
            when(finder.loadSimulatorResource(LED, "GeefBewijs/02.00.0000/notfound.xml")).thenReturn(mockDocumentB);
            when(finder.loadSimulatorResource(LED, "GeefBewijs/02.00.0000/success.xml")).thenReturn(mockDocumentC);

            var simulator = new PathBasedStaticResponseSimulator(finder, LED, KEY_INSZ);

            var response = simulator.send(makeGeefBewijsRequest("00071031644"));

            assertNotNull(response);
            assertEquals("foo", response.getValue("//prop"));
        }

        @Test
        @SneakyThrows
        void finderLooksForNotFoundDocument_IfTargetDocumentIsNotThere() {
            var mockDocumentA = documentWithPropValue("foo");
            var mockDocumentB = documentWithPropValue("bar");
            when(finder.loadSimulatorResource(LED, "GeefBewijs/02.00.0000/00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource(LED, "GeefBewijs/02.00.0000/notfound.xml")).thenReturn(mockDocumentA);
            when(finder.loadSimulatorResource(LED, "GeefBewijs/02.00.0000/success.xml")).thenReturn(mockDocumentB);

            var simulator = new PathBasedStaticResponseSimulator(finder, LED, KEY_INSZ);

            var response = simulator.send(makeGeefBewijsRequest("00071031644"));

            assertNotNull(response);
            assertEquals("foo", response.getValue("//prop"));
        }

        @Test
        @SneakyThrows
        void finderLooksForSuccessDocument_IfNeitherTargetNorNotFoundDocumentAreThere() {
            var mockDocument = documentWithPropValue("foo");
            when(finder.loadSimulatorResource(LED, "GeefBewijs/02.00.0000/00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource(LED, "GeefBewijs/02.00.0000/notfound.xml")).thenReturn(null);
            when(finder.loadSimulatorResource(LED, "GeefBewijs/02.00.0000/success.xml")).thenReturn(mockDocument);

            var simulator = new PathBasedStaticResponseSimulator(finder, LED, KEY_INSZ);

            var response = simulator.send(makeGeefBewijsRequest("00071031644"));

            assertNotNull(response);
            assertEquals("foo", response.getValue("//prop"));
        }

        @Test
        @SneakyThrows
        void finderLooksForTargetDocument_OnLowestLevelFirst() {
            var mockDocumentA = documentWithPropValue("foo");
            var mockDocumentB = documentWithPropValue("bar");
            var mockDocumentC = documentWithPropValue("baz");
            when(finder.loadSimulatorResource(LED, "GeefBewijs/02.00.0000/00071031644.xml")).thenReturn(mockDocumentA);
            when(finder.loadSimulatorResource(LED, "GeefBewijs/00071031644.xml")).thenReturn(mockDocumentB);
            when(finder.loadSimulatorResource(LED, "00071031644.xml")).thenReturn(mockDocumentC);

            var simulator = new PathBasedStaticResponseSimulator(finder, LED, KEY_INSZ);

            var response = simulator.send(makeGeefBewijsRequest("00071031644"));

            assertNotNull(response);
            assertEquals("foo", response.getValue("//prop"));
        }

        @Test
        @SneakyThrows
        void finderLooksForTargetDocument_OnHigherLevel() {
            var mockDocumentA = documentWithPropValue("foo");
            var mockDocumentB = documentWithPropValue("bar");
            when(finder.loadSimulatorResource(LED, "GeefBewijs/02.00.0000/00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource(LED, "GeefBewijs/00071031644.xml")).thenReturn(mockDocumentA);
            when(finder.loadSimulatorResource(LED, "00071031644.xml")).thenReturn(mockDocumentB);

            var simulator = new PathBasedStaticResponseSimulator(finder, LED, KEY_INSZ);

            var response = simulator.send(makeGeefBewijsRequest("00071031644"));

            assertNotNull(response);
            assertEquals("foo", response.getValue("//prop"));
        }

        @Test
        @SneakyThrows
        void finderLooksForTargetDocument_OnHighestLevel() {
            var mockDocument = documentWithPropValue("foo");
            when(finder.loadSimulatorResource(LED, "GeefBewijs/02.00.0000/00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource(LED, "GeefBewijs/00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource(LED, "00071031644.xml")).thenReturn(mockDocument);

            var simulator = new PathBasedStaticResponseSimulator(finder, LED, KEY_INSZ);

            var response = simulator.send(makeGeefBewijsRequest("00071031644"));

            assertNotNull(response);
            assertEquals("foo", response.getValue("//prop"));
        }

        @Test
        @SneakyThrows
        void finderLooksForTargetDocument_OnLevelsDeeperThanServiceAndVersion() {
            var mockDocumentA = documentWithPropValue("foo");
            var mockDocumentB = documentWithPropValue("bar");
            var mockDocumentC = documentWithPropValue("baz");
            when(finder.loadSimulatorResource(LED, "GeefBewijs/02.00.0000/deep/reallydeep/00071031644.xml")).thenReturn(mockDocumentA);
            when(finder.loadSimulatorResource(LED, "GeefBewijs/02.00.0000/deep/00071031644.xml")).thenReturn(mockDocumentB);
            when(finder.loadSimulatorResource(LED, "GeefBewijs/02.00.0000/00071031644.xml")).thenReturn(mockDocumentC);

            var simulator = new PathBasedStaticResponseSimulator(finder, LED, "//LV1", "//LV2", "//INSZ");

            var request = MagdaDocumentBuilder.request(Map.of("Context", GIVE_PROOF_CONTEXT,
                                                              "Vragen", questionInsz("00071031644"),
                                                              "LV1", "deep",
                                                              "LV2", "reallydeep"));
            var response = simulator.send(request);

            assertNotNull(response);
            assertEquals("foo", response.getValue("//prop"));
        }

        @Test
        @SneakyThrows
        void finderLooksNotFoundDocument_OnLowestLevelFirst() {
            var mockDocumentA = documentWithPropValue("foo");
            var mockDocumentB = documentWithPropValue("bar");
            var mockDocumentC = documentWithPropValue("baz");
            when(finder.loadSimulatorResource(LED, "GeefBewijs/02.00.0000/00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource(LED, "GeefBewijs/00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource(LED, "00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource(LED, "GeefBewijs/02.00.0000/notfound.xml")).thenReturn(mockDocumentA);
            when(finder.loadSimulatorResource(LED, "GeefBewijs/notfound.xml")).thenReturn(mockDocumentB);
            when(finder.loadSimulatorResource(LED, "notfound.xml")).thenReturn(mockDocumentC);

            var simulator = new PathBasedStaticResponseSimulator(finder, LED, KEY_INSZ);

            var response = simulator.send(makeGeefBewijsRequest("00071031644"));

            assertNotNull(response);
            assertEquals("foo", response.getValue("//prop"));
        }

        @Test
        @SneakyThrows
        void finderLooksForNotFoundDocument_OnHigherLevel() {
            var mockDocumentA = documentWithPropValue("foo");
            var mockDocumentB = documentWithPropValue("bar");
            when(finder.loadSimulatorResource(LED, "GeefBewijs/02.00.0000/00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource(LED, "GeefBewijs/00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource(LED, "00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource(LED, "GeefBewijs/02.00.0000/notfound.xml")).thenReturn(null);
            when(finder.loadSimulatorResource(LED, "GeefBewijs/notfound.xml")).thenReturn(mockDocumentA);
            when(finder.loadSimulatorResource(LED, "notfound.xml")).thenReturn(mockDocumentB);

            var simulator = new PathBasedStaticResponseSimulator(finder, LED, KEY_INSZ);

            var response = simulator.send(makeGeefBewijsRequest("00071031644"));

            assertNotNull(response);
            assertEquals("foo", response.getValue("//prop"));
        }

        @Test
        @SneakyThrows
        void finderLooksForNotFoundDocument_OnHighestLevel() {
            var mockDocument = documentWithPropValue("foo");
            when(finder.loadSimulatorResource(LED, "GeefBewijs/02.00.0000/00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource(LED, "GeefBewijs/00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource(LED, "00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource(LED, "GeefBewijs/02.00.0000/notfound.xml")).thenReturn(null);
            when(finder.loadSimulatorResource(LED, "GeefBewijs/notfound.xml")).thenReturn(null);
            when(finder.loadSimulatorResource(LED, "notfound.xml")).thenReturn(mockDocument);

            var simulator = new PathBasedStaticResponseSimulator(finder, LED, KEY_INSZ);

            var response = simulator.send(makeGeefBewijsRequest("00071031644"));

            assertNotNull(response);
            assertEquals("foo", response.getValue("//prop"));
        }

        @Test
        @SneakyThrows
        void finderLooksForTargetEverywhereBeforeLookingForNotFound() {
            var mockDocumentA = documentWithPropValue("foo");
            var mockDocumentB = documentWithPropValue("bar");
            when(finder.loadSimulatorResource(LED, "GeefBewijs/02.00.0000/00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource(LED, "GeefBewijs/00071031644.xml")).thenReturn(mockDocumentA);
            when(finder.loadSimulatorResource(LED, "GeefBewijs/02.00.0000/notfound.xml")).thenReturn(mockDocumentB);

            var simulator = new PathBasedStaticResponseSimulator(finder, LED, KEY_INSZ);

            var response = simulator.send(makeGeefBewijsRequest("00071031644"));

            assertNotNull(response);
            assertEquals("foo", response.getValue("//prop"));
        }

        @Test
        @SneakyThrows
        void finderFindsNothing() {
            when(finder.loadSimulatorResource(LED, "GeefBewijs/02.00.0000/00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource(LED, "GeefBewijs/00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource(LED, "00071031644.xml")).thenReturn(null);
            when(finder.loadSimulatorResource(LED, "GeefBewijs/02.00.0000/notfound.xml")).thenReturn(null);
            when(finder.loadSimulatorResource(LED, "GeefBewijs/notfound.xml")).thenReturn(null);
            when(finder.loadSimulatorResource(LED, "notfound.xml")).thenReturn(null);
            when(finder.loadSimulatorResource(LED, "GeefBewijs/02.00.0000/success.xml")).thenReturn(null);
            when(finder.loadSimulatorResource(LED, "GeefBewijs/success.xml")).thenReturn(null);
            when(finder.loadSimulatorResource(LED, "success.xml")).thenReturn(null);

            var simulator = new PathBasedStaticResponseSimulator(finder, LED, KEY_INSZ);

            var request = makeGeefBewijsRequest("00071031644");
            assertThrows(MagdaMockException.class, () -> simulator.send(request));
        }
    }
    
    @Nested
    class PatchResponse {
        @Mock private ResourceFinder finder;
        
        private PathBasedStaticResponseSimulator simulator;
        
        @BeforeEach
        void setup() {
            simulator = new PathBasedStaticResponseSimulator(finder, LED, KEY_INSZ);
            
            when(finder.loadSimulatorResource(eq(LED), anyString())).thenAnswer(iom -> new ByteArrayInputStream("""
<geef:GeefBewijsResponse xmlns:geef="http://geefbewijs.bewijsraadplegingdienst.led.vlaanderen.be">
 <Repliek>
     <Context>
         <Naam>GeefBewijs</Naam>
         <Versie>02.00.0000</Versie>
         <Bericht>
             <Type>ANTWOORD</Type>
             <Tijdstip>
                 <Datum>2017-05-05</Datum>
                 <Tijd>07:46:52.589</Tijd>
             </Tijdstip>
             <Afzender>
                 <Identificatie>vip.vlaanderen.be</Identificatie>
                 <Naam>MagdaGateway</Naam>
                 <Referte>c42275a3-f433-4fa1-b469-e47651f08208</Referte>
             </Afzender>
             <Ontvanger>
                 <Identificatie>wse.vlaanderen.be/vdab/mint-aip-bo</Identificatie>
                 <Referte>XE:193:25162:8280</Referte>
                 <Hoedanigheid>801</Hoedanigheid>
                 <Gebruiker>77050526771</Gebruiker>
             </Ontvanger>
         </Bericht>
     </Context>
 </Repliek>
</geef:GeefBewijsResponse>
                    """.getBytes()));
        }
        
        @Test
        void setsUser_whenPresentInRequest() {
            var request = MagdaDocumentBuilder.request(Map.of("Context", GIVE_PROOF_CONTEXT,
                                                              "Vragen", questionInsz("00071031644"),
                                                              "Afzender", Map.of("Gebruiker", "123")));
            
            var result = simulator.send(request);
            
            assertEquals("123", result.getValue("//Gebruiker"));
        }
        
        @Test
        void removesUser_whenNotPresentInRequest() {
            var request = MagdaDocumentBuilder.request(Map.of("Context", GIVE_PROOF_CONTEXT,
                                                              "Vragen", questionInsz("00071031644")));

            var result = simulator.send(request);
            
            assertNull(result.getValue("//Gebruiker"));
        }
        
    }
}