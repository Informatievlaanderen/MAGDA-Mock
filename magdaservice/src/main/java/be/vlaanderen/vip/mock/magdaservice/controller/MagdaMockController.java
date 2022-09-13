package be.vlaanderen.vip.mock.magdaservice.controller;


import be.vlaanderen.vip.mock.magda.client.MagdaMockConnection;
import be.vlaanderen.vip.mock.magdaservice.config.MagdaMockConfig;
import be.vlaanderen.vip.mock.magdaservice.config.RegistratieConfig;
import be.vlaanderen.vip.mock.magdaservice.exception.AttestNotFoundException;
import be.vlaanderen.vip.mock.magdaservice.magda.MagdaDocument;
import be.vlaanderen.vip.mock.magdaservice.magda.MagdaRequest;
import be.vlaanderen.vip.mock.magdaservice.magda.MagdaService;
import be.vlaanderen.vip.mock.magdaservice.util.INSZ;
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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import static java.security.KeyStore.getInstance;
import static org.springframework.http.MediaType.APPLICATION_XML;
import static org.springframework.util.MimeTypeUtils.APPLICATION_XML_VALUE;
import static org.springframework.util.MimeTypeUtils.TEXT_XML_VALUE;

@RestController
@CrossOrigin
@Slf4j
public class MagdaMockController {


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

    private static final String GEEF_AANSLAGBILJET_PERSONENBELASTING = "GeefAanslagBiljetPersonenbelastingDienst-02.00/soap/WebService";

    private static final String KEY_IS_INSZ = "//INSZ";
    private static final String MAGDA_MOCK_CONTEXT = "MagdaMock";

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

    private static String resourcePath(MagdaService service, String insz) {
        return "/magda_simulator/" + service.getUrl() + "/" + insz + ".xml";
    }

    private static String resourcePath(MagdaService service, List<String> insz) {

        StringBuilder s = new StringBuilder("/magda_simulator/" + service.getUrl());
        for (String str : insz) {
            s.append("/").append(str);
        }
        return s.append(".xml").toString();
    }

    @PostMapping(value = REGISTREER_INSCHRIJVING_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> registreerInschrijvingv20(@RequestBody(required = true) String request) throws IOException, ParserConfigurationException, SAXException {
        return processMagdaMockRequest(request, KEY_IS_INSZ);
    }

    @PostMapping(value = REGISTREER_UITSCHRIJVING_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> registreerUitschrijvingv20(@RequestBody(required = true) String request) throws IOException, ParserConfigurationException, SAXException {
        return processMagdaMockRequest(request, KEY_IS_INSZ);
    }

    @PostMapping(value = GEEF_AUDITLOGGING_SOAP_WEB_SERVICE_INCORRECT, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefAuditLoggingIncorrect(@RequestBody(required = true) String request) throws IOException, ParserConfigurationException, SAXException {
        return processMagdaMockRequest(request, "//Criteria/Sleutel/Waarde");
    }

    @PostMapping(value = GEEF_AUDITLOGGING_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefAuditLogging(@RequestBody(required = true) String request) throws IOException, ParserConfigurationException, SAXException {
        return processMagdaMockRequest(request, "//Criteria/Sleutel/Waarde");
    }

    @PostMapping(value = REGISTREER_INSCHRIJVING_21_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> registreerInschrijvingv21(@RequestBody(required = true) String request) throws IOException, ParserConfigurationException, SAXException {
        return processMagdaMockRequest(request, "//Subject/Sleutel");
    }

    @PostMapping(value = GEEF_ONDERNEMING_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefOnderneming(@RequestBody(required = true) String request) throws IOException, ParserConfigurationException, SAXException {
        return processMagdaMockRequest(request, "//Ondernemingsnummer");
    }

    @PostMapping(value = GEEF_ONDERNEMINGVKBO_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefOndernemingvkbo(@RequestBody(required = true) String request) throws IOException, ParserConfigurationException, SAXException {
        return processMagdaMockRequest(request, "//Ondernemingsnummer");
    }

    @PostMapping(value = ZOEK_EIGENDOMSTOESTANDEN_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> zoekEigendomsToestanden(@RequestBody(required = true) String request) throws IOException, ParserConfigurationException, SAXException {
        return processMagdaMockRequest(request, KEY_IS_INSZ);
    }

    @PostMapping(value = GEEF_EPC_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefEpc(@RequestBody(required = true) String request) throws IOException, ParserConfigurationException, SAXException {
        return processMagdaMockRequest(request, "//Criteria/Attesten", "//Criteria/GebouweenheidId");
    }

    @PostMapping(value = GEEF_PERSOON_0202_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefPersoon0202(@RequestBody(required = true) String request) throws IOException, ParserConfigurationException, SAXException {
        return processMagdaMockRequest(request, KEY_IS_INSZ);
    }

    @PostMapping(value = GEEF_HISTORIEK_PERSOON_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefHistoriekPersoon(@RequestBody(required = true) String request) throws IOException, ParserConfigurationException, SAXException {
        return processMagdaMockRequest(request, KEY_IS_INSZ);
    }

    @PostMapping(value = GEEF_GEZINSSAMENSTELLING_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefGezinssamenstelling(@RequestBody(required = true) String request) throws IOException, ParserConfigurationException, SAXException {
        return processMagdaMockRequest(request, KEY_IS_INSZ);
    }

    @PostMapping(value = GEEF_PASFOTO_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefPasfoto(@RequestBody(required = true) String request) throws IOException, ParserConfigurationException, SAXException {
        return processMagdaMockPasfotoRequest(request, KEY_IS_INSZ);
    }

    @PostMapping(value = GEEF_BEWIJS_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefBewijs(@RequestBody(required = true) String request) throws IOException, ParserConfigurationException, SAXException {
        return processMagdaMockRequest(request, KEY_IS_INSZ);
    }

    @PostMapping(value = GEEF_DOSSIER_KBI_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefDossierKbi(@RequestBody(required = true) String request) throws IOException, ParserConfigurationException, SAXException {
        return processMagdaMockRequest(request, KEY_IS_INSZ);
    }

    @PostMapping(value = GEEF_HISTORIEK_INSCHRIJVING_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefHistoriekInschrijving(@RequestBody(required = true) String request) throws IOException, ParserConfigurationException, SAXException {
        return processMagdaMockRequest(request, KEY_IS_INSZ);
    }

    @PostMapping(value = GEEF_HISTORIEK_INSCHRIJVING_21_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefHistoriekInschrijvingv21(@RequestBody(required = true) String request) throws IOException, ParserConfigurationException, SAXException {
        return processMagdaMockRequest(request, KEY_IS_INSZ);
    }

    @PostMapping(value = GEEF_LOOPBAAN_ONDERBREKINGEN_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefLoopbaanOnderbrekingen(@RequestBody(required = true) String request) throws IOException, ParserConfigurationException, SAXException {
        return processMagdaMockRequest(request, KEY_IS_INSZ);
    }

    @PostMapping(value = GEEF_STATUS_RECHT_ONDERSTEUNINGEN_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefStatusRechtOndersteuningen(@RequestBody(required = true) String request) throws IOException, ParserConfigurationException, SAXException {
        return processMagdaMockRequest(request, KEY_IS_INSZ);
    }

    @PostMapping(value = GEEF_FUNCTIES_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefFuncties(@RequestBody(required = true) String request) throws IOException, ParserConfigurationException, SAXException {
        return processMagdaMockRequest(request, KEY_IS_INSZ);
    }

    @PostMapping(value = GEEF_DOSSIER_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefDossier(@RequestBody(required = true) String request) throws IOException, ParserConfigurationException, SAXException {
        return processMagdaMockRequest(request, KEY_IS_INSZ);
    }

    @PostMapping(value = GEEF_ATTEST_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefAttest(@RequestBody(required = true) String request) throws IOException, ParserConfigurationException, SAXException {
        return processMagdaMockAttestRequest(request, KEY_IS_INSZ);
    }

    @PostMapping(value = GEEF_KINDVOORDELEN_SOAP_WEB_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefKindVoordelen(@RequestBody(required = true) String request) throws IOException, ParserConfigurationException, SAXException {
        return processMagdaMockRequest(request, KEY_IS_INSZ);
    }

    @PostMapping(value = RAADPLEEG_LEERKREDIETENSALDO_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> raadpleegLeerkredietensaldov10(@RequestBody String request) throws IOException, ParserConfigurationException, SAXException {
        return processMagdaMockRequest(request, KEY_IS_INSZ);
    }

    @PostMapping(value = GEEF_VOLLEDIG_DOSSIER_HANDICAP_SERVICE, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefVolledigDossierHandicap(@RequestBody String request) throws IOException, ParserConfigurationException, SAXException {
        return processMagdaMockRequest(request, "//rrnr");
    }

    @PostMapping(value = GEEF_AANSLAGBILJET_PERSONENBELASTING, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> geefAanslagbiljetPersonenBelasting(@RequestBody String request) throws IOException, ParserConfigurationException, SAXException {
        return processMagdaMockRequest(request, KEY_IS_INSZ);
    }

    private ResponseEntity<String> processMagdaMockRequest(String request, String... expression) throws ParserConfigurationException, SAXException, IOException {
        MagdaDocument aanvraag = parseRequest(request);

        return handleMagdaRequest(aanvraag, expression);
    }

    private ResponseEntity<String> processMagdaMockPasfotoRequest(String request, String expression) throws IOException, SAXException, ParserConfigurationException {
        MagdaDocument aanvraag = parseRequest(request);

        return handleMagdaPasfotoRequest(aanvraag, expression);
    }

    private ResponseEntity<String> processMagdaMockAttestRequest(String request, String expression) throws IOException, SAXException, ParserConfigurationException {
        MagdaDocument aanvraag = parseRequest(request);

        return handleMagdaAttestRequest(aanvraag, expression);
    }

    private MagdaDocument parseRequest(String request) throws ParserConfigurationException, SAXException, IOException {
        //TODO: handle request parsing errors and return Magda Uitzondering error
        //TODO: verify signature
        return MagdaDocument.fromString(request);
    }

    private ResponseEntity<String> handleMagdaRequest(MagdaDocument aanvraag, String... expression) throws IOException, ParserConfigurationException, SAXException {
        long start = System.nanoTime();

        MagdaRequest aanvraagParameters = new MagdaRequest(aanvraag, expression);

        log.info(String.format(">>> Vraag voor mock dienst %s v%s voor %s", aanvraagParameters.getServiceNaam(), aanvraagParameters.getServiceVersie(), aanvraagParameters.getKeys()));
        log.info(aanvraag.toString());

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

        // TODO: verify that identificatie and hoedanigheid are those of WWOOOM Mock, else Uitzondering "not authorized"


        // TODO: reserve a few INSZ numbers for server errors, e.g. no response or veeeeeeeeery slow response
        MagdaService magdaService = makeMagdaService(aanvraagParameters);

        String responsePath = resourcePath(magdaService, aanvraagParameters.getKeys());
        InputStream inputStream = getClass().getResourceAsStream(responsePath);
        if (inputStream == null) {
            responsePath = resourcePath(magdaService, "success");
            inputStream = getClass().getResourceAsStream(responsePath);
        }
        if (inputStream == null) {
            log.info(String.format("Magda resource %s niet gevonden", responsePath));
            responsePath = resourcePath(magdaService, "notfound");
            inputStream = getClass().getResourceAsStream(responsePath);
            if (inputStream == null) {
                log.info(String.format("Magda resource %s niet gevonden", responsePath));
            }
        }

        final String reponseUrl = responsePath.replace("/magda_simulator", "/api/v1/testdata/xml");

        final ResponseEntity<String> response = parseInputstream(aanvraagParameters, magdaService, inputStream, new HashMap<String, String>());

        Duration duration = Duration.of(System.nanoTime() - start, ChronoUnit.NANOS);
        log.info(String.format("<<< Vraag voor mock dienst %s voor %s in %d ms => %s", aanvraagParameters.getServiceNaam(), aanvraagParameters.getKeys(), duration.toMillis(), reponseUrl));

        return response;
    }

    private ResponseEntity<String> parseInputstream(MagdaRequest aanvraagParameters, MagdaService magdaService, InputStream inputStream, HashMap<String, String> mappings) throws IOException, ParserConfigurationException, SAXException {
        if (inputStream != null) {
            String response = readySoapResponse(inputStream, aanvraagParameters, mappings);

            // Voeg een key toe in config onder magdatamock.responsetimes met als naam de naam van de Magda dienst en als value
            //  de gewenste respons tijd in milliseconden
            simuleerMagdaResponsTijdVoor(magdaService.getServiceNaam());
            return ResponseEntity.ok().contentType(APPLICATION_XML).body(response);
        } else {
            log.error(
                    String.format("Could not find test XML for: [service: %s] [serviceVersion: %s] [%s: %s]",
                            magdaService.getServiceNaam(),
                            magdaService.getServiceVersie(),
                            aanvraagParameters.getExpressions(),
                            aanvraagParameters.getKeys()));

            // TODO: maak en return MAGDA Uitzondering antwoord
            return ResponseEntity.notFound().build();
        }
    }

    private ResponseEntity<String> handleMagdaPasfotoRequest(MagdaDocument aanvraag, String expression) throws IOException, ParserConfigurationException, SAXException {
        long start = System.nanoTime();

        MagdaRequest aanvraagParameters = new MagdaRequest(aanvraag, expression);

        final String rijksRegisterNummer = aanvraagParameters.getKeys().get(0);
        log.info(String.format(">>> Vraag voor mock dienst %s voor %s", aanvraagParameters.getServiceNaam(), rijksRegisterNummer));

        // TODO: verify that identificatie and hoedanigheid are those of WWOOOM Mock, else Uitzondering "not authorized"


        // TODO: reserve a few INSZ numbers for server errors, e.g. no response or veeeeeeeeery slow response


        // Burgers met RRN dat niet uit RR komt, maar uit KSZ hebben geen pasfoto
        MagdaService magdaService = makeMagdaService(aanvraagParameters);
        InputStream inputStream = getClass().getResourceAsStream(pasFotoresourcePath(magdaService, rijksRegisterNummer));


        HashMap<String, String> mappings = new HashMap<>();
        mappings.put("//Inhoud/Pasfoto/INSZ", rijksRegisterNummer);
        final ResponseEntity<String> response = parseInputstream(aanvraagParameters, magdaService, inputStream, mappings);

        Duration duration = Duration.of(System.nanoTime() - start, ChronoUnit.NANOS);
        log.info(String.format("<<< Vraag voor mock dienst %s voor %s in %d ms", aanvraagParameters.getServiceNaam(), rijksRegisterNummer, duration.toMillis()));

        return response;

    }

    private ResponseEntity<String> handleMagdaAttestRequest(MagdaDocument aanvraag, String expression) throws IOException, ParserConfigurationException, SAXException {

        long start = System.nanoTime();

        MagdaRequest aanvraagParameters = new MagdaRequest(aanvraag, expression);

        if (crypto != null && keyStore != null) {
            try {
                final WSHandlerResult verify = verify(aanvraag.getXml());
                System.out.println(verify);
            } catch (WSSecurityException e) {
                log.error("Signature verification gefaald", e);
            }
        }

        String attestNaam = aanvraag.getValue("//Vraag/Inhoud/Criteria/Attest/Naam");
        attestNaam = attestNaam.replace("/", "");
        String attestTaal = aanvraag.getValue("//Vraag/Inhoud/Criteria/Attest/Taal");

        String insz = aanvraagParameters.getKeys().get(0);
        log.info(String.format(">>> Vraag voor mock dienst %s voor %s", aanvraagParameters.getServiceNaam(), insz));

        MagdaService magdaService = makeMagdaService(aanvraagParameters);
        String responsePath = resourcePath(magdaService, insz);
        InputStream inputStream = getClass().getResourceAsStream(responsePath);
        final ResponseEntity<String> response = parseInputstream(aanvraagParameters, magdaService, inputStream, new HashMap<String, String>());

        Duration duration = Duration.of(System.nanoTime() - start, ChronoUnit.NANOS);
        log.info(String.format("<<< Vraag voor mock dienst %s voor %s in %d ms", aanvraagParameters.getServiceNaam(), insz, duration.toMillis()));

        return response;

    }

    private MagdaService makeMagdaService(MagdaRequest aanvraagParameters) {
        return new MagdaService(aanvraagParameters.getServiceNaam(), aanvraagParameters.getServiceVersie());
    }


    private void simuleerMagdaResponsTijdVoor(String serviceNaam) {
        Integer timeout = null;
        final HashMap<String, Integer> responsetimes = config.getResponsetimes();
        if (responsetimes != null) {
            timeout = responsetimes.get(serviceNaam);
        }
        if (timeout == null) {
            timeout = config.getDefaultresponsetime();
        }
        int timeoutLength = timeout.intValue();
        if (timeoutLength > 0) {
            try {
                Thread.sleep(timeoutLength);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private String readySoapResponse(InputStream inputStream,
                                     MagdaRequest context, HashMap<String, String> mappings) throws IOException, ParserConfigurationException, SAXException {
        MagdaDocument xml = MagdaDocument.fromStream(inputStream);

        xml.setValue("//Ontvanger/Referte", context.getReferte());
        xml.setValue("//Ontvanger/Identificatie", context.getIdentificatie());
        xml.setValue("//Ontvanger/Hoedanigheid", context.getHoedanigheid());
        xml.setValue("//Ontvanger/Gebruiker", context.getGebruiker());

        for (Map.Entry<String, String> map : mappings.entrySet()) {
            xml.setValue(map.getKey(), map.getValue());
        }

        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = dayFormat.format(new Date());
        xml.setValue("//Context/Bericht/Tijdstip/Datum", today);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.S");
        String time = timeFormat.format(new Date());
        xml.setValue("//Context/Bericht/Tijdstip/Tijd", time);

        // Identificeert antwoord als komend van Burgerloket Magda Mock
        xml.setValue("//Afzender/Referte", UUID.randomUUID().toString());
        xml.setValue("//Afzender/Identificatie", "kb.vlaanderen.be/aiv/burgerloket-wwoom-mock-server");
        xml.setValue("//Afzender/Naam", "Burgerloket Magda Mock");

        xml.setValue("//Antwoorden/Antwoord/Referte", context.getVraagReferte());

        String xmlString = xml.toString();
        return "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" >\n" +
                "  <soapenv:Header/>\n" +
                "  <soapenv:Body>\n" +
                xmlString +
                "  </soapenv:Body>\n" +
                "</soapenv:Envelope>"
                ;
    }

    private String pasFotoresourcePath(MagdaService service, String insz) {

        String path = "/magda_simulator/" + service.getUrl() + "/{replace}/";

        String geboorteDatum = insz.substring(0, 6);
        int dateOfBirth = Integer.parseInt(geboorteDatum);

        if (INSZ.isMannelijk(insz)) {
            path = path.replace("{replace}", "mannen") + (dateOfBirth % 6) + ".xml";
        } else {
            path = path.replace("{replace}", "vrouwen") + (dateOfBirth % 4) + ".xml";

        }

        return path;
    }


    private String attestResourcePath(MagdaService service, String naam) {
        String path = "/magda_simulator/" + service.getUrl() + "/" + naam;

        return path;
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
