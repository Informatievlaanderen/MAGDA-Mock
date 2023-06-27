package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.mock.magda.MagdaDocumentBuilder;
import be.vlaanderen.vip.mock.magda.client.exceptions.MagdaMockException;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinders;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Map;
import java.util.stream.Stream;

import static be.vlaanderen.vip.mock.magda.client.simulators.SOAPSimulatorBuilder.KEY_INSZ;
import static be.vlaanderen.vip.mock.magda.client.simulators.SOAPSimulatorBuilder.PERSOON;
import static org.junit.jupiter.api.Assertions.*;

class RandomPasfotoSimulatorTest {

    private Map<Object, Object> questionInsz(String insz) {
        return Map.of("Vraag", Map.of("Inhoud", Map.of("INSZ", insz)));
    }

    @Test
    @SneakyThrows
    void respondsWithRandomPassportPhotoResource() {
        var simulator = new RandomPasfotoSimulator(ResourceFinders.magdaSimulator(), PERSOON, KEY_INSZ);
        var request = MagdaDocumentBuilder.request(Map.of(
                "Context", Map.of(
                        "Naam", "GeefPasfoto",
                        "Versie", "02.00.0000"),
                "Vragen", questionInsz("00671031647")));

        var response = simulator.send(request);

        assertNotNull(response.getValue("//Antwoorden/Antwoord/Inhoud/Pasfoto/Foto"));
    }

    @ParameterizedTest
    @MethodSource("throwsExceptionIfRequestContainsIllegalValues_parameters")
    @SneakyThrows
    void throwsExceptionIfRequestContainsIllegalValues(String serviceName, String serviceVersion, String insz) {
        var simulator = new RandomPasfotoSimulator(ResourceFinders.magdaSimulator(), PERSOON, KEY_INSZ);
        var request = MagdaDocumentBuilder.request(Map.of(
                "Context", Map.of(
                        "Naam", serviceName,
                        "Versie", serviceVersion),
                "Vragen", questionInsz(insz)));

        assertThrows(MagdaMockException.class, () -> simulator.send(request));
    }

    private static Stream<Arguments> throwsExceptionIfRequestContainsIllegalValues_parameters() {
        return Stream.of(
                Arguments.of("GeefPasfoto/02.00.0000", "", "00671031647"),
                Arguments.of("..", "Persoon/GeefPasfoto/02.00.0000", "00671031647"),
                Arguments.of(".", "GeefPasfoto/02.00.0000", "00671031647"),
                Arguments.of("", "GeefPasfoto/02.00.0000", "00671031647"),
                Arguments.of("GeefPasfoto", "02.00.0000", "00671031647/../00671031647")
        );
    }
}