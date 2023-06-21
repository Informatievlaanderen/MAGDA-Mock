package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.diensten.GeefAanslagbiljetPersonenbelastingRequest;
import be.vlaanderen.vip.magda.legallogging.model.TypeUitzondering;
import be.vlaanderen.vip.mock.magda.client.legallogging.AfnemerLogServiceMock;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GeefAanslagbiljetPersonenBelastingTest extends MockTestBase {

    private static final String INSZ_MAGDA_OVERBELAST = "91610100176";
    private static final String INSZ_GEENDATA = "67021546719";
    private static final String INSZ_DATA_NA2000 = "00610122377";
    private static final String INSZ_DATA_VOOR2000 = "82702108146";


    @Test
    @SneakyThrows
    void defaultAanslagBiljet() {
        var request = GeefAanslagbiljetPersonenbelastingRequest.builder()
                .insz(INSZ_GEENDATA)
                .build();

        var afnemerLogService = new AfnemerLogServiceMock();

        var connector = makeMagdaConnector(afnemerLogService);

        var antwoord = connector.send(request);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, request);
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), GeefAanslagbiljetPersonenBelastingTest.ANTWOORD_REFERTE, request.getRequestId().toString());

        assertThatResponseContainsAnswerNoError(antwoord);

        assertThat(afnemerLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfFailedLoggedRequests()).isZero();

        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/GevraagdePersoon/INSZ", "82102108114");
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/GevraagdePersoon/FiscaleStatus/Code", "A");
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/GevraagdePersoon/FiscaleStatus/Omschrijving", "Titularis");

        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Inkomensjaar", "2011");
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Artikelnummer", "727270607");

    }

    @Test
    @SneakyThrows
    void aanslagBiljetMet2Codes() {
        var request = GeefAanslagbiljetPersonenbelastingRequest.builder()
                .insz(INSZ_DATA_NA2000)
                .build();

        var afnemerLogService = new AfnemerLogServiceMock();

        var connector = makeMagdaConnector(afnemerLogService);

        var antwoord = connector.send(request);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, request);
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), GeefAanslagbiljetPersonenBelastingTest.ANTWOORD_REFERTE, request.getRequestId().toString());

        assertThat(afnemerLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfFailedLoggedRequests()).isZero();

        var doc = antwoord.getDocument();
        assertNotNull(doc);

        assertThatTechnicalFieldsInResponseMatchRequest(antwoord, request);

        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/GevraagdePersoon/INSZ", INSZ_DATA_NA2000);
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/GevraagdePersoon/FiscaleStatus/Code", "A");
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/GevraagdePersoon/FiscaleStatus/Omschrijving", "Titularis");

        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Inkomensjaar", "2011");
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Artikelnummer", "727270607");

        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Items/Item[1]/Code", "A7270");
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Items/Item[1]/Waarde", "3758645");
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Items/Item[2]/Code", "A7555");
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Items/Item[2]/Waarde", "3758645");
    }

    @Test
    @SneakyThrows
    void aanslagBiljetMet2CodesVoor2000() {
        var request = GeefAanslagbiljetPersonenbelastingRequest.builder()
                .insz(INSZ_DATA_VOOR2000)
                .build();

        var afnemerLogService = new AfnemerLogServiceMock();

        var connector = makeMagdaConnector(afnemerLogService);

        var antwoord = connector.send(request);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, request);
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), GeefAanslagbiljetPersonenBelastingTest.ANTWOORD_REFERTE, request.getRequestId().toString());

        assertThat(afnemerLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfFailedLoggedRequests()).isZero();

        var doc = antwoord.getDocument();
        assertNotNull(doc);

        assertThatTechnicalFieldsInResponseMatchRequest(antwoord, request);

        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/GevraagdePersoon/INSZ", INSZ_DATA_VOOR2000);
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/GevraagdePersoon/FiscaleStatus/Code", "A");
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/GevraagdePersoon/FiscaleStatus/Omschrijving", "Titularis");

        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Inkomensjaar", "2011");
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Artikelnummer", "727270607");

        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Items/Item[1]/Code", "A7270");
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Items/Item[1]/Waarde", "3758645");
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Items/Item[2]/Code", "A7555");
        assertThatXmlFieldIsEqualTo(antwoord.getDocument(), "//Antwoorden/Antwoord/Inhoud/AanslagbiljetPersonenbelasting/Items/Item[2]/Waarde", "3758645");
    }

    @Test
    @SneakyThrows
    void geefAanslagbiljetPersonenBelasting0200LuktNietOmdatMagdaOverbelastIs() {
        var request = GeefAanslagbiljetPersonenbelastingRequest.builder()
                .insz(INSZ_MAGDA_OVERBELAST)
                .build();

        var afnemerLogService = new AfnemerLogServiceMock();

        var connector = makeMagdaConnector(afnemerLogService);

        var antwoord = connector.send(request);
        assertThatTechnicalFieldsAreFilledInCorrectly(antwoord, request);

        assertThatAnswerContainsUitzondering(antwoord);

        assertThat(afnemerLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfSucceededLoggedRequests()).isZero();
        assertThat(afnemerLogService.getNumberOfFailedLoggedRequests()).isEqualTo(1);

        var uitzondering = antwoord.getUitzonderingen().get(0);
        assertThat(uitzondering.getUitzonderingType()).isEqualTo(TypeUitzondering.FOUT);
        assertThat(uitzondering.getIdentificatie()).isEqualTo("99996");
        assertThat(uitzondering.getDiagnose()).isEqualTo("Te veel gelijktijdige bevragingen");
    }



}
