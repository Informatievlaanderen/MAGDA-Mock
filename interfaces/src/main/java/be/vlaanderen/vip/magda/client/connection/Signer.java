package be.vlaanderen.vip.magda.client.connection;

import org.apache.wss4j.dom.message.WSSecHeader;
import org.apache.wss4j.dom.message.WSSecSignature;

public class Signer extends WSSecSignature {

    public Signer(WSSecHeader securityHeader) {
        super(securityHeader);
    }

    public String getCertUri() {
        return this.certUri;
    }
}
