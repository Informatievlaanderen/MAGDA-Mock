package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.client.security.Signer;
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
import org.apache.wss4j.dom.engine.WSSecurityEngine;
import org.apache.wss4j.dom.handler.WSHandlerResult;
import org.apache.wss4j.dom.message.WSSecHeader;
import org.apache.wss4j.dom.util.WSSecurityUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Properties;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

// Zie https://vlaamseoverheid.atlassian.net/wiki/spaces/MG/pages/1161134185/Gebruik+certificaat+in+SoapUI

public class SigningTest {

    // Self-signed test certificaat + private key in PKCS12 file aangemaakt enkel voor de testen met https://hohnstaedt.de/xca/
    // JKS file aangemaakt vanaf PKCS12 file met behulp van https://keystore-explorer.org/downloads.html
    // Het bijgeleverd script in <project root>/scripts/make_mock_jks.sh kan ook worden gebruikt als alternatief.
    private static final String STORE_PASSWORD = "mijnpaswoord";
    private static final String CERTIFICATE_NAME = "mock x509 certificaat";
    private static final String SUBJECTDN = "EMAILADDRESS=pascal@nayima.be, CN=be.nayima.mock.client, OU=Dev, O=Nayima, L=Zaventem, ST=Vlaams Brabant, C=BE";
    private static final String TYPESTORE_PKCS_12 = "PKCS12";
    private static final String TYPESTORE_JKS = "JKS";

    @BeforeAll
    static void initWSS() {
        WSSConfig.init();
    }

    @SneakyThrows
    @Test
    void signingMetX509Certificaat() {
        try (InputStream resource = this.getClass().getResourceAsStream("/certificates/mock_x509_certificaat.pfx")) {
            var signedDocument = signWithKeystore(TYPESTORE_PKCS_12, resource, STORE_PASSWORD, CERTIFICATE_NAME, SUBJECTDN);
            try (InputStream resource2 = this.getClass().getResourceAsStream("/certificates/mock_x509_certificaat.pfx")) {
                assertDocumentIsSigned(signedDocument, TYPESTORE_PKCS_12, resource2, STORE_PASSWORD);
            }
        }
    }

    @SneakyThrows
    @Test
    void signingMetJKSStore() {
        try (InputStream resource = this.getClass().getResourceAsStream("/certificates/mock keystore.jks")) {
            var signedDocument = signWithKeystore(TYPESTORE_JKS, resource, STORE_PASSWORD, CERTIFICATE_NAME, SUBJECTDN);
            try (InputStream resource2 = this.getClass().getResourceAsStream("/certificates/mock_x509_certificaat.pfx")) {
                assertDocumentIsSigned(signedDocument, TYPESTORE_JKS, resource2, STORE_PASSWORD);
            }
        }
    }

    @SneakyThrows
    private void assertDocumentIsSigned(MagdaDocument signedDocument, String typeStore, InputStream resource, String storePassword) {
        System.out.println(signedDocument.toString());
        var header = signedDocument.xpath("//soapenv:Header");
        assertThat(header.getLength()).isEqualTo(1);
        var bst = signedDocument.xpath("//soapenv:Header/wsse:Security/wsse:BinarySecurityToken");
        assertThat(bst.getLength()).isEqualTo(1);
        var signature = signedDocument.xpath("//soapenv:Header/wsse:Security/ds:Signature");
        assertThat(signature.getLength()).isEqualTo(1);

        Merlin crypto = loadKeystore(typeStore, resource, storePassword);
        var result = verify(crypto,signedDocument.getXml()) ;

        // Signature ok als er een result is en geen WSSecurityException gegooid
        assertThat(result).isNotNull();

    }

    @SneakyThrows
    @Test
    void loadAanvraagZoekPersoonOpAdres() {
        try (InputStream request = this.getClass().getResourceAsStream("/requests/ZoekOpAdres.xml")) {
            var doc = MagdaDocument.fromStream(request);
            var ns = defaultNamespace(doc);
            System.out.println(ns);
            var naam = doc.getValue("//Verzoek/Context/Naam", ns);
            assertThat(naam).isEqualTo("ZoekPersoonOpAdres");
            var versie = doc.getValue("//Verzoek/Context/Versie", ns);
            assertThat(versie).isEqualTo("02.02.0000");
        }
    }

    @SneakyThrows
    @Test
    void loadAanvraagGeefPersoon() {
        try (InputStream request = this.getClass().getResourceAsStream("/requests/GeefPersoon.xml")) {
            var doc = MagdaDocument.fromStream(request);
            var ns = defaultNamespace(doc);
            System.out.println(ns);
            var naam = doc.getValue("//Verzoek/Context/Naam", ns);
            assertThat(naam).isEqualTo("GeefPersoon");
            var versie = doc.getValue("//Verzoek/Context/Versie", ns);
            assertThat(versie).isEqualTo("02.00.0000");
        }
    }


    private MagdaDocument signWithKeystore(String typeStore, InputStream resource, String storePaswoord, String certificaatNaam, String subjectDN) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, WSSecurityException, ParserConfigurationException, SAXException {
        Merlin crypto = loadKeystore(typeStore, resource, storePaswoord);

        CryptoType cryptoType = cryptoTypeSubjectDN(subjectDN);

        try (InputStream request = this.getClass().getResourceAsStream("/requests/GeefPersoon.xml")) {
            var output = signDocument(request, crypto, cryptoType, certificaatNaam, storePaswoord);
            var signedDocument = MagdaDocument.fromDocument(output);
            return signedDocument;
        }
    }


    private Document signDocument(InputStream request, Crypto crypto, CryptoType cryptoType, String certificaatNaam, String storePaswoord) throws ParserConfigurationException, IOException, SAXException, WSSecurityException {
        var requestXML = MagdaDocument.fromStream(request);
        var xml = requestXML.getXml();
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

    private WSHandlerResult verify(Crypto crypto, Document doc) throws WSSecurityException {
        WSSecurityEngine secEngine = new WSSecurityEngine();
        doc.normalizeDocument();
        return secEngine.processSecurityHeader(doc, null, null, crypto);
    }


    String defaultNamespace(MagdaDocument xml) {
        var nodes = xml.xpath("//soapenv:Body");
        for (var i = 0; i < nodes.getLength(); i++) {
            var n = nodes.item(i);
            if (n.getLocalName().equals("Body")) {
                var children = n.getChildNodes();
                for (var j = 0; j < children.getLength(); j++) {
                    var child = children.item(j);
                    if (child.getNamespaceURI() != null) {
                        return child.getNamespaceURI();
                    }
                }
            }
        }
        return null;
    }
}
