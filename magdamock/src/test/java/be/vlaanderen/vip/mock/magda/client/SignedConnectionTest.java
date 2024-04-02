package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.diensten.GeefBewijsRequest;
import be.vlaanderen.vip.magda.exception.UitzonderingenSectionInResponseException;
import be.vlaanderen.vip.magda.exception.NoResponseException;
import be.vlaanderen.vip.magda.exception.MagdaConnectionException;
import be.vlaanderen.vip.mock.magda.TestKeyStores;
import be.vlaanderen.vip.mock.magda.client.legallogging.ClientLogServiceMock;
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
        var request = GeefBewijsRequest.builder()
                .insz(REQUEST_INSZ)
                .build();

        var clientLogService = new ClientLogServiceMock();
        var connector = makeSignedMagdaConnector(clientLogService,
                TestKeyStores.mockKeystoreProperties,  // connection request signer
                TestKeyStores.mockKeystoreProperties,  // connection response verifier
                TestKeyStores.mockKeystoreProperties,  // simulator request verifier
                TestKeyStores.mockKeystoreProperties); // simulator response signer

        var antwoord = connector.send(request);
        log.info("{}", antwoord.getDocument());

        assertThat(antwoord.isBodyFilledIn()).isTrue();
        assertThat(antwoord.isHasContents()).isTrue();
        assertThat(antwoord.getUitzonderingEntries()).isEmpty();
        assertThat(antwoord.getResponseUitzonderingEntries()).isEmpty();

        assertThat(clientLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfFailedLoggedRequests()).isZero();

        var doc = antwoord.getDocument();

        assertEquals(REQUEST_INSZ, doc.getValue("//Antwoorden/Antwoord/Inhoud/Bewijzen/Bewijs/PersoonUitgereikt/INSZ"));
    }

    @Test
    @SneakyThrows
    void respondsAsNormal_IfNoKeystoresAreUsedAnywhere() {
        var request = GeefBewijsRequest.builder()
                .insz(REQUEST_INSZ)
                .build();

        var clientLogService = new ClientLogServiceMock();
        var connector = makeSignedMagdaConnector(clientLogService,
                null,  // connection request signer
                null,  // connection response verifier
                null,  // simulator request verifier
                null); // simulator response signer

        var antwoord = connector.send(request);
        log.info("{}", antwoord.getDocument());

        assertThat(antwoord.isBodyFilledIn()).isTrue();
        assertThat(antwoord.isHasContents()).isTrue();
        assertThat(antwoord.getUitzonderingEntries()).isEmpty();
        assertThat(antwoord.getResponseUitzonderingEntries()).isEmpty();

        assertThat(clientLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfFailedLoggedRequests()).isZero();

        var doc = antwoord.getDocument();

        assertEquals(REQUEST_INSZ, doc.getValue("//Antwoorden/Antwoord/Inhoud/Bewijzen/Bewijs/PersoonUitgereikt/INSZ"));
    }

    @Test
    @SneakyThrows
    void throwsUitzonderingSectionInResponseException_IfRequestSignatureIsUnexpected() {
        var request = GeefBewijsRequest.builder()
                .insz(REQUEST_INSZ)
                .build();

        var clientLogService = new ClientLogServiceMock();
        var connector = makeSignedMagdaConnector(clientLogService,
                TestKeyStores.mockKeystorePropertiesAlt,  // connection request signer
                TestKeyStores.mockKeystoreProperties,      // connection response verifier
                TestKeyStores.mockKeystoreProperties,      // simulator request verifier
                TestKeyStores.mockKeystoreProperties);     // simulator response signer

        try {
            connector.send(request);
            fail("No exception was thrown");
        } catch(UitzonderingenSectionInResponseException e) {
            assertEquals(REQUEST_INSZ, e.getSubject().getValue());
            assertNotNull(e.getRequestId());
            assertEquals(1, e.getUitzonderingEntries().size());

            var uitzondering = e.getUitzonderingEntries().get(0);
            assertEquals("SOAP FAULT", uitzondering.getIdentification());
            assertEquals("MAGDA", uitzondering.getOrigin());

            var errorDocument = MagdaDocument.fromString(uitzondering.getDiagnosis());
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
    void throwsUitzonderingSectionInResponseException_IfRequestIsNotSigned() {
        var request = GeefBewijsRequest.builder()
                .insz(REQUEST_INSZ)
                .build();

        var clientLogService = new ClientLogServiceMock();
        var connector = makeSignedMagdaConnector(clientLogService,
                null,  // connection request signer
                TestKeyStores.mockKeystoreProperties,  // connection response verifier
                TestKeyStores.mockKeystoreProperties,  // simulator request verifier
                TestKeyStores.mockKeystoreProperties); // simulator response signer

        try {
            connector.send(request);
            fail("No exception was thrown");
        } catch(UitzonderingenSectionInResponseException e) {
            assertNotNull(e.getRequestId());
            assertEquals(REQUEST_INSZ, e.getSubject().getValue());
            assertEquals(1, e.getUitzonderingEntries().size());

            var uitzondering = e.getUitzonderingEntries().get(0);
            assertEquals("SOAP FAULT", uitzondering.getIdentification());
            assertEquals("MAGDA", uitzondering.getOrigin());

            var errorDocument = MagdaDocument.fromString(uitzondering.getDiagnosis());
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
    void throwsNoResponseException_IfResponseSignatureIsUnexpected() {
        var request = GeefBewijsRequest.builder()
                .insz(REQUEST_INSZ)
                .build();

        var clientLogService = new ClientLogServiceMock();
        var connector = makeSignedMagdaConnector(clientLogService,
                TestKeyStores.mockKeystoreProperties,      // connection request signer
                TestKeyStores.mockKeystoreProperties,      // connection response verifier
                TestKeyStores.mockKeystoreProperties,      // simulator request verifier
                TestKeyStores.mockKeystorePropertiesAlt); // simulator response signer

        try {
            connector.send(request);
            fail("No exception was thrown");
        } catch(NoResponseException e) {
            assertInstanceOf(MagdaConnectionException.class, e.getCause());
        } catch(Exception ex) {
            fail("Unexpected exception was thrown: " + ex);
        }
    }

    @Test
    @SneakyThrows
    void throwsNoResponseException_IfResponseIsNotSigned() {
        var request = GeefBewijsRequest.builder()
                .insz(REQUEST_INSZ)
                .build();

        var clientLogService = new ClientLogServiceMock();
        var connector = makeSignedMagdaConnector(clientLogService,
                TestKeyStores.mockKeystoreProperties, // connection request signer
                TestKeyStores.mockKeystoreProperties, // connection response verifier
                TestKeyStores.mockKeystoreProperties, // simulator request verifier
                null);                    // simulator response signer

        try {
            connector.send(request);
            fail("No exception was thrown");
        } catch(NoResponseException e) {
            assertInstanceOf(MagdaConnectionException.class, e.getCause());
        } catch(Exception ex) {
            fail("Unexpected exception was thrown: " + ex);
        }
    }
}
