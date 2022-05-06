package be.vlaanderen.vip.magda.client;

import lombok.extern.slf4j.Slf4j;

import org.dom4j.dom.DOMNodeHelper;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.StringWriter;

@Slf4j
public class XmlUtil {
    private static final ThreadLocal<XPathFactory> xPathfactory = ThreadLocal.withInitial(XPathFactory::newInstance);
    private static final ThreadLocal<XPath> xpath = ThreadLocal.withInitial(() -> xPathfactory.get().newXPath());

    public static String getValue(Node xml, String expression) {
        XPathExpression expr = null;
        try {
            expr = xpath.get().compile(expression);
            NodeList nodes = (NodeList) expr.evaluate(xml, XPathConstants.NODESET);
            return nodes.item(0).getTextContent();
        } catch (XPathExpressionException e) {
            log.warn("Fout bij het ophalen van waarde '{}' : ", expression, e);
        }
        return "";
    }

    public static void setValue(Node xml, String expression, String value) {
        NodeList nodes = xpath(xml, expression);
        for (int pos = 0; pos < nodes.getLength(); pos++) {
            nodes.item(pos).setTextContent(value);
        }
    }

    public static NodeList xpath(Node xml, String expression) {
        try {
            XPathExpression expr = xpath.get().compile(expression);
            return (NodeList) expr.evaluate(xml, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            log.warn("Fout bij het uitvoeren van xpath '{}' : ", expression, e);
        }

        return new DOMNodeHelper.EmptyNodeList();
    }

    public static String toString(Node xml) {
        try {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
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
