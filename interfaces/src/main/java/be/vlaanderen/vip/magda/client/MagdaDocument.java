package be.vlaanderen.vip.magda.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.dom4j.dom.DOMNodeHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.namespace.NamespaceContext;
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
import java.util.Iterator;

@Slf4j
public class MagdaDocument {
    private final Document xml;
    private final XPathFactory xPathfactory = XPathFactory.newInstance();

    public MagdaDocument(Document xml) {
        this.xml = xml;
    }

    public static MagdaDocument fromString(String document) {
        return new MagdaDocument(parseString(document));
    }

    public static MagdaDocument fromStream(InputStream document) {
        return new MagdaDocument(parseStream(document));
    }

    public static MagdaDocument fromResource(Class<?> clazz, String name) {
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

    public static MagdaDocument fromDocument(Document doc) {
        return new MagdaDocument(doc);
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
        return getValue(expression, null);
    }

    public String getValue(String expression, String defaultNamespace) {
        var nodes = xpath(expression, defaultNamespace);
        if (nodes == null || nodes.getLength() == 0) {
            return null;
        }
        return nodes.item(0).getTextContent();
    }

    public NodeList xpath(String expression, String defaultNamespace) {
        final XPath xpath = makeXpath(defaultNamespace);
        XPathExpression expr = null;
        try {
            expr = xpath.compile(expression);
            NodeList nodes = (NodeList) expr.evaluate(xml, XPathConstants.NODESET);
            return nodes;
        } catch (XPathExpressionException e) {
            log.warn("Fout bij het ophalen van waarde '{}' : ", expression, e);
        }
        return new DOMNodeHelper.EmptyNodeList();
    }

    public NodeList xpath(String expression) {
        return xpath(expression, null);
    }

    private XPath makeXpath(String defaultNamespace) {
        NamespaceContext ctx = new NamespaceContext() {
            public String getNamespaceURI(String prefix) {
                return prefix.equals("soapenv") ? "http://schemas.xmlsoap.org/soap/envelope/"
                        : prefix.equals("wsu") ? "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd"
                        : prefix.equals("wsse") ? "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd"
                        : prefix.equals("ds") ? "http://www.w3.org/2000/09/xmldsig#"
                        : defaultNamespace;
            }

            public Iterator<String> getPrefixes(String val) {
                return null;
            }

            public String getPrefix(String uri) {
                return null;
            }
        };

        final XPath xpath = xPathfactory.newXPath();
        xpath.setNamespaceContext(ctx);
        return xpath;
    }


    public void setValue(String expression, String value) {
        NodeList nodes = xpath(expression);
        for (int pos = 0; pos < nodes.getLength(); pos++) {
            nodes.item(pos).setTextContent(value);
        }
    }

    public void setValue(String expression, String value, String defaultNamespace) {
        NodeList nodes = xpath(expression, defaultNamespace);
        for (int pos = 0; pos < nodes.getLength(); pos++) {
            nodes.item(pos).setTextContent(value);
        }
    }


    public Node createNode(String expression, String nodeName) {
        var node = xml.createElement(nodeName);
        xpath(expression).item(0).appendChild(node);
        return node;
    }

    public Node createTextNode(String expression, String nodeName, String value) {
        var node = xml.createElement(nodeName);
        var textNode = xml.createTextNode(value);
        node.appendChild(textNode);
        xpath(expression).item(0).appendChild(node);
        return node;
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
