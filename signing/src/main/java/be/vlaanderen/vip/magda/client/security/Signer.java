package be.vlaanderen.vip.magda.client.security;


import org.apache.wss4j.dom.message.WSSecHeader;
import org.apache.wss4j.dom.message.WSSecSignature;

class Signer extends WSSecSignature {

    public Signer(WSSecHeader securityHeader) {
        super(securityHeader);
    }

    public String getCertUri() {
        return this.certUri;
    }
}
