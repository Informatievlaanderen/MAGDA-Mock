package be.vlaanderen.vip.magda.client.security;

import be.vlaanderen.vip.magda.TestKeyStores;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import lombok.SneakyThrows;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DocumentSignerTest {

    @Test
    @SneakyThrows
    void addsWSSHeader() {
        var signer = DocumentSigner.fromJksStore(TestKeyStores.mockKeystoreProperties);

        var doc = MagdaDocument.fromResource(getClass(), "/sample.xml");

        assertNull(doc.getValue("//soapenv:Envelope/soapenv:Header/wsse:Security"));

        signer.signDocument(doc);

        assertNotNull(doc.getValue("//soapenv:Envelope/soapenv:Header/wsse:Security"));
    }

    @Test
    @SneakyThrows
    void throwsExceptionIfKeystoreIsInaccessible() {
        assertThrows(WSSecurityException.class, () -> DocumentSigner.fromJksStore(TestKeyStores.mockKeystorePropertiesWithWrongPassword));
    }
}