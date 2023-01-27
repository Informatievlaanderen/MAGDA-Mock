package be.vlaanderen.vip.magda.client.security;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import org.apache.wss4j.common.WSEncryptionPart;
import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.crypto.CryptoType;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.common.token.PKIPathSecurity;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.message.WSSecHeader;
import org.apache.wss4j.dom.util.WSSecurityUtil;
import org.w3c.dom.Document;

public class DocumentSigner {

    public static DocumentSigner fromJksStore(TwoWaySslProperties properties) throws WSSecurityException {
        return new DocumentSigner(properties, properties.toCrypto());
    }

    private final TwoWaySslProperties keystore;
    private final Crypto crypto;

    private DocumentSigner(TwoWaySslProperties keystore, Crypto crypto) {
        this.keystore = keystore;
        this.crypto = crypto;
    }

    public void signDocument(MagdaDocument magdaDocument) throws WSSecurityException {
        signDocument(magdaDocument.getXml());
    }

    public void signDocument(Document xml) throws WSSecurityException {
        WSSecHeader secHeader = new WSSecHeader(xml);

        secHeader.insertSecurityHeader();

        PKIPathSecurity bst = new PKIPathSecurity(xml);
        CryptoType cryptoType = new CryptoType(CryptoType.TYPE.ALIAS);
        cryptoType.setAlias(keystore.getKeyAlias());
        var certs = crypto.getX509Certificates(cryptoType);
        bst.setX509Certificates(certs, crypto);

        Signer sign = new Signer(secHeader);
        sign.setUserInfo(keystore.getKeyAlias(), keystore.getKeyPassword());
        sign.setIncludeSignatureToken(true);
        sign.setSignatureAlgorithm(WSConstants.RSA_SHA1);
        sign.setSigCanonicalization(WSConstants.C14N_EXCL_OMIT_COMMENTS);
        sign.setKeyIdentifierType(WSConstants.BST_DIRECT_REFERENCE);
        sign.setUseSingleCertificate(false);

        // Add SOAP Body
        String soapNamespace = WSSecurityUtil.getSOAPNamespace(xml.getDocumentElement());
        WSEncryptionPart encP = new WSEncryptionPart(WSConstants.ELEM_BODY, soapNamespace, "");
        sign.getParts().add(encP);

        sign.prepare(crypto);

        String certUri = sign.getCertUri();
        bst.setID(certUri);

        WSSecurityUtil.prependChildElement(secHeader.getSecurityHeaderElement(), bst.getElement());

        var referenceList = sign.addReferencesToSign(sign.getParts());
        sign.computeSignature(referenceList, false, null);
    }
}
