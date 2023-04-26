package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.*;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import be.vlaanderen.vip.magda.client.security.TwoWaySslProperties;
import be.vlaanderen.vip.magda.exception.TwoWaySslException;
import be.vlaanderen.vip.mock.magda.client.legallogging.AfnemerLogServiceMock;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder;
import lombok.extern.slf4j.Slf4j;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public abstract class MockTestBase {

    public static final String TEST_SERVICE_NAAM = "Magda Mock";
    public static final String TEST_SERVICE_URI = "be.vlaanderen/magda/mock.service";
    public static final String TEST_SERVICE_HOEDANIGHEID = "123";

    public static final String VRAAG_REFERTE = "//Vragen/Vraag/Referte";
    public static final String AFZENDER_REFERTE = "//Bericht/Afzender/Referte";
    public static final String AFZENDER_IDENTIFICATIE = "//Bericht/Afzender/Identificatie";
    public static final String AFZENDER_HOEDANIGHEID = "//Bericht/Afzender/Hoedanigheid";
    
    public static final String ANTWOORD_REFERTE = "//Antwoorden/Antwoord/Referte";
    public static final String ONTVANGER_REFERTE = "//Bericht/Ontvanger/Referte";
    public static final String ONTVANGER_IDENTIFICATIE = "//Bericht/Ontvanger/Identificatie";
    public static final String ONTVANGER_HOEDANIGHEID = "//Bericht/Ontvanger/Hoedanigheid";

    protected MagdaConnectorImpl makeMagdaConnector(AfnemerLogServiceMock afnemerLogService) {
        var connection = new MagdaMockConnection();

        var mockedMagdaHoedanigheid = MagdaRegistrationInfo.builder()
                .name(TEST_SERVICE_NAAM)
                .identification(TEST_SERVICE_URI)
                .hoedanigheidscode(TEST_SERVICE_HOEDANIGHEID)
                .build();
        var magdaHoedanigheidService = new MagdaHoedanigheidServiceMock(mockedMagdaHoedanigheid);

        return new MagdaConnectorImpl(connection, afnemerLogService, magdaHoedanigheidService);
    }

    protected MagdaConnectorImpl makeSignedMagdaConnector(
            AfnemerLogServiceMock afnemerLogService,
            TwoWaySslProperties signedConnectionRequestSignerKeystore,
            TwoWaySslProperties signedConnectionResponseVerifierKeystore,
            TwoWaySslProperties mockConnectionRequestVerifierKeystore,
            TwoWaySslProperties mockConnectionResponseSignerKeystore)
            throws TwoWaySslException {
        var mockConnection = new MagdaMockConnection(new ResourceFinder(), mockConnectionRequestVerifierKeystore, mockConnectionResponseSignerKeystore);

        var signedConnection = new MagdaSignedConnection(mockConnection, signedConnectionRequestSignerKeystore, signedConnectionResponseVerifierKeystore);

        var mockedMagdaHoedanigheid = MagdaRegistrationInfo.builder()
                .name(TEST_SERVICE_NAAM)
                .identification(TEST_SERVICE_URI)
                .hoedanigheidscode(TEST_SERVICE_HOEDANIGHEID)
                .build();
        var magdaHoedanigheidService = new MagdaHoedanigheidServiceMock(mockedMagdaHoedanigheid);

        return new MagdaConnectorImpl(signedConnection, afnemerLogService, magdaHoedanigheidService);
    }

    protected void assertThatTechnicalFieldsInResponseMatchRequest(MagdaAntwoord antwoord, Aanvraag aanvraag) {
        var doc = antwoord.getDocument();
        assertThatXmlFieldIsEqualTo(doc, RepertoriumTest.ONTVANGER_REFERTE, aanvraag.getRequestId().toString());
        assertThatXmlFieldIsEqualTo(doc, RepertoriumTest.ONTVANGER_IDENTIFICATIE, TEST_SERVICE_URI);
        assertThatXmlFieldIsEqualTo(doc, RepertoriumTest.ONTVANGER_HOEDANIGHEID, TEST_SERVICE_HOEDANIGHEID);
    }

    protected void assertThatXmlFieldIsEqualTo(MagdaDocument doc, String xmlPath, String expected) {
        String value = doc.getValue(xmlPath);
        assertThat(value).isNotNull();
        assertThat(value).isEqualTo(expected);
    }

    protected void assertThatResponseContainsAnswerNoError(MagdaAntwoord antwoord) {
        assertThat(antwoord.isBodyIngevuld()).isTrue();
        assertThat(antwoord.isHeeftInhoud()).isTrue();
        assertThat(antwoord.getAntwoordUitzonderingen()).isEmpty();
        assertThat(antwoord.getUitzonderingen()).isEmpty();
    }

    protected void assertThatAnswerContainsUitzondering(MagdaAntwoord antwoord) {
        assertThat(antwoord.isBodyIngevuld()).isFalse();
        assertThat(antwoord.isHeeftInhoud()).isFalse();
        assertThat(antwoord.getAntwoordUitzonderingen()).isEmpty();
        assertThat(antwoord.getUitzonderingen()).hasSize(1);
    }

    protected void assertThatTechnicalFieldsAreFilledInCorrectly(MagdaAntwoord antwoord, Aanvraag aanvraag) {
        log.debug("Response: {}", antwoord.getDocument().toString());

        assertThatTechnicalFieldsInResponseMatchRequest(antwoord, aanvraag);
    }
}
