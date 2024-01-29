package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.mock.magda.MagdaDocumentBuilder;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinders;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import static be.vlaanderen.vip.mock.magda.client.simulators.SOAPSimulatorBuilder.KEY_INSZ;
import static be.vlaanderen.vip.mock.magda.client.simulators.SOAPSimulatorBuilder.PERSOON;
import static org.junit.jupiter.api.Assertions.*;

class GeefAanslagbiljetPersonenbelastingSimulatorTest {

    @Test
    void fillsInIncomeYearFromRequestWhenEnabled() throws URISyntaxException, IOException {
        var insz = "00610122309";
        var incomeYear = "2021";

        var request = MagdaDocumentBuilder.request(Map.of(
                "Context", Map.of(
                        "Naam", "GeefAanslagbiljetPersonenbelasting",
                        "Versie", "02.00.0000"),
                "Vragen", Map.of(
                        "Vraag", Map.of(
                                "Inhoud", Map.of(
                                        "INSZ", insz,
                                        "Criteria", Map.of(
                                                "Inkomensjaar", incomeYear))))));

        var simulator = new GeefAanslagbiljetPersonenbelastingSimulator(ResourceFinders.magdaSimulator(), PERSOON, true, KEY_INSZ);

        var response = simulator.send(request);

        assertEquals(incomeYear, response.getValue("//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Inkomensjaar"));
    }

    @Test
    void retainsIncomeYearFromResponseWhenDisabled() throws URISyntaxException, IOException {
        var request = MagdaDocumentBuilder.request(Map.of(
                "Context", Map.of(
                        "Naam", "GeefAanslagbiljetPersonenbelasting",
                        "Versie", "02.00.0000"),
                "Vragen", Map.of(
                        "Vraag", Map.of(
                                "Inhoud", Map.of(
                                        "INSZ", "00610122309",
                                        "Criteria", Map.of(
                                                "Inkomensjaar", "2021"))))));

        var simulator = new GeefAanslagbiljetPersonenbelastingSimulator(ResourceFinders.magdaSimulator(), PERSOON, false, KEY_INSZ);

        var response = simulator.send(request);

        assertEquals("2011", response.getValue("//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Inkomensjaar"));
    }

}