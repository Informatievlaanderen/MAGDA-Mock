package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.diensten.GeefAanslagbiljetPersonenbelastingRequest;
import be.vlaanderen.vip.magda.legallogging.model.UitzonderingType;
import be.vlaanderen.vip.mock.magda.client.legallogging.ClientLogServiceMock;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GeefAanslagbiljetPersonenbelastingTest extends MockTestBase {

    private static final UUID REQUEST_ID = UUID.fromString("64fb1939-0ca7-432b-b7f4-3b53f7fc3789");
    private static final String INSZ_MAGDA_OVERBELAST = "91610100176";
    private static final String INSZ_GEENDATA = "67021546719";
    private static final String INSZ_DATA_NA2000 = "00610122309";
    private static final String INSZ_DATA_VOOR2000 = "82702108146";

    @Test
    @SneakyThrows
    void notFoundAanslagBiljet() {
        var request = GeefAanslagbiljetPersonenbelastingRequest.builder2()
                .insz(INSZ_GEENDATA)
                .incomeYear(Year.of(2021))
                .build();

        var clientLogService = new ClientLogServiceMock();

        var connector = makeMagdaConnector(clientLogService);

        var antwoord = connector.send(request, REQUEST_ID);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, REQUEST_ID);
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), GeefAanslagbiljetPersonenbelastingTest.ANTWOORD_REFERTE, REQUEST_ID.toString());

        assertThat(clientLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfFailedLoggedRequests()).isZero();

        //No data found
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Uitzonderingen/Uitzondering/Identificatie", "45008");
    }

    @Test
    @SneakyThrows
    void notFoundAanslagBiljetWithStaticSimulatorShouldRetainInkomensjaar() {
        var request = GeefAanslagbiljetPersonenbelastingRequest.builder2()
                .insz(INSZ_GEENDATA)
                .incomeYear(Year.of(2021))
                .build();

        var clientLogService = new ClientLogServiceMock();

        var connector = makeMagdaConnector(clientLogService, false);

        var antwoord = connector.send(request, REQUEST_ID);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, REQUEST_ID);
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), GeefAanslagbiljetPersonenbelastingTest.ANTWOORD_REFERTE, REQUEST_ID.toString());
        
        assertThat(clientLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfFailedLoggedRequests()).isZero();

        //No data found
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Uitzonderingen/Uitzondering/Identificatie", "45008");
    }

    @Test
    @SneakyThrows
    void aanslagBiljetMet2Codes() {
        var request = GeefAanslagbiljetPersonenbelastingRequest.builder2()
                .insz(INSZ_DATA_NA2000)
                .incomeYear(Year.of(2021))
                .build();

        var clientLogService = new ClientLogServiceMock();

        var connector = makeMagdaConnector(clientLogService);

        var antwoord = connector.send(request, REQUEST_ID);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, REQUEST_ID);
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), GeefAanslagbiljetPersonenbelastingTest.ANTWOORD_REFERTE, REQUEST_ID.toString());

        assertThat(clientLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfFailedLoggedRequests()).isZero();

        var doc = antwoord.getDocument();
        assertNotNull(doc);

        assertThatTechnicalFieldsInResponseMatchRequest(antwoord, REQUEST_ID);

        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/GevraagdePersoon/INSZ", INSZ_DATA_NA2000);
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/GevraagdePersoon/FiscaleStatus/Code", "A");
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/GevraagdePersoon/FiscaleStatus/Omschrijving", "Titularis");

        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Inkomensjaar", "2021");
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Artikelnummer", "727270607");

        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Items/Item[1]/Code", "A7270");
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Items/Item[1]/Waarde", "3758645");
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Items/Item[2]/Code", "A7555");
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Items/Item[2]/Waarde", "3758645");
    }

    @Test
    @SneakyThrows
    void aanslagBiljetMet2CodesVoor2000() {
        var request = GeefAanslagbiljetPersonenbelastingRequest.builder2()
                .insz(INSZ_DATA_VOOR2000)
                .incomeYear(Year.of(2021))
                .build();

        var clientLogService = new ClientLogServiceMock();

        var connector = makeMagdaConnector(clientLogService);

        var antwoord = connector.send(request, REQUEST_ID);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, REQUEST_ID);
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), GeefAanslagbiljetPersonenbelastingTest.ANTWOORD_REFERTE, REQUEST_ID.toString());

        assertThat(clientLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfFailedLoggedRequests()).isZero();

        var doc = antwoord.getDocument();
        assertNotNull(doc);

        assertThatTechnicalFieldsInResponseMatchRequest(antwoord, REQUEST_ID);

        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/GevraagdePersoon/INSZ", INSZ_DATA_VOOR2000);
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/GevraagdePersoon/FiscaleStatus/Code", "A");
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/GevraagdePersoon/FiscaleStatus/Omschrijving", "Titularis");

        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Inkomensjaar", "2021");
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Artikelnummer", "727270607");

        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Items/Item[1]/Code", "A7270");
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Items/Item[1]/Waarde", "3758645");
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Items/Item[2]/Code", "A7555");
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Items/Item[2]/Waarde", "3758645");
    }

    @Test
    @SneakyThrows
    void geefAanslagbiljetPersonenBelasting0200LuktNietOmdatMagdaOverbelastIs() {
        var request = GeefAanslagbiljetPersonenbelastingRequest.builder2()
                .insz(INSZ_MAGDA_OVERBELAST)
                .incomeYear(Year.of(2021))
                .build();

        var clientLogService = new ClientLogServiceMock();

        var connector = makeMagdaConnector(clientLogService);

        var antwoord = connector.send(request, REQUEST_ID);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, REQUEST_ID);

        assertThatAnswerContainsUitzondering(antwoord);

        assertThat(clientLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfSucceededLoggedRequests()).isZero();
        assertThat(clientLogService.getNumberOfFailedLoggedRequests()).isEqualTo(1);

        var uitzondering = antwoord.getUitzonderingEntries().get(0);
        assertThat(uitzondering.getUitzonderingType()).isEqualTo(UitzonderingType.FOUT);
        assertThat(uitzondering.getIdentification()).isEqualTo("99996");
        assertThat(uitzondering.getDiagnosis()).isEqualTo("Te veel gelijktijdige bevragingen");
    }
}