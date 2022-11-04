package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.Aanvraag;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.connection.MagdaConnection;
import be.vlaanderen.vip.magda.exception.MagdaSendFailed;
import be.vlaanderen.vip.mock.magda.client.simulators.RandomPasfotoSimulator;
import be.vlaanderen.vip.mock.magda.client.simulators.SOAPSimulator;
import be.vlaanderen.vip.mock.magda.client.simulators.StaticResponseSimulator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class MagdaMockConnection implements MagdaConnection {
    @Getter
    private final List<Document> verzondenDocumenten = new ArrayList<>();
    private Document defaultResponse = null;

    private static HashMap<String, SOAPSimulator> simulators;

    // Subcategories for MAGDA services
    private static final String PERSOON = "Persoon";
    private static final String ONDERNEMING = "Onderneming";
    private static final String VASTGOED = "Vastgoed";

    private static final String KEY_INSZ = "//INSZ";

    private static final String KEY_ONDERNEMINGSNUMMER = "//Ondernemingsnummer";

    private static final String KEY_RRNR = "//rrnr";

    static {
        simulators = new HashMap<>();

        // PERSOON Standaard
        simulators.put("RegistreerInschrijving/02.00.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));
        simulators.put("RegistreerInschrijving/02.01.0000", new StaticResponseSimulator(PERSOON, "//Subject/Type", "//Subject/Sleutel"));
        simulators.put("RegistreerUitschrijving/02.00.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));

        simulators.put("GeefBewijs/02.00.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));
        simulators.put("GeefHistoriekInschrijving/02.01.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));
        simulators.put("RaadpleegLeerkredietsaldo/01.00.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));

        simulators.put("GeefLoopbaanOnderbrekingen/02.00.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));
        simulators.put("GeefStatusRechtOndersteuningen/02.00.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));
        simulators.put("GeefFuncties/02.00.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));
        simulators.put("GeefDossiers/02.00.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));
        simulators.put("GeefKindVoordelen/02.00.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));
        simulators.put("GeefVolledigDossierHandicap/03.00.0000", new StaticResponseSimulator(PERSOON, KEY_RRNR));

        simulators.put("GeefPersoon/02.02.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));
        simulators.put("GeefHistoriekPersoon/02.00.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));
        simulators.put("GeefHistoriekPersoon/02.02.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));
        simulators.put("GeefGezinssamenstelling/02.00.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));
        simulators.put("GeefGezinssamenstelling/02.02.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));

        simulators.put("GeefDossierKBI/01.00.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));

        simulators.put("GeefAanslagbiljetPersonenbelasting/02.00.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));

        simulators.put("ZoekEigendomstoestanden/02.00.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));

        simulators.put("ZoekPersoonOpAdres/02.02.0000", new StaticResponseSimulator(PERSOON, "//Inhoud/Bron","//Criteria/Adres/PostCode", "//Criteria/Adres/Straatcode", "//Criteria/Adres/Huisnummer", "//Criteria/EnkelReferentiepersoon"));

        // PERSOON Custom
        simulators.put("GeefAttest/02.00.0000", new StaticResponseSimulator(PERSOON, KEY_INSZ));
        simulators.put("GeefPasfoto/02.00.0000", new RandomPasfotoSimulator(PERSOON, KEY_INSZ));

        // ONDERNEMING
        simulators.put("GeefOnderneming/02.00.0000", new StaticResponseSimulator(ONDERNEMING, KEY_ONDERNEMINGSNUMMER));
        simulators.put("GeefOndernemingVKBO/02.00.0000", new StaticResponseSimulator(ONDERNEMING, KEY_ONDERNEMINGSNUMMER));

        // GEBOUW
        simulators.put("GeefEpc/02.00.0000", new StaticResponseSimulator(VASTGOED, "//Criteria/Attesten", "//Criteria/GebouweenheidId"));
        simulators.put("GeefEpc/02.01.0000", new StaticResponseSimulator(VASTGOED, "//Criteria/Attesten", "//Criteria/GebouweenheidId", "//Criteria/Adres/Postcode", "//Criteria/Adres/Straat", "//Criteria/Adres/Huisnummer"));

    }

    @Override
    public Document sendDocument(Aanvraag aanvraag, Document xml) throws MagdaSendFailed {
        return sendDocument(xml);
    }

    @Override
    public Document sendDocument(Document xml) throws MagdaSendFailed {
        log.info("Answering using MAGDA Mock");

        verzondenDocumenten.add(xml);
        if (defaultResponse != null) {
            Document answer = defaultResponse;
            defaultResponse = null;
            return answer;
        }

        return send(xml);
    }

    private Document send(Document xml) {
        var request = MagdaDocument.fromDocument(xml);
        var dienst = request.getValue("//Verzoek/Context/Naam");
        var versie = request.getValue("//Verzoek/Context/Versie");

        var simulator = simulators.get(dienst + "/" + versie);
        if (simulator != null) {
            var response = simulator.send(request);
            String soap = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" >\n" +
                    "  <soapenv:Header/>\n" +
                    "  <soapenv:Body>\n" +
                    response +
                    "  </soapenv:Body>\n" +
                    "</soapenv:Envelope>";
            return MagdaDocument.fromString(soap).getXml();
        }
        log.error("Er is geen magda simulator geregistreerd voor {}/{}", dienst, versie);
        return null;
    }

    public void setDefaultResponse(Document xml) {
        defaultResponse = xml;
    }
}
