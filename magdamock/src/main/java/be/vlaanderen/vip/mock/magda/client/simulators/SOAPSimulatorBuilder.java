package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.security.DocumentSignatureVerifier;
import be.vlaanderen.vip.magda.client.security.DocumentSigner;
import be.vlaanderen.vip.magda.client.security.TwoWaySslProperties;
import be.vlaanderen.vip.magda.exception.TwoWaySslException;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder;

public class SOAPSimulatorBuilder {
    // Subcategories for MAGDA services
    public static final String PERSOON = "Persoon";
    public static final String ONDERNEMING = "Onderneming";
    public static final String VASTGOED = "Vastgoed";

    public static final String KEY_INSZ = "//INSZ";
    public static final String KEY_ONDERNEMINGSNUMMER = "//Ondernemingsnummer";
    public static final String KEY_RRNR = "//rrnr";
    
    private ResourceFinder finder;
    private CombinedSimulator simulator;
    private SOAPSimulator result;
    
    SOAPSimulatorBuilder(ResourceFinder finder) {
        this.finder = finder;
        this.simulator = new CombinedSimulator();
        this.result = simulator;
    }
    
    public SOAPSimulatorBuilder magdaMockSimulator() {
        // PERSOON Standaard
        simulator.register("RegistreerInschrijving", "02.00.0000", new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("RegistreerInschrijving", "02.01.0000", new StaticResponseSimulator(finder, PERSOON, "//Subject/Type", "//Subject/Sleutel"));
        simulator.register("RegistreerUitschrijving", "02.00.0000", new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));

        simulator.register("GeefBewijs", "02.00.0000", new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("GeefHistoriekInschrijving", "02.01.0000", new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("RaadpleegLeerkredietsaldo", "01.00.0000", new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));

        simulator.register("GeefLoopbaanOnderbrekingen", "02.00.0000", new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("GeefStatusRechtOndersteuningen", "02.00.0000", new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("GeefFuncties", "02.00.0000", new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("GeefDossiers", "02.00.0000", new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("GeefKindVoordelen", "02.00.0000", new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("GeefVolledigDossierHandicap", "03.00.0000", new StaticResponseSimulator(finder, PERSOON, KEY_RRNR));

        simulator.register("GeefPersoon", "02.02.0000", new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("GeefHistoriekPersoon", "02.00.0000", new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("GeefHistoriekPersoon", "02.02.0000", new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("GeefGezinssamenstelling", "02.00.0000", new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("GeefGezinssamenstelling", "02.02.0000", new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));

        simulator.register("GeefDossierKBI", "01.00.0000", new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));

        simulator.register("GeefAanslagbiljetPersonenbelasting", "02.00.0000", new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));

        simulator.register("ZoekEigendomstoestanden", "02.00.0000", new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));

        simulator.register("ZoekPersoonOpAdres", "02.02.0000", new StaticResponseSimulator(finder, PERSOON, "//Inhoud/Bron","//Criteria/Adres/PostCode", "//Criteria/Adres/Straatcode", "//Criteria/Adres/Huisnummer", "//Criteria/EnkelReferentiepersoon"));

        // PERSOON Custom
        simulator.register("GeefAttest", "02.00.0000", new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("GeefPasfoto", "02.00.0000", new RandomPasfotoSimulator(finder, PERSOON, KEY_INSZ));

        // ONDERNEMING
        simulator.register("GeefOnderneming", "02.00.0000", new StaticResponseSimulator(finder, ONDERNEMING, KEY_ONDERNEMINGSNUMMER));
        simulator.register("GeefOndernemingVKBO", "02.00.0000", new StaticResponseSimulator(finder, ONDERNEMING, KEY_ONDERNEMINGSNUMMER));

        // GEBOUW
        simulator.register("GeefEpc", "02.00.0000", new StaticResponseSimulator(finder, VASTGOED, "//Criteria/Attesten", "//Criteria/GebouweenheidId"));
        simulator.register("GeefEpc", "02.01.0000", new StaticResponseSimulator(finder, VASTGOED, "//Criteria/Attesten", "//Criteria/GebouweenheidId", "//Criteria/Adres/Postcode", "//Criteria/Adres/Straat", "//Criteria/Adres/Huisnummer"));

        return this;
    }
    
    public SOAPSimulatorBuilder requestVerifier(DocumentSignatureVerifier verifier) {
        result = new SignatureVerifyingSimulator(finder, result, verifier);
        return this;
    }
    
    public SOAPSimulatorBuilder requestVerifierProperties(TwoWaySslProperties properties) throws TwoWaySslException {
        return requestVerifier(DocumentSignatureVerifier.fromJksStore(properties));
    }
    
    public SOAPSimulatorBuilder responseSigner(DocumentSigner signer) {
        result = new SigningSimulator(finder, result, signer);
        return this;
    }

    public SOAPSimulatorBuilder responseSignerProperties(TwoWaySslProperties properties) throws TwoWaySslException {
        return responseSigner(DocumentSigner.fromJksStore(properties));
    }
    
    public SOAPSimulator build() {
        return result;
    }
    
    public static SOAPSimulatorBuilder builder(ResourceFinder finder) {
        return new SOAPSimulatorBuilder(finder);
    }
}
