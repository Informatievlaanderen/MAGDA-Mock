package be.vlaanderen.vip.magda.client.security;

import be.vlaanderen.vip.magda.TestKeyStores;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.exception.TwoWaySslException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DocumentSignatureVerifierTest {

    @Test
    @SneakyThrows
    void verificationSucceeds_ifPublicKeyMatchesSignature() {
        var signer = DocumentSigner.fromJksStore(TestKeyStores.mockKeystoreProperties);
        var verifier = DocumentSignatureVerifier.fromJksStore(TestKeyStores.mockKeystoreProperties);

        var doc = MagdaDocument.fromResource(getClass(), "/sample.xml");

        signer.signDocument(doc);

        assertDoesNotThrow(() -> verifier.verifyDocument(doc));
    }

    @Test
    @SneakyThrows
    void verificationFails_ifPublicKeyDoesNotMatchSignature() {
        var signer = DocumentSigner.fromJksStore(TestKeyStores.mockKeystorePropertiesAlt);
        var verifier = DocumentSignatureVerifier.fromJksStore(TestKeyStores.mockKeystoreProperties);

        var doc = MagdaDocument.fromResource(getClass(), "/sample.xml");

        signer.signDocument(doc);

        assertThrows(InvalidSignatureException.class, () -> verifier.verifyDocument(doc));
    }

    @Test
    @SneakyThrows
    void verificationFails_ifDocumentIsNotSigned() {
        var verifier = DocumentSignatureVerifier.fromJksStore(TestKeyStores.mockKeystoreProperties);

        var doc = MagdaDocument.fromResource(getClass(), "/sample.xml");

        assertThrows(InvalidSignatureException.class, () -> verifier.verifyDocument(doc));
    }

    @Test
    @SneakyThrows
    void throwsExceptionIfKeystoreIsInaccessible() {
        assertThrows(TwoWaySslException.class, () -> DocumentSignatureVerifier.fromJksStore(TestKeyStores.mockKeystorePropertiesWithWrongPassword));
    }
}
