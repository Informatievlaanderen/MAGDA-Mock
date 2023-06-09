package be.vlaanderen.vip.magda.client.security;

import org.apache.wss4j.common.WSEncryptionPart;
import org.apache.wss4j.common.WSS4JConstants;
import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.crypto.CryptoType;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.common.token.PKIPathSecurity;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.message.WSSecHeader;
import org.apache.wss4j.dom.util.WSSecurityUtil;
import org.w3c.dom.Document;

import static be.vlaanderen.vip.magda.client.security.TwoWaySslUtil.constructCryptoFromProperties;

public class DocumentSigner {

    public static DocumentSigner fromJksStore(TwoWaySslProperties properties) throws TwoWaySslException {
        return new DocumentSigner(properties, constructCryptoFromProperties(properties));
    }

    private final TwoWaySslProperties keystore;
    private final Crypto crypto;

    private DocumentSigner(TwoWaySslProperties keystore, Crypto crypto) {
        this.keystore = keystore;
        this.crypto = crypto;
    }

    public void signDocument(Document xml) throws WSSecurityException {
        var secHeader = new WSSecHeader(xml);

        secHeader.insertSecurityHeader();

        var bst = new PKIPathSecurity(xml);
        var cryptoType = new CryptoType(CryptoType.TYPE.ALIAS);
        cryptoType.setAlias(keystore.getKeyAlias());
        var certs = crypto.getX509Certificates(cryptoType);
        bst.setX509Certificates(certs, crypto);

        var sign = new Signer(secHeader);
        sign.setUserInfo(keystore.getKeyAlias(), keystore.getKeyPassword());
        sign.setIncludeSignatureToken(true);
        sign.setSignatureAlgorithm(WSS4JConstants.RSA_SHA1);
        sign.setSigCanonicalization(WSS4JConstants.C14N_EXCL_OMIT_COMMENTS);
        sign.setKeyIdentifierType(WSConstants.BST_DIRECT_REFERENCE);
        sign.setUseSingleCertificate(false);

        // Add SOAP Body
        var soapNamespace = WSSecurityUtil.getSOAPNamespace(xml.getDocumentElement());
        var encP = new WSEncryptionPart(WSS4JConstants.ELEM_BODY, soapNamespace, "");
        sign.getParts().add(encP);

        sign.prepare(crypto);

        var certUri = sign.getCertUri();
        bst.setID(certUri);

        WSSecurityUtil.prependChildElement(secHeader.getSecurityHeaderElement(), bst.getElement());

        var referenceList = sign.addReferencesToSign(sign.getParts());
        sign.computeSignature(referenceList, false, null);
    }
}
