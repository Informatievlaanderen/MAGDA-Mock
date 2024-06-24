package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.*;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import be.vlaanderen.vip.magda.client.security.TwoWaySslException;
import be.vlaanderen.vip.magda.client.security.TwoWaySslProperties;
import be.vlaanderen.vip.mock.magda.client.legallogging.ClientLogServiceMock;
import be.vlaanderen.vip.mock.magda.client.simulators.SOAPSimulatorBuilder;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinders;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;

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
    void setup() throws URISyntaxException, IOException {
        finder = ResourceFinders.magdaSimulator();
    }

    protected MagdaConnectorImpl makeMagdaConnector(ClientLogServiceMock clientLogServiceMock){
        return makeMagdaConnector(clientLogServiceMock, true);
    }

    protected MagdaConnectorImpl makeMagdaConnector(ClientLogServiceMock clientLogService, boolean copyPropertiesFromRequest) {
        var mockConnection = mockConnection(copyPropertiesFromRequest);
        var mockedMagdaHoedanigheid = MagdaRegistrationInfo.builder()
                .identification(TEST_SERVICE_URI)
                .hoedanigheidscode(TEST_SERVICE_HOEDANIGHEID)
                .build();
        var magdaHoedanigheidService = new MagdaHoedanigheidServiceMock(mockedMagdaHoedanigheid);

        return new MagdaConnectorImpl(mockConnection, clientLogService, magdaHoedanigheidService);
    }

    protected MagdaConnectorImpl makeSignedMagdaConnector(
            ClientLogServiceMock clientLogService,
            TwoWaySslProperties signedConnectionRequestSignerKeystore,
            TwoWaySslProperties signedConnectionResponseVerifierKeystore,
            TwoWaySslProperties mockConnectionRequestVerifierKeystore,
            TwoWaySslProperties mockConnectionResponseSignerKeystore)
            throws TwoWaySslException {
        var mockConnection = mockConnection(mockConnectionRequestVerifierKeystore, mockConnectionResponseSignerKeystore, true);
        var signedConnection = new MagdaSignedConnection(mockConnection, signedConnectionRequestSignerKeystore, signedConnectionResponseVerifierKeystore);

        var mockedMagdaHoedanigheid = MagdaRegistrationInfo.builder()
                .identification(TEST_SERVICE_URI)
                .hoedanigheidscode(TEST_SERVICE_HOEDANIGHEID)
                .build();
        var magdaHoedanigheidService = new MagdaHoedanigheidServiceMock(mockedMagdaHoedanigheid);

        return new MagdaConnectorImpl(signedConnection, clientLogService, magdaHoedanigheidService);
    }

    private MagdaMockConnection mockConnection(boolean copyPropertiesFromRequest){
        return mockConnection(null, null, copyPropertiesFromRequest);
    }
    
    private MagdaMockConnection mockConnection(
            TwoWaySslProperties mockConnectionRequestVerifierKeystore,
            TwoWaySslProperties mockConnectionResponseSignerKeystore,
            boolean copyPropertiesFromRequest) {
        var simulatorBuilder = SOAPSimulatorBuilder.builder(finder)
                .magdaMockSimulator(copyPropertiesFromRequest);
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

    protected void assertThatTechnicalFieldsInResponseMatchRequest(MagdaResponse magdaResponse, UUID requestId) {
        var doc = magdaResponse.getDocument();
        assertThatXmlFieldIsEqualTo(doc, RepertoriumTest.ONTVANGER_REFERTE, requestId.toString());
        assertThatXmlFieldIsEqualTo(doc, RepertoriumTest.ONTVANGER_IDENTIFICATIE, TEST_SERVICE_URI);
        assertThatXmlFieldIsEqualTo(doc, RepertoriumTest.ONTVANGER_HOEDANIGHEID, TEST_SERVICE_HOEDANIGHEID);
    }

    protected void assertThatXmlFieldIsEqualTo(MagdaDocument doc, String xmlPath, String expected) {
        var value = doc.getValue(xmlPath);
        assertThat(value).isNotNull();
        assertThat(value).isEqualTo(expected);
    }

    protected void assertThatResponseContainsAnswerNoError(MagdaResponse magdaResponse) {
        assertThat(magdaResponse.isBodyFilledIn()).isTrue();
        assertThat(magdaResponse.isHasContents()).isTrue();
        assertThat(magdaResponse.getResponseUitzonderingEntries()).isEmpty();
        assertThat(magdaResponse.getUitzonderingEntries()).isEmpty();
    }

    protected void assertThatAnswerContainsUitzondering(MagdaResponse magdaResponse) {
        assertThat(magdaResponse.isBodyFilledIn()).isFalse();
        assertThat(magdaResponse.isHasContents()).isFalse();
        assertThat(magdaResponse.getResponseUitzonderingEntries()).isEmpty();
        assertThat(magdaResponse.getUitzonderingEntries()).hasSize(1);
    }

    protected void assertThatTechnicalFieldsAreFilledInCorrectly(MagdaResponse magdaResponse, UUID requestId) {
        log.debug("Response: {}", magdaResponse.getDocument().toString());

        assertThatTechnicalFieldsInResponseMatchRequest(magdaResponse, requestId);
    }
}
