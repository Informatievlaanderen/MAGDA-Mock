package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.client.connection.MagdaConnection;
import be.vlaanderen.vip.magda.config.MagdaConfigDto;
import be.vlaanderen.vip.magda.exception.MagdaSendFailed;
import org.apache.commons.lang3.StringUtils;
import org.apache.wss4j.common.WSEncryptionPart;
import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.crypto.CryptoFactory;
import org.apache.wss4j.common.crypto.CryptoType;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.common.token.PKIPathSecurity;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.engine.WSSConfig;
import org.apache.wss4j.dom.engine.WSSecurityEngine;
import org.apache.wss4j.dom.handler.WSHandlerResult;
import org.apache.wss4j.dom.message.WSSecHeader;
import org.apache.wss4j.dom.util.WSSecurityUtil;
import org.w3c.dom.Document;

import java.util.Properties;

public class MagdaSignedConnection implements MagdaConnection {
    private final MagdaConnection magdaConnection;
    private final MagdaConfigDto config;
    private final Crypto crypto;
    private boolean cryptoActive;

    public MagdaSignedConnection(MagdaConnection magdaConnection,
                                 MagdaConfigDto config) throws WSSecurityException {
        this.magdaConnection = magdaConnection;
        this.config = config;
        this.cryptoActive = StringUtils.isNotEmpty(config.getKeystore().getKeyStoreLocation());

        if (cryptoActive) {
            crypto = makeCrypto(config);
        } else {
            crypto = null;
        }
    }

    public void signDocument(Document xml) throws WSSecurityException {
        WSSecHeader secHeader = new WSSecHeader(xml);

        secHeader.insertSecurityHeader();

        PKIPathSecurity bst = new PKIPathSecurity(xml);
        CryptoType cryptoType = new CryptoType(CryptoType.TYPE.ALIAS);
        cryptoType.setAlias(config.getKeystore().getKeyAlias());
        var certs = crypto.getX509Certificates(cryptoType);
        bst.setX509Certificates(certs, crypto);

        Signer sign = new Signer(secHeader);
        sign.setUserInfo(config.getKeystore().getKeyAlias(), config.getKeystore().getKeyPassword());
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

    public WSHandlerResult verify(Document doc) throws WSSecurityException {
        WSSecurityEngine secEngine = new WSSecurityEngine();
        doc.normalizeDocument();
        return secEngine.processSecurityHeader(doc, null, null, crypto);
    }

    @Override
    public Document sendDocument(Aanvraag aanvraag, Document xml) throws MagdaSendFailed {
        if (!cryptoActive) {
            // if crypto is not active, we just need to send it without signing and verifying
            return magdaConnection.sendDocument(aanvraag, xml);
        }

        try {
            signDocument(xml);
        } catch (WSSecurityException securityException) {
            throw new MagdaSendFailed("Kan MAGDA aanvraag niet signen", securityException);
        }

        Document response = magdaConnection.sendDocument(aanvraag, xml);

        try {
            verify(response);
        } catch (WSSecurityException securityException) {
            throw new MagdaSendFailed("MAGDA antwoord niet correct gesigned", securityException);
        }

        return response;
    }

    private Crypto makeCrypto(MagdaConfigDto config) throws WSSecurityException {
        WSSConfig.init();
        var props = new Properties();
        props.setProperty("org.apache.wss4j.crypto.provider", "org.apache.wss4j.common.crypto.Merlin");
        props.setProperty("org.apache.wss4j.crypto.merlin.keystore.type", "jks");
        props.setProperty("org.apache.wss4j.crypto.merlin.keystore.password", config.getKeystore().getKeyStorePassword());
        props.setProperty("org.apache.wss4j.crypto.merlin.keystore.alias", config.getKeystore().getKeyAlias());
        props.setProperty("org.apache.wss4j.crypto.merlin.keystore.file", config.getKeystore().getKeyStoreLocation().replace("classpath:", ""));

        return CryptoFactory.getInstance(props);
    }
}
