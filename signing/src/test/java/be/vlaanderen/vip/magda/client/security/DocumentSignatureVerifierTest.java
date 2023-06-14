package be.vlaanderen.vip.magda.client.security;

import be.vlaanderen.vip.magda.TestKeyStores;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DocumentSignatureVerifierTest {

    @Test
    @SneakyThrows
    void verificationSucceeds_ifPublicKeyMatchesSignature() {
        var signer = DocumentSigner.fromJksStore(TestKeyStores.mockKeystoreProperties);
        var verifier = DocumentSignatureVerifier.fromJksStore(TestKeyStores.mockKeystoreProperties);

        var doc = MagdaDocument.fromResource(getClass(), "/sample.xml");
        assertNotNull(doc);

        signer.signDocument(doc.getXml());

        assertDoesNotThrow(() -> verifier.verifyDocument(doc.getXml()));
    }

    @Test
    @SneakyThrows
    void verificationFails_ifPublicKeyDoesNotMatchSignature() {
        var signer = DocumentSigner.fromJksStore(TestKeyStores.mockKeystorePropertiesAlt);
        var verifier = DocumentSignatureVerifier.fromJksStore(TestKeyStores.mockKeystoreProperties);

        var doc = MagdaDocument.fromResource(getClass(), "/sample.xml");
        assertNotNull(doc);

        signer.signDocument(doc.getXml());

        assertThrows(InvalidSignatureException.class, () -> verifier.verifyDocument(doc.getXml()));
    }

    @Test
    @SneakyThrows
    void verificationFails_ifDocumentIsNotSigned() {
        var verifier = DocumentSignatureVerifier.fromJksStore(TestKeyStores.mockKeystoreProperties);

        var doc = MagdaDocument.fromResource(getClass(), "/sample.xml");
        assertNotNull(doc);

        assertThrows(InvalidSignatureException.class, () -> verifier.verifyDocument(doc.getXml()));
    }

    @Test
    @SneakyThrows
    void throwsExceptionIfKeystoreIsInaccessible() {
        assertThrows(TwoWaySslException.class, () -> DocumentSignatureVerifier.fromJksStore(TestKeyStores.mockKeystorePropertiesWithWrongPassword));
    }
}
