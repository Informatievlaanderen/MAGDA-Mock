package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.security.DocumentSignatureVerifier;
import be.vlaanderen.vip.magda.client.security.DocumentSigner;
import be.vlaanderen.vip.magda.client.security.TwoWaySslException;
import be.vlaanderen.vip.magda.client.security.TwoWaySslProperties;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder;

public class SOAPSimulatorBuilder {
    // Subcategories for MAGDA services
    public static final String PERSOON = "Persoon";
    public static final String ONDERNEMING = "Onderneming";
    public static final String VASTGOED = "Vastgoed";

    public static final String KEY_INSZ = "//INSZ";
    public static final String KEY_ONDERNEMINGSNUMMER = "//Ondernemingsnummer";
    public static final String KEY_RRNR = "//rrnr";

    private static final String VERSION_01_00 = "01.00.0000";
    private static final String VERSION_02_00 = "02.00.0000";
    private static final String VERSION_02_01 = "02.01.0000";
    private static final String VERSION_02_02 = "02.02.0000";
    private static final String VERSION_03_00 = "03.00.0000";

    private final ResourceFinder finder;
    private final CombinedSimulator simulator;
    private SOAPSimulator result;
    
    SOAPSimulatorBuilder(ResourceFinder finder) {
        this.finder = finder;
        this.simulator = new CombinedSimulator();
        this.result = simulator;
    }
    
    public SOAPSimulatorBuilder magdaMockSimulator() {
        // PERSOON Standaard
        simulator.register("RegistreerInschrijving", VERSION_02_00, new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("RegistreerInschrijving", VERSION_02_01, new StaticResponseSimulator(finder, PERSOON, "//Subject/Type", "//Subject/Sleutel"));
        simulator.register("RegistreerUitschrijving", VERSION_02_00, new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));

        simulator.register("GeefBewijs", VERSION_02_00, new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("GeefHistoriekInschrijving", VERSION_02_01, new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("RaadpleegLeerkredietsaldo", VERSION_01_00, new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));

        simulator.register("GeefLoopbaanOnderbrekingen", VERSION_02_00, new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("GeefStatusRechtOndersteuningen", VERSION_02_00, new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("GeefFuncties", VERSION_02_00, new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("GeefDossiers", VERSION_02_00, new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("GeefKindVoordelen", VERSION_02_00, new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("GeefVolledigDossierHandicap", VERSION_03_00, new StaticResponseSimulator(finder, PERSOON, KEY_RRNR));

        simulator.register("GeefPersoon", VERSION_02_02, new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("GeefHistoriekPersoon", VERSION_02_00, new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("GeefHistoriekPersoon", VERSION_02_02, new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("GeefGezinssamenstelling", VERSION_02_00, new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("GeefGezinssamenstelling", VERSION_02_02, new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));

        simulator.register("GeefDossierKBI", VERSION_01_00, new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));

        simulator.register("GeefAanslagbiljetPersonenbelasting", VERSION_02_00, new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));

        simulator.register("ZoekEigendomstoestanden", VERSION_02_00, new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));

        simulator.register("ZoekPersoonOpAdres", VERSION_02_02, new StaticResponseSimulator(finder, PERSOON, "//Inhoud/Bron","//Criteria/Adres/PostCode", "//Criteria/Adres/Straatcode", "//Criteria/Adres/Huisnummer", "//Criteria/EnkelReferentiepersoon"));

        // PERSOON Custom
        simulator.register("GeefAttest", VERSION_02_00, new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("GeefPasfoto", VERSION_02_00, new RandomPasfotoSimulator(finder, PERSOON, KEY_INSZ));

        // ONDERNEMING
        simulator.register("GeefOnderneming", VERSION_02_00, new StaticResponseSimulator(finder, ONDERNEMING, KEY_ONDERNEMINGSNUMMER));
        simulator.register("GeefOndernemingVKBO", VERSION_02_00, new StaticResponseSimulator(finder, ONDERNEMING, KEY_ONDERNEMINGSNUMMER));

        // GEBOUW
        simulator.register("GeefEpc", VERSION_02_00, new StaticResponseSimulator(finder, VASTGOED, "//Criteria/Attesten", "//Criteria/GebouweenheidId"));
        simulator.register("GeefEpc", VERSION_02_01, new StaticResponseSimulator(finder, VASTGOED, "//Criteria/Attesten", "//Criteria/GebouweenheidId", "//Criteria/Adres/Postcode", "//Criteria/Adres/Straat", "//Criteria/Adres/Huisnummer"));

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
