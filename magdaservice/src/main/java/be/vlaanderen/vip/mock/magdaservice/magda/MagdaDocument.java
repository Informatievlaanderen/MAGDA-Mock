package be.vlaanderen.vip.mock.magdaservice.magda;

import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.apache.commons.io.IOUtils;
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

    public Document getXml() {
        return xml;
    }

    public static MagdaDocument fromString(String document) throws ParserConfigurationException, SAXException, IOException {
        return new MagdaDocument(parseString(document));
    }

    public static MagdaDocument fromStream(InputStream document) throws ParserConfigurationException, SAXException, IOException {
        return new MagdaDocument(parseStream(document));
    }

    private static Document parseStream(InputStream resource) throws ParserConfigurationException, SAXException, IOException {
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

    private static Document parseString(String input) throws IOException, ParserConfigurationException, SAXException {
        return parseStream(IOUtils.toInputStream(input, "UTF-8"));
    }

    public String getValue(String expression) {
        XPathExpression expr = null;
        try {
            expr = xpath.compile(expression);
            NodeList nodes = (NodeList) expr.evaluate(xml, XPathConstants.NODESET);
            if (nodes.getLength() == 1) {
                return nodes.item(0).getTextContent();
            }
        } catch (XPathExpressionException e) {
           log.warn( "Fout bij het ophalen van waarde '" + expression + "' : ", e);
        }
        return "";
    }

    public void setValue(String expression, String value) {
        try {
            XPathExpression expr = null;
            expr = xpath.compile(expression);
            NodeList nodes = (NodeList) expr.evaluate(xml, XPathConstants.NODESET);
            for (int pos = 0; pos < nodes.getLength(); pos++) {
                Node item = nodes.item(pos);
                item.setTextContent(value);
            }
        } catch (XPathExpressionException e) {
           log.warn( "Fout bij het zetten van waarde '" + expression + "' naar '" + value + "': ", e);
        }
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
           log.warn( "Fout bij omzetten van XML naar string: ", e);
        }
        return "";
    }
}
