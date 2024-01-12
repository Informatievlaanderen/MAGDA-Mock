package be.vlaanderen.vip.magda;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class DomUtils {
    
    DomUtils() { }
    
    public static Document createDocument(InputStream is) throws SAXException, IOException, ParserConfigurationException {
        var fact = DocumentBuilderFactory.newInstance();
        fact.setExpandEntityReferences(false);
        fact.setIgnoringElementContentWhitespace(true);
        fact.setValidating(false);
        fact.setNamespaceAware(true);
        return fact.newDocumentBuilder()
                   .parse(is);
    }

    public static Document createDocument(String input) {
        try {
            return DomUtils.createDocument(new ByteArrayInputStream(input.getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Node getNode(Document document, String xpath) {
        try {
            var fact = XPathFactory.newInstance();
            var path = fact.newXPath();
            return (Node) path.compile(xpath)
                              .evaluate(document, XPathConstants.NODE);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static NodeList getNodeList(Document document, String xpath) {
        try {
            var fact = XPathFactory.newInstance();
            var path = fact.newXPath();
            return (NodeList) path.compile(xpath)
                                  .evaluate(document, XPathConstants.NODESET);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
}
