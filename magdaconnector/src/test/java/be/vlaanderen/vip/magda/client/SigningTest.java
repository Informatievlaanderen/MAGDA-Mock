package be.vlaanderen.vip.magda.client;

import lombok.SneakyThrows;
import org.apache.wss4j.common.WSEncryptionPart;
import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.crypto.CryptoFactory;
import org.apache.wss4j.common.crypto.CryptoType;
import org.apache.wss4j.common.crypto.Merlin;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.common.token.PKIPathSecurity;
import org.apache.wss4j.dom.WSConstants;
import org.apache.wss4j.dom.engine.WSSConfig;
import org.apache.wss4j.dom.message.WSSecHeader;
import org.apache.wss4j.dom.util.WSSecurityUtil;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Properties;

// Zie https://vlaamseoverheid.atlassian.net/wiki/spaces/MG/pages/1161134185/Gebruik+certificaat+in+SoapUI

public class SigningTest {

    // Self-signed test certificaat + private key in PKCS12 file aangemaakt enkel voor de testen met https://hohnstaedt.de/xca/
    // JKS file aangemaakt vanaf PKCS12 file met behulp van https://keystore-explorer.org/downloads.html
    private static final String STORE_PASSWORD = "mijnpaswoord";
    private static final String CERTIFICATE_NAME = "mock x509 certificaat";
    private static final String SUBJECTDN = "EMAILADDRESS=pascal@nayima.be, CN=be.nayima.mock.client, OU=Dev, O=Nayima, L=Zaventem, ST=Vlaams Brabant, C=BE";

    @SneakyThrows
    @Test
    void test2() {
        WSSConfig.init();

        try (InputStream resource = this.getClass().getResourceAsStream("/certificates/mock_x509_certificaat.pfx")) {
            signWithKeystore("PKCS12", resource, STORE_PASSWORD, CERTIFICATE_NAME, SUBJECTDN);
        }
    }


    @SneakyThrows
    @Test
    void test3() {
        WSSConfig.init();

        try (InputStream resource = this.getClass().getResourceAsStream("/certificates/mock keystore.jks")) {
            signWithKeystore("JKS", resource, STORE_PASSWORD, CERTIFICATE_NAME, SUBJECTDN);
        }
    }


    private void signWithKeystore(String typeStore, InputStream resource, String storePaswoord, String certificaatNaam, String subjectDN) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, WSSecurityException, ParserConfigurationException, SAXException {
        Merlin crypto = loadKeystore(typeStore, resource, storePaswoord);

        CryptoType cryptoType = cryptoTypeSubjectDN(subjectDN);

        try (InputStream request = this.getClass().getResourceAsStream("/requests/GeefPersoon.xml")) {
            var output = signDocument(request, crypto, cryptoType, certificaatNaam, storePaswoord);
            System.out.println(xmlToString(output));
        }
    }

    private Document signDocument(InputStream request, Crypto crypto, CryptoType cryptoType, String certificaatNaam, String storePaswoord) throws ParserConfigurationException, IOException, SAXException, WSSecurityException {
        var xml = parse(request);
        WSSecHeader secHeader = new WSSecHeader(xml);

        secHeader.insertSecurityHeader();

        PKIPathSecurity bst = new PKIPathSecurity(xml);

        var certs = crypto.getX509Certificates(cryptoType);

        bst.setX509Certificates(certs, crypto);

        Signer sign = new Signer(secHeader);
        sign.setUserInfo(certificaatNaam, storePaswoord);
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
        return xml;
    }

    private CryptoType cryptoTypeSubjectDN(String subjectDN) {
        CryptoType cryptoType = new CryptoType(CryptoType.TYPE.SUBJECT_DN);
        cryptoType.setSubjectDN(subjectDN);
        return cryptoType;
    }

    private Merlin loadKeystore(String typeStore, InputStream resource, String storePaswoord) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, WSSecurityException {
        KeyStore store = KeyStore.getInstance(typeStore);
        store.load(resource, storePaswoord.toCharArray());

        Properties properties = new Properties();
        properties.setProperty("org.apache.ws.security.crypto.provider", "org.apache.wss4j.common.crypto.Merlin");

        Merlin crypto = (Merlin) CryptoFactory.getInstance(properties);
        crypto.setKeyStore(store);
        return crypto;
    }

    private static String xmlToString(Document xml) {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(xml), new StreamResult(writer));
            return writer.toString();
        } catch (TransformerException e) {
            System.err.println("Fout bij omzetten van XML naar string: " + e);
        }
        return "";
    }

    Document parse(InputStream resource) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setExpandEntityReferences(false);
        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setValidating(false);
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(resource);

        return document;
    }

}
