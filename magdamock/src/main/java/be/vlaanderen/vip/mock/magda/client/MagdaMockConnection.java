package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.Aanvraag;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.connection.MagdaConnection;
import be.vlaanderen.vip.magda.exception.MagdaSendFailed;
import be.vlaanderen.vip.mock.magda.client.simulators.SOAPSimulator;
import be.vlaanderen.vip.mock.magda.client.simulators.StaticResponseSimulator;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class MagdaMockConnection implements MagdaConnection {
    @Getter
    private final List<Document> verzondenDocumenten = new ArrayList<>();
    private Document defaultResponse = null;

    private static HashMap<String, SOAPSimulator> simulators;

    static {
        simulators = new HashMap<>();

        // PERSOON Standaard
        simulators.put("RegistreerInschrijving/02.00.0000", new StaticResponseSimulator("//INSZ"));
        simulators.put("RegistreerInschrijving/02.01.0000", new StaticResponseSimulator("//Subject/Type", "//Subject/Sleutel"));
        simulators.put("RegistreerUitschrijving/02.00.0000", new StaticResponseSimulator("//INSZ"));

        simulators.put("GeefBewijs/02.00.0000", new StaticResponseSimulator("//INSZ"));
        simulators.put("GeefHistoriekInschrijving/02.01.0000", new StaticResponseSimulator("//INSZ"));
        simulators.put("RaadpleegLeerkredietsaldo/01.00.0000", new StaticResponseSimulator("//INSZ"));

        simulators.put("GeefLoopbaanOnderbrekingen/02.00.0000", new StaticResponseSimulator("//INSZ"));
        simulators.put("GeefStatusRechtOndersteuningen/02.00.0000", new StaticResponseSimulator("//INSZ"));
        simulators.put("GeefFuncties/02.00.0000", new StaticResponseSimulator("//INSZ"));
        simulators.put("GeefDossiers/02.00.0000", new StaticResponseSimulator("//INSZ"));
        simulators.put("GeefKindVoordelen/02.00.0000", new StaticResponseSimulator("//INSZ"));
        simulators.put("GeefVolledigDossierHandicap/03.00.0000", new StaticResponseSimulator("//rrnr"));


        simulators.put("GeefPersoon/02.02.0000", new StaticResponseSimulator("//INSZ"));
        simulators.put("GeefHistoriekPersoon/02.00.0000", new StaticResponseSimulator("//INSZ"));
        simulators.put("GeefHistoriekPersoon/02.02.0000", new StaticResponseSimulator("//INSZ"));
        simulators.put("GeefGezinssamenstelling/02.00.0000", new StaticResponseSimulator("//INSZ"));
        simulators.put("GeefGezinssamenstelling/02.02.0000", new StaticResponseSimulator("//INSZ"));

        simulators.put("GeefDossierKBI/01.00.0000", new StaticResponseSimulator("//INSZ"));

        simulators.put("GeefAanslagbiljetPersonenbelasting/02.00.0000", new StaticResponseSimulator("//INSZ"));

        simulators.put("ZoekEigendomstoestanden/02.00.0000", new StaticResponseSimulator("//INSZ"));

        // PERSOON Custom
        simulators.put("GeefAttest/02.00.0000", new StaticResponseSimulator("//INSZ"));
        simulators.put("GeefPasfoto/02.00.0000", new StaticResponseSimulator("//INSZ"));

        // ONDERNEMING
        simulators.put("GeefOnderneming/02.00.0000", new StaticResponseSimulator("//Ondernemingsnummer"));
        simulators.put("GeefOndernemingVKBO/02.00.0000", new StaticResponseSimulator("//Ondernemingsnummer"));

        // GEBOUW
        simulators.put("GeefEpc/02.00.0000", new StaticResponseSimulator("//Criteria/Attesten", "//Criteria/GebouweenheidId"));

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
            return parseXml(soap);
        }
        log.error("Er is geen magda simulator geregistreerd voor {}/{}", dienst, versie);
        return null;
    }

    private Document parseXml(String xmlDocument) {
        Document document = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setExpandEntityReferences(false);
        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setValidating(false);
        dbf.setNamespaceAware(true);
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
            document = db.parse(IOUtils.toInputStream(xmlDocument, "UTF-8"));
        } catch (Exception e) {
            log.error("Fout bij het parsen van de soap XML: ", e);
        }
        return document;
    }

    public void setDefaultResponse(Document xml) {
        defaultResponse = xml;
    }
}
