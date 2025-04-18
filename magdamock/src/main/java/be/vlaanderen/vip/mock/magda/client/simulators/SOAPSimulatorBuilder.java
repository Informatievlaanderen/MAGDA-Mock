package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.security.DocumentSignatureVerifier;
import be.vlaanderen.vip.magda.client.security.DocumentSigner;
import be.vlaanderen.vip.magda.client.security.TwoWaySslException;
import be.vlaanderen.vip.magda.client.security.TwoWaySslProperties;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder;

import java.time.Duration;

public class SOAPSimulatorBuilder {
    // Subcategories for MAGDA services
    public static final String DOSSIER = "Dossier";
    public static final String GEZIN = "Gezin";
    public static final String INKOMEN = "Inkomen";
    public static final String KADASTER = "Kadaster";
    public static final String LED = "LED";
    public static final String ONDERNEMING = "Onderneming";
    public static final String ONDERWIJS = "Onderwijs";
    public static final String PERSOON = "Persoon";
    public static final String REPERTORIUM = "Repertorium";
    public static final String SOCECON = "SocEcon";
    public static final String SOCZEK = "SocZek";
    public static final String VASTGOED = "Vastgoed";
    public static final String WERK = "Werk";

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
        // Dossier
        simulator.register("GeefDossiers", VERSION_02_00, new PathBasedStaticResponseSimulator(finder, DOSSIER, KEY_INSZ));

        // Gezin
        simulator.register("GeefKindVoordelen", VERSION_02_00, new PathBasedStaticResponseSimulator(finder, GEZIN, KEY_INSZ));

        // Inkomen
        simulator.register("GeefAanslagbiljetPersonenbelasting", VERSION_02_00, new GeefAanslagbiljetPersonenbelastingSimulator(finder, INKOMEN, copyPropertiesFromRequest, KEY_INSZ));

        // Kadaster
        simulator.register("ZoekEigendomstoestanden", VERSION_02_00, new PathBasedStaticResponseSimulator(finder, KADASTER, KEY_INSZ));

        // LED
        simulator.register("GeefBewijs", VERSION_02_00, new PathBasedStaticResponseSimulator(finder, LED, KEY_INSZ));
        simulator.register("RegistreerBewijs", VERSION_02_00, new PathBasedStaticResponseSimulator(finder, LED, KEY_INSZ));

        // Onderneming
        simulator.register("GeefFuncties", VERSION_02_00, new PathBasedStaticResponseSimulator(finder, ONDERNEMING, KEY_INSZ));
        simulator.register("GeefJaarrekeningen", VERSION_02_00, new PathBasedStaticResponseSimulator(finder, ONDERNEMING, KEY_ONDERNEMINGSNUMMER, KEY_BOEKJAAR));
        simulator.register("GeefOnderneming", VERSION_02_00, new PathBasedStaticResponseSimulator(finder, ONDERNEMING, KEY_ONDERNEMINGSNUMMER));
        simulator.register("GeefOndernemingVKBO", VERSION_02_00, new PathBasedStaticResponseSimulator(finder, ONDERNEMING, KEY_ONDERNEMINGSNUMMER));

        // Onderwijs
        simulator.register("GeefHistoriekInschrijving", VERSION_02_01, new PathBasedStaticResponseSimulator(finder, ONDERWIJS, KEY_INSZ));

        // Persoon
        simulator.register("GeefAttest", VERSION_02_00, new PathBasedStaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("GeefGezinssamenstelling", VERSION_02_00, new PathBasedStaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("GeefGezinssamenstelling", VERSION_02_02, new PathBasedStaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("GeefHistoriekGezinssamenstelling", VERSION_02_02, new PathBasedStaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("GeefHistoriekPersoon", VERSION_02_00, new PathBasedStaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("GeefHistoriekPersoon", VERSION_02_02, new PathBasedStaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("GeefPasfoto", VERSION_02_00, new RandomPasfotoSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("GeefPersoon", VERSION_02_02, new PathBasedStaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("RaadpleegLeerkredietsaldo", VERSION_01_00, new PathBasedStaticResponseSimulator(finder, PERSOON, KEY_INSZ));
        simulator.register("ZoekPersoonOpAdres", VERSION_02_02, new PathBasedStaticResponseSimulator(finder, PERSOON, "//Inhoud/Bron","//Criteria/Adres/PostCode", "//Criteria/Adres/Straatcode", "//Criteria/Adres/Huisnummer", "//Criteria/EnkelReferentiepersoon"));

        // Repertorium
        simulator.register("RegistreerInschrijving", VERSION_02_00, new PathBasedStaticResponseSimulator(finder, REPERTORIUM, KEY_INSZ));
        simulator.register("RegistreerInschrijving", VERSION_02_01, new PathBasedStaticResponseSimulator(finder, REPERTORIUM, "//Subject/Type", "//Subject/Sleutel"));
        simulator.register("RegistreerUitschrijving", VERSION_02_00, new PathBasedStaticResponseSimulator(finder, REPERTORIUM, KEY_INSZ));

        // SocEcon
        simulator.register("GeefStatusRechtOndersteuningen", VERSION_02_00, new PathBasedStaticResponseSimulator(finder, SOCECON, KEY_INSZ));

        // SocZek
        simulator.register("GeefBetalingenHandicap", VERSION_03_00, new PathBasedStaticResponseSimulator(finder, SOCZEK, KEY_SSIN));
        simulator.register("GeefDossierHandicap", VERSION_03_00, new PathBasedStaticResponseSimulator(finder, SOCZEK, KEY_SSIN));
        simulator.register("GeefLeefloonbedragen", VERSION_02_00, new PathBasedStaticResponseSimulator(finder, SOCZEK, KEY_INSZ));
        simulator.register("GeefSociaalStatuut", VERSION_03_00, new GeefSociaalStatuutSimulator(finder, SOCZEK, KEY_INSZ));
        simulator.register("GeefVolledigDossierHandicap", VERSION_03_00, new PathBasedStaticResponseSimulator(finder, SOCZEK, KEY_RRNR));

        // Vastgoed
        simulator.register("GeefEpc", VERSION_02_01, new PathBasedStaticResponseSimulator(finder, VASTGOED,  "//Criteria/Adres/Postcode", "//Criteria/Adres/Straat", "//Criteria/Adres/Huisnummer"));

        // Werk
        simulator.register("GeefLoopbaanARZA", VERSION_02_01, new PathBasedStaticResponseSimulator(finder, WERK, KEY_INSZ));
        simulator.register("GeefLoopbaanOnderbrekingen", VERSION_02_00, new PathBasedStaticResponseSimulator(finder, WERK, KEY_INSZ));
        simulator.register("GeefWerkrelaties", VERSION_02_00, new PathBasedStaticResponseSimulator(finder, WERK, KEY_INSZ));

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
