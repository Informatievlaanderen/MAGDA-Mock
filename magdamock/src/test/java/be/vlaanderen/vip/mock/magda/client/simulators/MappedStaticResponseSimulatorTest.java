package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.mock.magda.MagdaDocumentBuilder;
import be.vlaanderen.vip.mock.magda.client.exceptions.MagdaMockException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static be.vlaanderen.vip.mock.magda.client.simulators.SOAPSimulatorBuilder.KEY_INSZ;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MappedStaticResponseSimulatorTest {

    private static final Map<Object, Object> GIVE_PROOF_CONTEXT = Map.of(
            "Naam", "GeefBewijs",
            "Versie", "02.00.0000");

    private MagdaDocument makeGeefBewijsRequest(String insz) {
        return MagdaDocumentBuilder.request(Map.of("Context", GIVE_PROOF_CONTEXT,
                "Vragen", questionInsz(insz)));
    }

    private Map<Object, Object> questionInsz(String insz) {
        return Map.of("Vraag", Map.of("Inhoud", Map.of("INSZ", insz)));
    }

    private MappedStaticResponseSimulator simulator;

    @BeforeEach
    public void setup() {
        simulator = new MappedStaticResponseSimulator(KEY_INSZ)
                .add(MagdaDocument.fromResource(getClass(), "/magda_simulator/Persoon/GeefBewijs/02.00.0000/00671031676.xml"), "00671031676");
    }

    @Test
    @SneakyThrows
    void respondsWithMappedStaticResource() {
        var request = makeGeefBewijsRequest("00671031676");

        var response = simulator.send(request);

        assertEquals("2014070108135743808300040H", response.getValue("//Bewijs/Leverancier/Bewijsreferte"));
    }

    @Test
    @SneakyThrows
    void whenNoResourceIsMapped_throwsException() {
        var request = makeGeefBewijsRequest("10611300379");

        assertThrows(MagdaMockException.class, () -> simulator.send(request));
    }

    @ParameterizedTest
    @MethodSource("throwsExceptionIfRequestContainsIllegalValues_parameters")
    @SneakyThrows
    void throwsExceptionIfRequestContainsIllegalValues(String serviceName, String serviceVersion, String insz) {
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
}