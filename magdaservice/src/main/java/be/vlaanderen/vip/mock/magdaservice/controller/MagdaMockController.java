package be.vlaanderen.vip.mock.magdaservice.controller;


import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.security.TwoWaySslProperties;
import be.vlaanderen.vip.magda.exception.MagdaSendFailed;
import be.vlaanderen.vip.mock.magda.client.MagdaMockConnection;
import be.vlaanderen.vip.mock.magdaservice.config.RegistratieConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_XML;
import static org.springframework.util.MimeTypeUtils.APPLICATION_XML_VALUE;
import static org.springframework.util.MimeTypeUtils.TEXT_XML_VALUE;

@RestController
@Slf4j
public class MagdaMockController {

    // Gemeenschappelijk endpoint voor alle MAGDA SOAP 2.0 webservices
    private static final String MAGDA_SOAP_02_00 = "Magda-02.00/soap/WebService";

    private MagdaMockConnection mockConnection;

    @Autowired
    public MagdaMockController(RegistratieConfig config) throws WSSecurityException {
        var keystore = readKeystoreProperties(config);

        try {
            log.info("Initializing mock connection...");
            mockConnection = new MagdaMockConnection(keystore, keystore);
        } catch (WSSecurityException e) {
            log.error("Failed to initialize the crypto engine!");
            throw e;
        }
    }

    @PostMapping(value = MAGDA_SOAP_02_00, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> magdaSoap0200WebService(@RequestBody(required = true) String request) throws MagdaSendFailed {
        return processMagdaMockRequest(request);
    }


    private ResponseEntity<String> processMagdaMockRequest(String request) throws MagdaSendFailed {
        //TODO: handle request parsing errors and return Magda Uitzondering error
        MagdaDocument aanvraag = MagdaDocument.fromString(request);

        var antwoord = mockConnection.sendDocument(aanvraag.getXml());
        if (antwoord != null) {
            var doc = MagdaDocument.fromDocument(antwoord);
            final ResponseEntity<String> response = parseInputstream(doc);

            return response;

        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private TwoWaySslProperties readKeystoreProperties(RegistratieConfig config) {
        log.info("Reading key store from config...");
        if(config.getCertPath() != null) {
            var keystore = new TwoWaySslProperties();
            keystore.setKeyStoreLocation(config.getCertPath());
            keystore.setKeyStorePassword(config.getKeystorePassword());
            keystore.setKeyAlias(config.getKeyAlias());
            keystore.setKeyPassword(config.getKeyPassword());

            return keystore;
        } else {
            log.warn("Key store was not configured.");
            return null;
        }
    }

    private ResponseEntity<String> parseInputstream(MagdaDocument magdaDocument) {
        if (magdaDocument != null) {
              return ResponseEntity.ok().contentType(APPLICATION_XML).body(magdaDocument.toString());
        } else {
            log.error("Could not find XML");

            // TODO: maak en return MAGDA Uitzondering antwoord
            return ResponseEntity.notFound().build();
        }
    }
}
