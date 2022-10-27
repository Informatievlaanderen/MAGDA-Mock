package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.Aanvraag;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.connection.MagdaConnection;
import be.vlaanderen.vip.magda.exception.MagdaSendFailed;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isEmpty;
import static org.apache.commons.lang3.StringUtils.isNotEmpty;

@Slf4j
public class MagdaMockConnection implements MagdaConnection {
    @Getter
    private final List<Document> verzondenDocumenten = new ArrayList<>();
    private Document defaultResponse = null;
    private final XPathFactory xPathfactory = XPathFactory.newInstance();
    private final XPath xpath = xPathfactory.newXPath();

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

        String key1 = request.getValue("//INSZ");
        String key0 = null;
        if (isEmpty(key1)) {
            key1 = request.getValue("//rrnr");
        }
        if (isEmpty(key1)) {
            key1 = request.getValue("//Criteria/Ondernemingsnummer");
        }
        if (isEmpty(key1)) {
            key1 = request.getValue("//Ondernemingsnummer");

        }
        if (isEmpty(key1)) {
            key1 = request.getValue("//Criteria/GebouweenheidId");
            if (isNotEmpty(key1)) {
                key0 = request.getValue("//Criteria/Attesten");
                if (isEmpty(key0)) {
                    key0 = "0";
                }
            }
        }
        if (isEmpty(key1)) {
            key1 = request.getValue("//Criteria/Sleutel/Waarde");
        }
        if (isEmpty(key1)) {
            key1 = request.getValue("//Subject/Sleutel");
        }

        String referte = request.getValue("//Afzender/Referte");
        String identificatie = request.getValue("//Afzender/Identificatie");
        String hoedanigheid = request.getValue("//Afzender/Hoedanigheid");
        String gebruiker = request.getValue("//Afzender/Gebruiker");


        var response = loadResource(dienst, versie, key0, key1);
        if (response == null) {
            response = loadResource(dienst, versie, key0, "notfound");
        }
        if (response == null) {
            response = loadResource(dienst, versie, key0, "success");
        }

        if (response != null) {
            response.setValue("//Referte", referte);
            response.setValue("//Ontvanger/Identificatie", identificatie);
            response.setValue("//Ontvanger/Hoedanigheid", hoedanigheid);
            response.setValue("//Ontvanger/Gebruiker", gebruiker);

            SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
            String today = dayFormat.format(new Date());
            response.setValue("//Context/Bericht/Tijdstip/Datum", today);

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.S");
            String time = timeFormat.format(new Date());
            response.setValue("//Context/Bericht/Tijdstip/Tijd", time);

            // Identificeert antwoord als komend van Magda Mock
            response.setValue("//Afzender/Referte", UUID.randomUUID().toString());
            response.setValue("//Afzender/Identificatie", "kb.vlaanderen.be/aiv/magda-mock-server");
            response.setValue("//Afzender/Naam", "Magda Mock Server");

            String soap = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" >\n" +
                    "  <soapenv:Header/>\n" +
                    "  <soapenv:Body>\n" +
                    response +
                    "  </soapenv:Body>\n" +
                    "</soapenv:Envelope>";
            return parseXml(soap);
        } else {
            log.warn("Geen mock data gevonden voor request naar {} {}", dienst, versie);
            return null;
        }
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

    private MagdaDocument loadResource(String dienst, String versie, String prefix, String identificatie) {
        String testResource = "/magda_simulator/" + dienst + "/" + versie + "/";
        if (isNotEmpty(prefix)) {
            testResource += prefix + "/";
        }
        testResource += identificatie + ".xml";
        try (InputStream resource = this.getClass().getResourceAsStream(testResource)) {
            if (Objects.nonNull(resource)) {
                return MagdaDocument.fromStream(resource);
            }
        } catch (Exception e) {
            log.error("Fout bij het laden van resource {}: ", testResource, e);
        }
        return null;
    }


    public void setDefaultResponse(Document xml) {
        defaultResponse = xml;
    }
}
