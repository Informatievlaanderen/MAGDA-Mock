package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.security.DocumentSignatureVerifier;
import be.vlaanderen.vip.magda.client.security.DocumentSigner;
import be.vlaanderen.vip.magda.client.security.TwoWaySslException;
import be.vlaanderen.vip.magda.client.security.TwoWaySslProperties;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder;

import java.time.Duration;

public class SOAPSimulatorBuilder {
    // Subcategories for MAGDA services
    public static final String PERSOON = "Persoon";
    public static final String ONDERNEMING = "Onderneming";
    public static final String VASTGOED = "Vastgoed";

    public static final String KEY_INSZ = "//INSZ";
    public static final String KEY_ONDERNEMINGSNUMMER = "//Ondernemingsnummer";
    public static final String KEY_BOEKJAAR = "//Boekjaar";
    public static final String KEY_RRNR = "//rrnr";
    public static final String KEY_SSIN = "//ssin";

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

    /**
     * Builds a magdaMockSimulator, which will return static responses based on the request.
     *
     * @return The builder
     */
    public SOAPSimulatorBuilder magdaMockSimulator() {
        return magdaMockSimulator(false);
    }

    /**
     * Builds a magdaMockSimulator, which will return mocked responses based on the request.
     * Through the copyPropertiesFromRequest parameter, the simulator can be configured to copy properties
     * from the request to the response. Currently, this behaviour is only implemented for the
     * GeefAanslagbiljetPersonenbelasting service.
     *
     * @param copyPropertiesFromRequest Whether to copy certain properties from the request to the response
     * @return The builder
     */
    public SOAPSimulatorBuilder magdaMockSimulator(boolean copyPropertiesFromRequest) {
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
        simulator.register("GeefHistoriekGezinssamenstelling", VERSION_02_02, new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("GeefBetalingenHandicap", VERSION_03_00, new StaticResponseSimulator(finder, PERSOON, KEY_SSIN));
        simulator.register("GeefDossierHandicap", VERSION_03_00, new StaticResponseSimulator(finder, PERSOON, KEY_SSIN));
        simulator.register("GeefLeefloonbedragen", VERSION_02_00, new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));

        simulator.register("GeefDossierKBI", VERSION_01_00, new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));

        simulator.register("ZoekEigendomstoestanden", VERSION_02_00, new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));

        simulator.register("ZoekPersoonOpAdres", VERSION_02_02, new StaticResponseSimulator(finder, PERSOON, "//Inhoud/Bron","//Criteria/Adres/PostCode", "//Criteria/Adres/Straatcode", "//Criteria/Adres/Huisnummer", "//Criteria/EnkelReferentiepersoon"));
        
        //simulator.register("GeefSociaalStatuut", VERSION_03_00, new GeefSociaalStatuutSimulator(finder, PERSOON, "//SociaalStatuut/Naam", KEY_INSZ));
        simulator.register("GeefSociaalStatuut", VERSION_03_00, new GeefSociaalStatuutSimulator(finder, PERSOON, KEY_INSZ));

        simulator.register("GeefLoopbaanARZA", VERSION_02_01, new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("GeefWerkrelaties", VERSION_02_00, new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));

        // PERSOON Custom
        simulator.register("GeefAttest", VERSION_02_00, new StaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("GeefPasfoto", VERSION_02_00, new RandomPasfotoSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("GeefAanslagbiljetPersonenbelasting", VERSION_02_00, new GeefAanslagbiljetPersonenbelastingSimulator(finder, PERSOON, copyPropertiesFromRequest, KEY_INSZ));

        // ONDERNEMING
        simulator.register("GeefOnderneming", VERSION_02_00, new StaticResponseSimulator(finder, ONDERNEMING, KEY_ONDERNEMINGSNUMMER));
        simulator.register("GeefOndernemingVKBO", VERSION_02_00, new StaticResponseSimulator(finder, ONDERNEMING, KEY_ONDERNEMINGSNUMMER));
        simulator.register("GeefJaarrekeningen", VERSION_02_00, new StaticResponseSimulator(finder, ONDERNEMING, KEY_ONDERNEMINGSNUMMER, KEY_BOEKJAAR));

        // GEBOUW
        simulator.register("GeefEpc", VERSION_02_00, new StaticResponseSimulator(finder, VASTGOED, "//Criteria/Attesten", "//Criteria/GebouweenheidId"));
        simulator.register("GeefEpc", VERSION_02_01, new StaticResponseSimulator(finder, VASTGOED,  "//Criteria/Adres/Postcode", "//Criteria/Adres/Straat", "//Criteria/Adres/Huisnummer"));

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

    public SOAPSimulatorBuilder responseDelay(Duration delay) {
        result = new DelaySimulator(result, delay);
        return this;
    }
    
    public SOAPSimulator build() {
        return result;
    }
    
    public static SOAPSimulatorBuilder builder(ResourceFinder finder) {
        return new SOAPSimulatorBuilder(finder);
    }
}
