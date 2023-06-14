package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.*;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import be.vlaanderen.vip.magda.client.security.TwoWaySslException;
import be.vlaanderen.vip.magda.client.security.TwoWaySslProperties;
import be.vlaanderen.vip.mock.magda.client.legallogging.AfnemerLogServiceMock;
import be.vlaanderen.vip.mock.magda.client.simulators.SOAPSimulatorBuilder;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinders;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public abstract class MockTestBase {

    public static final String TEST_SERVICE_URI = "be.vlaanderen/magda/mock.service";
    public static final String TEST_SERVICE_HOEDANIGHEID = "123";

    public static final String ANTWOORD_REFERTE = "//Antwoorden/Antwoord/Referte";
    public static final String ONTVANGER_REFERTE = "//Bericht/Ontvanger/Referte";
    public static final String ONTVANGER_IDENTIFICATIE = "//Bericht/Ontvanger/Identificatie";
    public static final String ONTVANGER_HOEDANIGHEID = "//Bericht/Ontvanger/Hoedanigheid";
    
    private ResourceFinder finder;
    
    @BeforeEach
    void setup() {
        finder = ResourceFinders.magdaSimulator();
    }

    protected MagdaConnectorImpl makeMagdaConnector(AfnemerLogServiceMock afnemerLogService) {
        var mockConnection = mockConnection();
        var mockedMagdaHoedanigheid = MagdaRegistrationInfo.builder()
                .identification(TEST_SERVICE_URI)
                .hoedanigheidscode(TEST_SERVICE_HOEDANIGHEID)
                .build();
        var magdaHoedanigheidService = new MagdaHoedanigheidServiceMock(mockedMagdaHoedanigheid);

        return new MagdaConnectorImpl(mockConnection, afnemerLogService, magdaHoedanigheidService);
    }

    protected MagdaConnectorImpl makeSignedMagdaConnector(
            AfnemerLogServiceMock afnemerLogService,
            TwoWaySslProperties signedConnectionRequestSignerKeystore,
            TwoWaySslProperties signedConnectionResponseVerifierKeystore,
            TwoWaySslProperties mockConnectionRequestVerifierKeystore,
            TwoWaySslProperties mockConnectionResponseSignerKeystore)
            throws TwoWaySslException {
        var mockConnection = mockConnection(mockConnectionRequestVerifierKeystore, mockConnectionResponseSignerKeystore);
        var signedConnection = new MagdaSignedConnection(mockConnection, signedConnectionRequestSignerKeystore, signedConnectionResponseVerifierKeystore);

        var mockedMagdaHoedanigheid = MagdaRegistrationInfo.builder()
                .identification(TEST_SERVICE_URI)
                .hoedanigheidscode(TEST_SERVICE_HOEDANIGHEID)
                .build();
        var magdaHoedanigheidService = new MagdaHoedanigheidServiceMock(mockedMagdaHoedanigheid);

        return new MagdaConnectorImpl(signedConnection, afnemerLogService, magdaHoedanigheidService);
    }
    
    private MagdaMockConnection mockConnection() {
        return mockConnection(null, null);
    }
    
    private MagdaMockConnection mockConnection(
            TwoWaySslProperties mockConnectionRequestVerifierKeystore,
            TwoWaySslProperties mockConnectionResponseSignerKeystore) {
        var simulatorBuilder = SOAPSimulatorBuilder.builder(finder)
                .magdaMockSimulator();
        if(mockConnectionRequestVerifierKeystore != null) {
            try {
                simulatorBuilder = simulatorBuilder.requestVerifierProperties(mockConnectionRequestVerifierKeystore);
            } catch (TwoWaySslException e) {
                throw new RuntimeException(e);
            }
        }
        if(mockConnectionResponseSignerKeystore != null) {
            try {
                simulatorBuilder = simulatorBuilder.responseSignerProperties(mockConnectionResponseSignerKeystore);
            } catch (TwoWaySslException e) {
                throw new RuntimeException(e);
            }
        }
        return new MagdaMockConnection(simulatorBuilder.build());
    }

    protected void assertThatTechnicalFieldsInResponseMatchRequest(MagdaAntwoord antwoord, Aanvraag aanvraag) {
        var doc = antwoord.getDocument();
        assertThatXmlFieldIsEqualTo(doc, RepertoriumTest.ONTVANGER_REFERTE, aanvraag.getRequestId().toString());
        assertThatXmlFieldIsEqualTo(doc, RepertoriumTest.ONTVANGER_IDENTIFICATIE, TEST_SERVICE_URI);
        assertThatXmlFieldIsEqualTo(doc, RepertoriumTest.ONTVANGER_HOEDANIGHEID, TEST_SERVICE_HOEDANIGHEID);
    }

    protected void assertThatXmlFieldIsEqualTo(MagdaDocument doc, String xmlPath, String expected) {
        var value = doc.getValue(xmlPath);
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
