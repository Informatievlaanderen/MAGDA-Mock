package be.vlaanderen.vip.magda.client.security;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.exception.TwoWaySslException;
import lombok.extern.slf4j.Slf4j;
import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.engine.WSSecurityEngine;
import org.w3c.dom.Document;

@Slf4j
public class DocumentSignatureVerifier {

    public static DocumentSignatureVerifier fromJksStore(TwoWaySslProperties properties) throws TwoWaySslException {
        return new DocumentSignatureVerifier(properties.toCrypto());
    }

    private Crypto crypto;

    private DocumentSignatureVerifier(Crypto crypto) {
        this.crypto = crypto;
    }

    public void verifyDocument(MagdaDocument aanvraag) throws InvalidSignatureException {
        verifyDocument(aanvraag.getXml());
    }

    public void verifyDocument(Document doc) throws InvalidSignatureException {
        try {
            log.info("Verifying signature...");

            var secEngine = new WSSecurityEngine();
            doc.normalizeDocument();
            var verification = secEngine.processSecurityHeader(doc, null, null, crypto);

            if(verification == null) {
                throw new InvalidSignatureException("Document is not signed");
            }
        } catch(WSSecurityException e) {
            throw new InvalidSignatureException("Document signature verification failed", e);
        }
    }
}
