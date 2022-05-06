package be.vlaanderen.vip.magda.client.connection;

import be.vlaanderen.vip.magda.client.Aanvraag;
import be.vlaanderen.vip.magda.exception.MagdaSendFailed;
import lombok.CustomLog;
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
    private List<Document> verzondenDocumenten = new ArrayList<>();
    private Document defaultResponse = null;
    private final XPathFactory xPathfactory = XPathFactory.newInstance();
    private final XPath xpath = xPathfactory.newXPath();

    @Override
    public Document sendDocument(Aanvraag aanvraag, Document xml) throws MagdaSendFailed {
        log.info("Answering using MAGDA Mock");
        verzondenDocumenten.add(xml);
        if (defaultResponse != null) {
            Document answer = defaultResponse;
            defaultResponse = null;
            return answer;
        }

        String key1 = getValue(xml, "//INSZ");
        String key0 = null;
        if (isEmpty(key1)) {
            key1 = getValue(xml, "//rrnr");
        }
        if (isEmpty(key1)) {
            key1 = getValue(xml, "//Criteria/Ondernemingsnummer");
        }
        if (isEmpty(key1)) {
            key1 = getValue(xml, "//Ondernemingsnummer");

        }
        if (isEmpty(key1)) {
            key1 = getValue(xml, "//Criteria/GebouweenheidId");
            if (isNotEmpty(key1)) {
                key0 = getValue(xml, "//Criteria/Attesten");
                if (isEmpty(key0)) {
                    key0 = "0";
                }
            }
        }
        if (isEmpty(key1)) {
            key1 = getValue(xml, "//Criteria/Sleutel/Waarde");
        }
        if (isEmpty(key1)) {
            key1 = getValue(xml, "//Subject/Sleutel");
        }

        String dienst = getValue(xml, "//Context/Naam");
        String versie = getValue(xml, "//Context/Versie");
        String referte = getValue(xml, "//Afzender/Referte");
        String identificatie = getValue(xml, "//Afzender/Identificatie");
        String hoedanigheid = getValue(xml, "//Afzender/Hoedanigheid");
        String gebruiker = getValue(xml, "//Afzender/Gebruiker");


        Document response = loadResource(dienst, versie, key0, key1);
        if (response == null) {
            response = loadResource(dienst, versie, key0, "notfound");
        }
        if (response == null) {
            response = loadResource(dienst, versie, key0, "success");
        }

        if (response != null) {
            setValue(response, "//Referte", referte);
            setValue(response, "//Ontvanger/Identificatie", identificatie);
            setValue(response, "//Ontvanger/Hoedanigheid", hoedanigheid);
            setValue(response, "//Ontvanger/Gebruiker", gebruiker);

            SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
            String today = dayFormat.format(new Date());
            setValue(response, "//Context/Bericht/Tijdstip/Datum", today);

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.S");
            String time = timeFormat.format(new Date());
            setValue(response, "//Context/Bericht/Tijdstip/Tijd", time);

            // Identificeert antwoord als komend van Burgerloket Magda Mock
            setValue(response, "//Afzender/Referte", UUID.randomUUID().toString());
            setValue(response, "//Afzender/Identificatie", "kb.vlaanderen.be/aiv/burgerloket-wwoom-mock-server-unittest");
            setValue(response, "//Afzender/Naam", "Burgerloket Magda Mock Unittest");

            String soap = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" >\n" +
                    "  <soapenv:Header/>\n" +
                    "  <soapenv:Body>\n" +
                    toXmlString(response) +
                    "  </soapenv:Body>\n" +
                    "</soapenv:Envelope>";
            return parseXml(soap);
        } else {
            log.warn(String.format("Geen mock data gevonden voor request naar %s", aanvraag.magdaService().getNaam()));
        }

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

    private Document loadResource(String dienst, String versie, String prefix, String identificatie) {
        String testResource = "/magda_simulator/" + dienst + "/" + versie + "/";
        if (isNotEmpty(prefix)) {
            testResource += prefix + "/";
        }
        testResource += identificatie + ".xml";
        try (InputStream resource = this.getClass().getResourceAsStream(testResource)) {
            if (Objects.nonNull(resource)) {
                return parseStream(resource);
            }
        } catch (Exception e) {
            log.error("Fout bij het laden van resource: ", e);
        }
        return null;
    }

    private Document parseStream(InputStream resource) throws ParserConfigurationException, SAXException, IOException {
        Document xmlInhoud;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setExpandEntityReferences(false);
        dbf.setIgnoringElementContentWhitespace(true);
        dbf.setValidating(false);
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        xmlInhoud = db.parse(resource);
        return xmlInhoud;
    }

    public String toXmlString(Document xml) {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(xml), new StreamResult(writer));
            return writer.toString();
        } catch (TransformerException e) {
            log.error("Fout bij het omzetten van XML naar string: ", e);
        }
        return "";
    }

    public void setDefaultResponse(Document xml) {
        defaultResponse = xml;
    }

    public String getValue(Document xml, String path) {
        XPathExpression expr = null;
        try {
            expr = xpath.compile(path);
            NodeList nodes = (NodeList) expr.evaluate(xml, XPathConstants.NODESET);
            if (nodes.getLength() == 1) {
                final Node item = nodes.item(0);
                return item.getTextContent();
            }
        } catch (XPathExpressionException e) {
            log.error("Fout in getValue '" + path + "': ", e);
        }

        return "";
    }


    public void setValue(Document xml, String expression, String value) {
        try {
            XPathExpression expr = null;
            expr = xpath.compile(expression);
            NodeList nodes = (NodeList) expr.evaluate(xml, XPathConstants.NODESET);
            for (int pos = 0; pos < nodes.getLength(); pos++) {
                nodes.item(pos).setTextContent(value);
            }
        } catch (XPathExpressionException e) {
            log.warn("Fout bij het zetten van waarde '" + expression + "' naar '" + value + "': ", e);
        }
    }
}
