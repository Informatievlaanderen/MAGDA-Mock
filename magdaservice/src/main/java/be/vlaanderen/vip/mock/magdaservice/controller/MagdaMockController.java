package be.vlaanderen.vip.mock.magdaservice.controller;


import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.exception.MagdaSendFailed;
import be.vlaanderen.vip.mock.magda.client.MagdaMockConnection;
import be.vlaanderen.vip.mock.magdaservice.config.MagdaMockConfig;
import be.vlaanderen.vip.mock.magdaservice.config.RegistratieConfig;
import be.vlaanderen.vip.mock.magdaservice.exception.AttestNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
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

import java.io.*;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.*;

import static java.security.KeyStore.getInstance;
import static org.springframework.http.MediaType.APPLICATION_XML;
import static org.springframework.util.MimeTypeUtils.APPLICATION_XML_VALUE;
import static org.springframework.util.MimeTypeUtils.TEXT_XML_VALUE;

@RestController
@Slf4j
public class MagdaMockController {

    // Gemeenschappelijk endpoint voor alle MAGDA SOAP 2.0 webservices
    private static final String MAGDA_SOAP_02_00 = "Magda-02.00/soap/WebService";

    // Alle onderstaande individuele endpoints voor MAGDA SOAP Services zijn deprecated en vervangen door magdaSoap0200WebService
    // START DEPRECATED

    // KADASTER
    private static final String ZOEK_EIGENDOMSTOESTANDEN_SOAP_WEB_SERVICE = "ZoekEigendomstoestandenDienst-02.00/soap/WebService";

    private static final String GEEF_EPC_SOAP_WEB_SERVICE = "GeefEpcDienst-02.00/soap/WebService"; // TODO: bevestig naam endpoint

    // REPERTORIUM
    private static final String REGISTREER_INSCHRIJVING_SOAP_WEB_SERVICE = "RegistreerInschrijvingDienst-02.00/soap/WebService";
    private static final String REGISTREER_UITSCHRIJVING_SOAP_WEB_SERVICE = "RegistreerUitschrijvingDienst-02.00/soap/WebService";
    private static final String REGISTREER_INSCHRIJVING_21_SOAP_WEB_SERVICE = "RegistreerInschrijvingDienst-02.01/soap/WebService";

    // ONDERNEMING
    private static final String GEEF_ONDERNEMING_SOAP_WEB_SERVICE = "GeefOndernemingDienst-02.00/soap/WebService";
    private static final String GEEF_ONDERNEMINGVKBO_SOAP_WEB_SERVICE = "GeefOndernemingVKBODienst-02.00/soap/WebService";
    private static final String GEEF_FUNCTIES_SOAP_WEB_SERVICE = "GeefFunctiesDienst-02.00/soap/WebService";
    // PERSOON
    private static final String GEEF_PERSOON_0202_SOAP_WEB_SERVICE = "GeefPersoonDienst-02.02/soap/WebService";
    private static final String GEEF_HISTORIEK_PERSOON_SOAP_WEB_SERVICE = "GeefHistoriekPersoonDienst-02.00/soap/WebService";
    private static final String GEEF_GEZINSSAMENSTELLING_SOAP_WEB_SERVICE = "GeefGezinssamenstellingDienst-02.00/soap/WebService";
    private static final String GEEF_PASFOTO_SOAP_WEB_SERVICE = "GeefPasfotoDienst-02.00/soap/WebService";

    // LED
    private static final String GEEF_BEWIJS_SOAP_WEB_SERVICE = "GeefBewijsDienst-02.00/soap/WebService";
    // INBURGERING
    private static final String GEEF_DOSSIER_KBI_SOAP_WEB_SERVICE = "GeefDossierKBIDienst-01.00/soap/WebService";
    // ONDERWIJS
    private static final String GEEF_HISTORIEK_INSCHRIJVING_SOAP_WEB_SERVICE = "GeefHistoriekInschrijvingDienst-02.00/soap/WebService";
    private static final String GEEF_HISTORIEK_INSCHRIJVING_21_SOAP_WEB_SERVICE = "GeefHistoriekInschrijvingDienst-02.01/soap/WebService";
    // WERK
    private static final String GEEF_LOOPBAAN_ONDERBREKINGEN_SOAP_WEB_SERVICE = "GeefLoopbaanonderbrekingenDienst-02.00/soap/WebService";

    // DOSSIER
    private static final String GEEF_DOSSIER_SOAP_WEB_SERVICE = "GeefDossiersDienst-02.00/soap/WebService";

    // TODO: endpoint niet gedocumenteerd
    private static final String GEEF_STATUS_RECHT_ONDERSTEUNINGEN_SOAP_WEB_SERVICE = "GeefStatusRechtOndersteuningenDienst-02.00/soap/WebService";
    // TODO: endpoint niet gedocumenteerd
    private static final String GEEF_ATTEST_SOAP_WEB_SERVICE = "GeefAttestDienst-02.00/soap/WebService";
    // TODO
    private static final String GEEF_AUDITLOGGING_SOAP_WEB_SERVICE_INCORRECT = "GeefAuditLogging-02.00/soap/WebService";
    private static final String GEEF_AUDITLOGGING_SOAP_WEB_SERVICE = "GeefAuditLoggingDienst-02.00/soap/WebService";

    private static final String GEEF_KINDVOORDELEN_SOAP_WEB_SERVICE = "GeefKindVoordelenDienst-02.00/soap/WebService";

    private static final String RAADPLEEG_LEERKREDIETENSALDO_SERVICE = "OnderwijsEnVorming/DHO2/PersoonDienst_v01_00";

    private static final String GEEF_VOLLEDIG_DOSSIER_HANDICAP_SERVICE = "GeefVolledigDossierHandicapDienst-03.00/soap/WebService";

    private static final String GEEF_AANSLAGBILJET_PERSONENBELASTING = "GeefAanslagbiljetPersonenbelastingDienst-02.00/soap/WebService";
    // END DEPRECATED

    private final MagdaMockConfig config;
    private final MagdaMockConnection mockConnection;
    private KeyStore keyStore;
    private Crypto crypto;

    private HashSet<String> interestingSecurityAttributes = new HashSet<>();

    @Autowired
    public MagdaMockController(MagdaMockConfig magdaMockConfig, RegistratieConfig config) {
        this.config = magdaMockConfig;
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

    // Alle onderstaande individuele endpoints voor MAGDA SOAP Services zijn deprecated en vervangen door magdaSoap0200WebService
    // START DEPRECATED

    @Deprecated(since = "20221028",forRemoval = true)
    @PostMapping(value = REGISTREER_INSCHRIJVING_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> registreerInschrijvingv20(@RequestBody(required = true) String request) throws MagdaSendFailed {
        return processMagdaMockRequest(request);
    }

    @Deprecated(since = "20221028",forRemoval = true)
    @PostMapping(value = REGISTREER_UITSCHRIJVING_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> registreerUitschrijvingv20(@RequestBody(required = true) String request) throws MagdaSendFailed {
        return processMagdaMockRequest(request);
    }

    @Deprecated(since = "20221028",forRemoval = true)
    @PostMapping(value = GEEF_AUDITLOGGING_SOAP_WEB_SERVICE_INCORRECT, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefAuditLoggingIncorrect(@RequestBody(required = true) String request) throws MagdaSendFailed {
        return processMagdaMockRequest(request);
    }

    @Deprecated(since = "20221028",forRemoval = true)
    @PostMapping(value = GEEF_AUDITLOGGING_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefAuditLogging(@RequestBody(required = true) String request) throws MagdaSendFailed {
        return processMagdaMockRequest(request);
    }

    @Deprecated(since = "20221028",forRemoval = true)
    @PostMapping(value = REGISTREER_INSCHRIJVING_21_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> registreerInschrijvingv21(@RequestBody(required = true) String request) throws MagdaSendFailed {
        return processMagdaMockRequest(request);
    }

    @Deprecated(since = "20221028",forRemoval = true)
    @PostMapping(value = GEEF_ONDERNEMING_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefOnderneming(@RequestBody(required = true) String request) throws MagdaSendFailed {
        return processMagdaMockRequest(request);
    }

    @Deprecated(since = "20221028",forRemoval = true)
    @PostMapping(value = GEEF_ONDERNEMINGVKBO_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefOndernemingvkbo(@RequestBody(required = true) String request) throws MagdaSendFailed {
        return processMagdaMockRequest(request);
    }

    @Deprecated(since = "20221028",forRemoval = true)
    @PostMapping(value = ZOEK_EIGENDOMSTOESTANDEN_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> zoekEigendomsToestanden(@RequestBody(required = true) String request) throws MagdaSendFailed {
        return processMagdaMockRequest(request);
    }

    @Deprecated(since = "20221028",forRemoval = true)
    @PostMapping(value = GEEF_EPC_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefEpc(@RequestBody(required = true) String request) throws MagdaSendFailed {
        return processMagdaMockRequest(request);
    }

    @Deprecated(since = "20221028",forRemoval = true)
    @PostMapping(value = GEEF_PERSOON_0202_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefPersoon0202(@RequestBody(required = true) String request) throws MagdaSendFailed {
        return processMagdaMockRequest(request);
    }

    @Deprecated(since = "20221028",forRemoval = true)
    @PostMapping(value = GEEF_HISTORIEK_PERSOON_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefHistoriekPersoon(@RequestBody(required = true) String request) throws MagdaSendFailed {
        return processMagdaMockRequest(request);
    }

    @Deprecated(since = "20221028",forRemoval = true)
    @PostMapping(value = GEEF_GEZINSSAMENSTELLING_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefGezinssamenstelling(@RequestBody(required = true) String request) throws MagdaSendFailed {
        return processMagdaMockRequest(request);
    }

    @Deprecated(since = "20221028",forRemoval = true)
    @PostMapping(value = GEEF_PASFOTO_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefPasfoto(@RequestBody(required = true) String request) throws MagdaSendFailed {
        return processMagdaMockRequest(request);
    }

    @Deprecated(since = "20221028",forRemoval = true)
    @PostMapping(value = GEEF_BEWIJS_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefBewijs(@RequestBody(required = true) String request) throws MagdaSendFailed {
        return processMagdaMockRequest(request);
    }

    @Deprecated(since = "20221028",forRemoval = true)
    @PostMapping(value = GEEF_DOSSIER_KBI_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefDossierKbi(@RequestBody(required = true) String request) throws MagdaSendFailed {
        return processMagdaMockRequest(request);
    }

    @Deprecated(since = "20221028",forRemoval = true)
    @PostMapping(value = GEEF_HISTORIEK_INSCHRIJVING_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefHistoriekInschrijving(@RequestBody(required = true) String request) throws MagdaSendFailed {
        return processMagdaMockRequest(request);
    }

    @Deprecated(since = "20221028",forRemoval = true)
    @PostMapping(value = GEEF_HISTORIEK_INSCHRIJVING_21_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefHistoriekInschrijvingv21(@RequestBody(required = true) String request) throws MagdaSendFailed {
        return processMagdaMockRequest(request);
    }

    @Deprecated(since = "20221028",forRemoval = true)
    @PostMapping(value = GEEF_LOOPBAAN_ONDERBREKINGEN_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefLoopbaanOnderbrekingen(@RequestBody(required = true) String request) throws MagdaSendFailed {
        return processMagdaMockRequest(request);
    }

    @Deprecated(since = "20221028",forRemoval = true)
    @PostMapping(value = GEEF_STATUS_RECHT_ONDERSTEUNINGEN_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefStatusRechtOndersteuningen(@RequestBody(required = true) String request) throws MagdaSendFailed {
        return processMagdaMockRequest(request);
    }

    @Deprecated(since = "20221028",forRemoval = true)
    @PostMapping(value = GEEF_FUNCTIES_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefFuncties(@RequestBody(required = true) String request) throws MagdaSendFailed {
        return processMagdaMockRequest(request);
    }

    @Deprecated(since = "20221028",forRemoval = true)
    @PostMapping(value = GEEF_DOSSIER_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefDossier(@RequestBody(required = true) String request) throws MagdaSendFailed {
        return processMagdaMockRequest(request);
    }

    @Deprecated(since = "20221028",forRemoval = true)
    @PostMapping(value = GEEF_ATTEST_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefAttest(@RequestBody(required = true) String request) throws MagdaSendFailed {
        return processMagdaMockRequest(request);
    }

    @Deprecated(since = "20221028",forRemoval = true)
    @PostMapping(value = GEEF_KINDVOORDELEN_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefKindVoordelen(@RequestBody(required = true) String request) throws MagdaSendFailed {
        return processMagdaMockRequest(request);
    }

    @Deprecated(since = "20221028",forRemoval = true)
    @PostMapping(value = RAADPLEEG_LEERKREDIETENSALDO_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> raadpleegLeerkredietensaldov10(@RequestBody String request) throws MagdaSendFailed {
        return processMagdaMockRequest(request);
    }

    @Deprecated(since = "20221028",forRemoval = true)
    @PostMapping(value = GEEF_VOLLEDIG_DOSSIER_HANDICAP_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefVolledigDossierHandicap(@RequestBody String request) throws MagdaSendFailed {
        return processMagdaMockRequest(request);
    }

    @Deprecated(since = "20221028",forRemoval = true)
    @PostMapping(value = GEEF_AANSLAGBILJET_PERSONENBELASTING, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefAanslagbiljetPersonenBelasting(@RequestBody String request) throws MagdaSendFailed {
        return processMagdaMockRequest(request);
    }
    // END DEPRECATED

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
            // TODO: maak en return MAGDA Uitzondering antwoord
            return ResponseEntity.notFound().build();
        }
    }


    private String loadPDF(UUID transactieId, String resourceName) throws AttestNotFoundException {
        try (InputStream stream = getClass().getResourceAsStream(resourceName)) {
            if (stream == null) {
                throw new AttestNotFoundException(resourceName);
            }
            long size = getClass().getResource(resourceName).getFile().length();

            byte[] encoded = Base64.getEncoder().encode(convertStreamToByteArray(stream, size));
            return new String(encoded);

        } catch (IOException e) {
            throw new AttestNotFoundException(resourceName, e);
        }
    }

    private byte[] convertStreamToByteArray(InputStream stream, long size) throws IOException {
        byte[] buffer = new byte[(int) size];
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        int bytesRead = 0;
        while ((bytesRead = stream.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        stream.close();
        os.flush();
        os.close();
        return os.toByteArray();
    }

    private String loadResourceString(String name) throws URISyntaxException, IOException {
        InputStream is = this.getClass().getResourceAsStream(name);
        StringWriter writer = new StringWriter();
        IOUtils.copy(is, writer, "UTF-8");
        return writer.toString();
    }

    private WSHandlerResult verify(Document doc) throws WSSecurityException {
        WSSecurityEngine secEngine = new WSSecurityEngine();
        doc.normalizeDocument();
        return secEngine.processSecurityHeader(doc, null, null, crypto);
    }
}
