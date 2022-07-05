package be.vlaanderen.vip.magda.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.dom4j.dom.DOMNodeHelper;
import org.w3c.dom.Document;
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
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

@Slf4j
public class MagdaDocument {
    private final Document xml;
    private final XPathFactory xPathfactory = XPathFactory.newInstance();
    private final XPath xpath = xPathfactory.newXPath();

    public MagdaDocument(Document xml) {
        this.xml = xml;
    }

    public static MagdaDocument fromString(String document) {
        return new MagdaDocument(parseString(document));
    }

    public static MagdaDocument fromStream(InputStream document) {
        return new MagdaDocument(parseStream(document));
    }

    public static MagdaDocument fromResource(Class clazz, String name) {
        InputStream resource = clazz.getResourceAsStream(name);
        if (resource != null) {
            return new MagdaDocument(parseStream(resource));
        } else {
            return null;
        }
    }

    public static MagdaDocument fromTemplate(Aanvraag aanvraag) {
        return fromResource(MagdaDocument.class, "/templates/" + aanvraag.magdaService().getNaam() + "/" + aanvraag.magdaService().getVersie() + "/template.xml");
    }

    private static Document parseStream(InputStream resource) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setExpandEntityReferences(false);
            dbf.setIgnoringElementContentWhitespace(true);
            dbf.setValidating(false);
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document xmlInhoud = db.parse(resource);
            return xmlInhoud;
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new RuntimeException(e);
        }
    }

    private static Document parseString(String input) {
        return parseStream(IOUtils.toInputStream(input, "UTF-8"));

    }

    public Document getXml() {
        return xml;
    }

    public String getValue(String expression) {
        XPathExpression expr = null;
        try {
            expr = xpath.compile(expression);
            NodeList nodes = (NodeList) expr.evaluate(xml, XPathConstants.NODESET);
            return nodes.item(0).getTextContent();
        } catch (XPathExpressionException e) {
            log.warn("Fout bij het ophalen van waarde '{}' : ", expression, e);
        }
        return "";
    }

    public void setValue(String expression, String value) {
        NodeList nodes = xpath(expression);
        for (int pos = 0; pos < nodes.getLength(); pos++) {
            nodes.item(pos).setTextContent(value);
        }
    }

    public NodeList xpath(String expression) {
        try {
            XPathExpression expr = xpath.compile(expression);
            return (NodeList) expr.evaluate(xml, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            log.warn("Fout bij het uitvoeren van xpath '{}' : ", expression, e);
        }

        return new DOMNodeHelper.EmptyNodeList();
    }

    public String toString() {
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
            log.warn("Fout bij omzetten van XML naar string: ", e);
        }
        return "";
    }
}
