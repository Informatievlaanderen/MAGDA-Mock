package be.vlaanderen.vip.mock.magdaservice.controller;


import static java.security.KeyStore.getInstance;
import static org.springframework.http.MediaType.APPLICATION_XML;
import static org.springframework.util.MimeTypeUtils.APPLICATION_XML_VALUE;
import static org.springframework.util.MimeTypeUtils.TEXT_XML_VALUE;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.wss4j.common.crypto.Crypto;
import org.apache.wss4j.common.crypto.CryptoFactory;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.apache.wss4j.dom.engine.WSSConfig;
import org.apache.wss4j.dom.engine.WSSecurityEngine;
import org.apache.wss4j.dom.engine.WSSecurityEngineResult;
import org.apache.wss4j.dom.handler.WSHandlerResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.exception.MagdaSendFailed;
import be.vlaanderen.vip.mock.magda.client.MagdaMockConnection;
import be.vlaanderen.vip.mock.magdaservice.config.RegistratieConfig;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class MagdaMockController {

    // Gemeenschappelijk endpoint voor alle MAGDA SOAP 2.0 webservices
    private static final String MAGDA_SOAP_02_00 = "Magda-02.00/soap/WebService";

    private final MagdaMockConnection mockConnection;
    private KeyStore keyStore;
    private Crypto crypto;

    private HashSet<String> interestingSecurityAttributes = new HashSet<>();

    @Autowired
    public MagdaMockController(RegistratieConfig config) {
        mockConnection = new MagdaMockConnection();

        interestingSecurityAttributes.add("canonicalization-method");
        interestingSecurityAttributes.add("signature-method");
        interestingSecurityAttributes.add("principal");
        interestingSecurityAttributes.add("validated-token");
        interestingSecurityAttributes.add("id");
        interestingSecurityAttributes.add("binary-security-token");
        interestingSecurityAttributes.add("x509-certificate");

        log.info("Initializing crypto...");
        WSSConfig.init();
        Properties props = new Properties();
        props.setProperty("org.apache.wss4j.crypto.provider", "org.apache.wss4j.common.crypto.Merlin");
        props.setProperty("org.apache.wss4j.crypto.merlin.keystore.type", "jks");
        props.setProperty("org.apache.wss4j.crypto.merlin.keystore.password", config.getKeystorePassword());
        props.setProperty("org.apache.wss4j.crypto.merlin.keystore.alias", config.getKeyAlias());
        props.setProperty("org.apache.wss4j.crypto.merlin.keystore.file", config.getCertPath());

        try {
            crypto = CryptoFactory.getInstance(props);
        } catch (WSSecurityException e) {
            log.error("Can't get instance of Crypto engine", e);
        }
        log.info("Initialized crypto...");
        log.info(String.format("Loading keystore from %s", config.getCertPath()));

        File keystoreUrl = new File(config.getCertPath());

        try {
            keyStore = getInstance("jks");
        } catch (KeyStoreException e) {
            log.error("Can't get instance of JKS keystore", e);
        }

        try {
            keyStore.load(new FileInputStream(keystoreUrl), config.getKeystorePassword().toCharArray());
        } catch (IOException e) {
            log.error("Can't load keystore from " + keystoreUrl.toString(), e);
        } catch (NoSuchAlgorithmException e) {
            log.error("Can't verify integrity of keystore from " + keystoreUrl.toString(), e);
        } catch (CertificateException e) {
            log.error("Can't load certificates from from keystore" + keystoreUrl.toString(), e);
        }
        log.info(String.format("Loaded keystore from %s", config.getCertPath()));
    }

    @PostMapping(value = MAGDA_SOAP_02_00, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> magdaSoap0200WebService(@RequestBody(required = true) String request) throws MagdaSendFailed {
        return processMagdaMockRequest(request);
    }


    private ResponseEntity<String> processMagdaMockRequest(String request) throws MagdaSendFailed {
        //TODO: handle request parsing errors and return Magda Uitzondering error
        MagdaDocument aanvraag = MagdaDocument.fromString(request);

        verifySignatureOf(aanvraag);

        var antwoord = mockConnection.sendDocument(aanvraag.getXml());
        if (antwoord != null) {
            var doc = MagdaDocument.fromDocument(antwoord);
            final ResponseEntity<String> response = parseInputstream(doc);

            return response;

        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private void verifySignatureOf(MagdaDocument aanvraag) {
        if (crypto != null && keyStore != null) {
            try {
                log.info("Verifying signature...");
                final WSHandlerResult verify = verify(aanvraag.getXml());
                if (verify != null) {
                    final List<WSSecurityEngineResult> results = verify.getResults();
                    for (WSSecurityEngineResult result : results) {
                        for (Map.Entry<String, Object> entry : result.entrySet()) {
                            if (interestingSecurityAttributes.contains(entry.getKey())) {
                                log.info(String.format("Security attribute '%s'='%s", entry.getKey(), entry.getValue()));
                            }
                        }
                    }
                } else {
                    log.error("Signature verification gefaald. Geen resultaat");
                }
            } catch (WSSecurityException e) {
                log.error("Signature verification gefaald", e);
            }
        }
    }

    private ResponseEntity<String> parseInputstream(MagdaDocument magdaDocument) {
        if (magdaDocument != null) {
              return ResponseEntity.ok().contentType(APPLICATION_XML).body(magdaDocument.toString());
        } else {
            log.error(
                    String.format("Could not find XML"));

            // TODO: maak en return MAGDA Uitzondering antwoord
            return ResponseEntity.notFound().build();
        }
    }

    private WSHandlerResult verify(Document doc) throws WSSecurityException {
        WSSecurityEngine secEngine = new WSSecurityEngine();
        doc.normalizeDocument();
        return secEngine.processSecurityHeader(doc, null, null, crypto);
    }
}
