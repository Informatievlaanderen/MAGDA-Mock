package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.connection.MagdaConnection;
import be.vlaanderen.vip.magda.client.security.DocumentSignatureVerifier;
import be.vlaanderen.vip.magda.client.security.DocumentSigner;
import be.vlaanderen.vip.magda.client.security.TwoWaySslProperties;
import be.vlaanderen.vip.magda.exception.MagdaSendFailed;
import be.vlaanderen.vip.magda.exception.TwoWaySslException;
import be.vlaanderen.vip.mock.magda.client.simulators.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;

import java.util.Optional;

@Slf4j
public class MagdaMockConnection implements MagdaConnection {

    private Document defaultResponse = null;
    
    private ISOAPSimulator simulator;

    // Subcategories for MAGDA services
    public static final String PERSOON = "Persoon";
    public static final String ONDERNEMING = "Onderneming";
    public static final String VASTGOED = "Vastgoed";

    public static final String KEY_INSZ = "//INSZ";

    public static final String KEY_ONDERNEMINGSNUMMER = "//Ondernemingsnummer";

    public static final String KEY_RRNR = "//rrnr";

    public MagdaMockConnection() {
        this.simulator = constructBuiltInSimulator(Optional.empty(), Optional.empty());
    }

    public MagdaMockConnection(TwoWaySslProperties requestVerifierConfig, TwoWaySslProperties responseSignerConfig) throws TwoWaySslException {
        Optional<DocumentSignatureVerifier> requestVerifier;
        Optional<DocumentSigner> responseSigner;

        if(requestVerifierConfig != null && StringUtils.isNotEmpty(requestVerifierConfig.getKeyStoreLocation())) {
            requestVerifier = Optional.of(DocumentSignatureVerifier.fromJksStore(requestVerifierConfig));
        } else {
            requestVerifier = Optional.empty();
        }

        if(responseSignerConfig != null && StringUtils.isNotEmpty(responseSignerConfig.getKeyStoreLocation())) {
            responseSigner = Optional.of(DocumentSigner.fromJksStore(responseSignerConfig));
        } else {
            responseSigner = Optional.empty();
        }

        this.simulator = constructBuiltInSimulator(requestVerifier, responseSigner);
    }

    private static ISOAPSimulator constructBuiltInSimulator(Optional<DocumentSignatureVerifier> requestVerifier, Optional<DocumentSigner> responseSigner) {
        var combinedSimulator = new CombinedSimulator();

        // PERSOON Standaard
        combinedSimulator.register("RegistreerInschrijving", "02.00.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));
        combinedSimulator.register("RegistreerInschrijving", "02.01.0000", new StaticResponseSimulator(PERSOON, "//Subject/Type", "//Subject/Sleutel"));
        combinedSimulator.register("RegistreerUitschrijving", "02.00.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));

        combinedSimulator.register("GeefBewijs", "02.00.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));
        combinedSimulator.register("GeefHistoriekInschrijving", "02.01.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));
        combinedSimulator.register("RaadpleegLeerkredietsaldo", "01.00.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));

        combinedSimulator.register("GeefLoopbaanOnderbrekingen", "02.00.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));
        combinedSimulator.register("GeefStatusRechtOndersteuningen", "02.00.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));
        combinedSimulator.register("GeefFuncties", "02.00.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));
        combinedSimulator.register("GeefDossiers", "02.00.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));
        combinedSimulator.register("GeefKindVoordelen", "02.00.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));
        combinedSimulator.register("GeefVolledigDossierHandicap", "03.00.0000", new StaticResponseSimulator(PERSOON, KEY_RRNR));

        combinedSimulator.register("GeefPersoon", "02.02.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));
        combinedSimulator.register("GeefHistoriekPersoon", "02.00.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));
        combinedSimulator.register("GeefHistoriekPersoon", "02.02.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));
        combinedSimulator.register("GeefGezinssamenstelling", "02.00.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));
        combinedSimulator.register("GeefGezinssamenstelling", "02.02.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));

        combinedSimulator.register("GeefDossierKBI", "01.00.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));

        combinedSimulator.register("GeefAanslagbiljetPersonenbelasting", "02.00.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));

        combinedSimulator.register("ZoekEigendomstoestanden", "02.00.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));

        combinedSimulator.register("ZoekPersoonOpAdres", "02.02.0000", new StaticResponseSimulator(PERSOON, "//Inhoud/Bron","//Criteria/Adres/PostCode", "//Criteria/Adres/Straatcode", "//Criteria/Adres/Huisnummer", "//Criteria/EnkelReferentiepersoon"));

        // PERSOON Custom
        combinedSimulator.register("GeefAttest", "02.00.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));
        combinedSimulator.register("GeefPasfoto", "02.00.0000", new RandomPasfotoSimulator(PERSOON, KEY_INSZ));

        // ONDERNEMING
        combinedSimulator.register("GeefOnderneming", "02.00.0000", new StaticResponseSimulator(ONDERNEMING, KEY_ONDERNEMINGSNUMMER));
        combinedSimulator.register("GeefOndernemingVKBO", "02.00.0000", new StaticResponseSimulator(ONDERNEMING, KEY_ONDERNEMINGSNUMMER));

        // GEBOUW
        combinedSimulator.register("GeefEpc", "02.00.0000", new StaticResponseSimulator(VASTGOED, "//Criteria/Attesten", "//Criteria/GebouweenheidId"));
        combinedSimulator.register("GeefEpc", "02.01.0000", new StaticResponseSimulator(VASTGOED, "//Criteria/Attesten", "//Criteria/GebouweenheidId", "//Criteria/Adres/Postcode", "//Criteria/Adres/Straat", "//Criteria/Adres/Huisnummer"));

        ISOAPSimulator simulator = combinedSimulator;

        if(requestVerifier.isPresent()) {
            simulator = new SignatureVerifyingSimulator(simulator, requestVerifier.get());
        }

        if(responseSigner.isPresent()) {
            simulator = new SigningSimulator(simulator, responseSigner.get());
        }

        return simulator;
    }

    @Override
    public Document sendDocument(Document xml) throws MagdaSendFailed {
        log.info("Answering using MAGDA Mock");

        if (defaultResponse != null) {
            Document answer = defaultResponse;
            defaultResponse = null;
            return answer;
        }

        return send(xml);
    }

    private Document send(Document xml) throws MagdaSendFailed {
        return simulator.send(MagdaDocument.fromDocument(xml)).getXml();
    }

    public void setDefaultResponse(Document xml) {
        defaultResponse = xml;
    }
}
