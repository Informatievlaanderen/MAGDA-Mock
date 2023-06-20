package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.diensten.GeefBewijsRequest;
import be.vlaanderen.vip.magda.exception.BackendUitzonderingenException;
import be.vlaanderen.vip.magda.exception.GeenAntwoordException;
import be.vlaanderen.vip.magda.exception.MagdaConnectionException;
import be.vlaanderen.vip.mock.magda.TestKeyStores;
import be.vlaanderen.vip.mock.magda.client.legallogging.AfnemerLogServiceMock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class SignedConnectionTest extends MockTestBase {

    private static final String REQUEST_INSZ = "67621546751";

    @Test
    @SneakyThrows
    void respondsAsNormal_IfRequestSignatureIsValid() {
        var request = new GeefBewijsRequest(REQUEST_INSZ);

        var afnemerLogService = new AfnemerLogServiceMock();
        var connector = makeSignedMagdaConnector(afnemerLogService,
                TestKeyStores.mockKeystoreProperties,  // connection request signer
                TestKeyStores.mockKeystoreProperties,  // connection response verifier
                TestKeyStores.mockKeystoreProperties,  // simulator request verifier
                TestKeyStores.mockKeystoreProperties); // simulator response signer

        var antwoord = connector.send(request);
        log.info("{}", antwoord.getDocument());

        assertThat(antwoord.isBodyIngevuld()).isTrue();
        assertThat(antwoord.isHeeftInhoud()).isTrue();
        assertThat(antwoord.getUitzonderingen()).isEmpty();
        assertThat(antwoord.getAntwoordUitzonderingen()).isEmpty();

        assertThat(afnemerLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfFailedLoggedRequests()).isZero();

        var doc = antwoord.getDocument();

        assertEquals(REQUEST_INSZ, doc.getValue("//Antwoorden/Antwoord/Inhoud/Bewijzen/Bewijs/PersoonUitgereikt/INSZ"));
    }

    @Test
    @SneakyThrows
    void respondsAsNormal_IfNoKeystoresAreUsedAnywhere() {
        var request = new GeefBewijsRequest(REQUEST_INSZ);

        var afnemerLogService = new AfnemerLogServiceMock();
        var connector = makeSignedMagdaConnector(afnemerLogService,
                null,  // connection request signer
                null,  // connection response verifier
                null,  // simulator request verifier
                null); // simulator response signer

        var antwoord = connector.send(request);
        log.info("{}", antwoord.getDocument());

        assertThat(antwoord.isBodyIngevuld()).isTrue();
        assertThat(antwoord.isHeeftInhoud()).isTrue();
        assertThat(antwoord.getUitzonderingen()).isEmpty();
        assertThat(antwoord.getAntwoordUitzonderingen()).isEmpty();

        assertThat(afnemerLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(afnemerLogService.getNumberOfFailedLoggedRequests()).isZero();

        var doc = antwoord.getDocument();

        assertEquals(REQUEST_INSZ, doc.getValue("//Antwoorden/Antwoord/Inhoud/Bewijzen/Bewijs/PersoonUitgereikt/INSZ"));
    }

    @Test
    @SneakyThrows
    void respondsWithBackendUitzonderingenException_IfRequestSignatureIsUnexpected() {
        var request = new GeefBewijsRequest(REQUEST_INSZ);

        var afnemerLogService = new AfnemerLogServiceMock();
        var connector = makeSignedMagdaConnector(afnemerLogService,
                TestKeyStores.mockKeystorePropertiesAlt,  // connection request signer
                TestKeyStores.mockKeystoreProperties,      // connection response verifier
                TestKeyStores.mockKeystoreProperties,      // simulator request verifier
                TestKeyStores.mockKeystoreProperties);     // simulator response signer

        try {
            connector.send(request);
            fail("No exception was thrown");
        } catch(BackendUitzonderingenException e) {
            assertEquals(REQUEST_INSZ, e.getInsz());
            assertEquals(1, e.getUitzonderingen().size());

            var uitzondering = e.getUitzonderingen().get(0);
            assertEquals("SOAP FAULT", uitzondering.getIdentificatie());
            assertEquals("MAGDA", uitzondering.getOorsprong());

            var errorDocument = MagdaDocument.fromString(uitzondering.getDiagnose());
            assertEquals("Server",
                    errorDocument.getValue("//soapenv:Envelope/soapenv:Body/soapenv:Fault/faultcode"));
            assertEquals("ERR_025: Verification Failure: Document signature verification failed",
                    errorDocument.getValue("//soapenv:Envelope/soapenv:Body/soapenv:Fault/faultstring"));
        } catch(Exception ex) {
            fail("Unexpected exception was thrown: " + ex);
        }
    }

    @Test
    @SneakyThrows
    void respondsWithBackendUitzonderingenException_IfRequestIsNotSigned() {
        var request = new GeefBewijsRequest(REQUEST_INSZ);

        var afnemerLogService = new AfnemerLogServiceMock();
        var connector = makeSignedMagdaConnector(afnemerLogService,
                null,  // connection request signer
                TestKeyStores.mockKeystoreProperties,  // connection response verifier
                TestKeyStores.mockKeystoreProperties,  // simulator request verifier
                TestKeyStores.mockKeystoreProperties); // simulator response signer

        try {
            connector.send(request);
            fail("No exception was thrown");
        } catch(BackendUitzonderingenException e) {
            assertEquals(REQUEST_INSZ, e.getInsz());
            assertEquals(1, e.getUitzonderingen().size());

            var uitzondering = e.getUitzonderingen().get(0);
            assertEquals("SOAP FAULT", uitzondering.getIdentificatie());
            assertEquals("MAGDA", uitzondering.getOorsprong());

            var errorDocument = MagdaDocument.fromString(uitzondering.getDiagnose());
            assertEquals("Server",
                    errorDocument.getValue("//soapenv:Envelope/soapenv:Body/soapenv:Fault/faultcode"));
            assertEquals("ERR_025: Verification Failure: Document is not signed",
                    errorDocument.getValue("//soapenv:Envelope/soapenv:Body/soapenv:Fault/faultstring"));
        } catch(Exception ex) {
            fail("Unexpected exception was thrown: " + ex);
        }
    }

    @Test
    @SneakyThrows
    void respondsWithGeenAntwoordException_IfResponseSignatureIsUnexpected() {
        var request = new GeefBewijsRequest(REQUEST_INSZ);

        var afnemerLogService = new AfnemerLogServiceMock();
        var connector = makeSignedMagdaConnector(afnemerLogService,
                TestKeyStores.mockKeystoreProperties,      // connection request signer
                TestKeyStores.mockKeystoreProperties,      // connection response verifier
                TestKeyStores.mockKeystoreProperties,      // simulator request verifier
                TestKeyStores.mockKeystorePropertiesAlt); // simulator response signer

        try {
            connector.send(request);
            fail("No exception was thrown");
        } catch(GeenAntwoordException e) {
            assertInstanceOf(MagdaConnectionException.class, e.getCause());
        } catch(Exception ex) {
            fail("Unexpected exception was thrown: " + ex);
        }
    }

    @Test
    @SneakyThrows
    void respondsWithGeenAntwoordException_IfResponseIsNotSigned() {
        var request = new GeefBewijsRequest(REQUEST_INSZ);

        var afnemerLogService = new AfnemerLogServiceMock();
        var connector = makeSignedMagdaConnector(afnemerLogService,
                TestKeyStores.mockKeystoreProperties, // connection request signer
                TestKeyStores.mockKeystoreProperties, // connection response verifier
                TestKeyStores.mockKeystoreProperties, // simulator request verifier
                null);                    // simulator response signer

        try {
            connector.send(request);
            fail("No exception was thrown");
        } catch(GeenAntwoordException e) {
            assertInstanceOf(MagdaConnectionException.class, e.getCause());
        } catch(Exception ex) {
            fail("Unexpected exception was thrown: " + ex);
        }
    }
}
