package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.client.util.XmlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.dom4j.dom.DOMNodeHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
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
        var resource = clazz.getResourceAsStream(name);
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
            var dbf = DocumentBuilderFactory.newInstance();
            dbf.setExpandEntityReferences(false);
            dbf.setIgnoringElementContentWhitespace(true);
            dbf.setValidating(false);
            dbf.setNamespaceAware(true);
            var db = dbf.newDocumentBuilder();
            var reader = new InputStreamReader(resource, StandardCharsets.UTF_8);
            var is = new InputSource(reader);
            is.setEncoding("UTF-8");

            return db.parse(is);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new MagdaDocumentException("Failed to parse stream as document", e);
        }
    }

    private static Document parseString(String input) {
        return parseStream(IOUtils.toInputStream(input, StandardCharsets.UTF_8));
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
        final var xpath = makeXpath(defaultNamespace);
        try {
            // TODO improvement: use pre-compiled xpath expressions instead of compiling them on the fly
            return (NodeList) xpath.compile(expression).evaluate(xml, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            log.warn("Error retrieving value '{}' : ", expression, e);
        }
        return new DOMNodeHelper.EmptyNodeList();
    }

    public NodeList xpath(String expression) {
        return xpath(expression, null);
    }

    private XPath makeXpath(String defaultNamespace) {
        var ctx = new NamespaceContext() {
            public String getNamespaceURI(String prefix) {
                return switch (prefix) {
                    case "soapenv" -> "http://schemas.xmlsoap.org/soap/envelope/";
                    case "wsu"     -> "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
                    case "wsse"    -> "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
                    case "ds"      -> "http://www.w3.org/2000/09/xmldsig#";
                    default        -> defaultNamespace;
                };
            }

            public Iterator<String> getPrefixes(String val) {
                return null;
            }

            public String getPrefix(String uri) {
                return null;
            }
        };

        final var xpath = xPathfactory.newXPath();
        xpath.setNamespaceContext(ctx);
        return xpath;
    }

    public void setValue(String expression, String value) {
        var nodes = xpath(expression);
        for (var pos = 0; pos < nodes.getLength(); pos++) {
            nodes.item(pos).setTextContent(value);
        }
    }

    public void createNode(String expression, String nodeName) {
        var node = xml.createElement(nodeName);
        xpath(expression).item(0).appendChild(node);
    }

    public void createTextNode(String expression, String nodeName, String value) {
        var node = xml.createElement(nodeName);
        var textNode = xml.createTextNode(value);
        node.appendChild(textNode);
        xpath(expression).item(0).appendChild(node);
    }

    public void removeNode(String expression) {
        var nodes = xpath(expression);
        for(var pos = nodes.getLength() - 1; pos >= 0; pos--) { // traverse the list in reverse, because this is going to be messing with the indices
            var elm = (Element)nodes.item(pos);
            elm.getParentNode().removeChild(elm);
        }
    }

    public MagdaServiceIdentificatie getServiceIdentification() {
        return new MagdaServiceIdentificatie(
                getValue("//Verzoek/Context/Naam"),
                getValue("//Verzoek/Context/Versie"));
    }

    public String toString() {
        try {
            var transformer = XmlUtils.createTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            var writer = new StringWriter();
            transformer.transform(new DOMSource(xml), new StreamResult(writer));
            return writer.toString();
        } catch (TransformerException e) {
            log.warn("Error converting XML to string: ", e);
        }
        return "";
    }
}
