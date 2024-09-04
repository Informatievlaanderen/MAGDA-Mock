package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.mock.magda.MagdaDocumentBuilder;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinders;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import static be.vlaanderen.vip.mock.magda.client.simulators.SOAPSimulatorBuilder.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class GeefDossierHandicapSimulatorTest {

    @Test
    void fillsInSsinFromByDateRequest() throws URISyntaxException, IOException {
        var ssin = "10712995476";

        var request = MagdaDocumentBuilder.request(Map.of(
                "Context", Map.of(
                        "Naam", "GeefDossierHandicap",
                        "Versie", "03.00.0000"),
                "Vragen", Map.of(
                        "Vraag", Map.of(
                                "Inhoud", Map.of(
                                        "Criteria", Map.of(
                                                "ConsultFilesByDateCriteria", Map.of(
                                                        "ssin", ssin
                                                )))))));

        var simulator = new GeefDossierHandicapSimulator(ResourceFinders.magdaSimulator(), PERSOON, KEY_SSIN);

        var response = simulator.send(request);

        assertEquals(ssin, response.getValue("//Antwoorden/Antwoord/Inhoud/ConsultFilesByDateResponse/ssin"));
    }
    @Test
    void ssinNotFilledInWhenNoDataFound() throws URISyntaxException, IOException {
        var ssin = "00010100173";
        var request = MagdaDocumentBuilder.request(Map.of(
                "Context", Map.of(
                        "Naam", "GeefDossierHandicap",
                        "Versie", "03.00.0000"),
                "Vragen", Map.of(
                        "Vraag", Map.of(
                                "Inhoud", Map.of(
                                        "Criteria", Map.of(
                                                "ConsultFilesByDateCriteria", Map.of(
                                                        "ssin", ssin
                                                )))))));

        var simulator = new GeefDossierHandicapSimulator(ResourceFinders.magdaSimulator(), PERSOON, KEY_SSIN);

        var response = simulator.send(request);

        assertNull(response.getValue("//Antwoorden/Antwoord/Inhoud/ConsultFilesByDateResponse/ssin"));
    }
}