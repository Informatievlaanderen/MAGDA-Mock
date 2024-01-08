package be.vlaanderen.vip.magda.client.xml.node;

import java.util.Optional;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.NodeList;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XPathResolver {
    private final XPathFactory xPathfactory = XPathFactory.newInstance();
    private final XPath xpath = xPathfactory.newXPath();
    
    public Optional<Node> getNode(Object object, String path) {
        return getNodeList(object, path)
                .map(this::toNode);
    }

    public Optional<NodeList> getNodeList(Object object, String path) {
        try {
            return Optional.ofNullable(xpath.compile(path))
                           .map(exp -> evaluate(object, exp));
        } catch (XPathExpressionException e) {
            log.error("Something went wrong while executing xpath", e);
            return Optional.empty();
        }
    }

    public Node toNode(NodeList list) {
        if(list.getLength() == 0) {
            return null;
        }
        if(list.getLength() == 1) {
            return new NodeWrapper(list.item(0));
        }
        return new NodeListWrapper(list);
    }
    
    private NodeList evaluate(Object object, XPathExpression expression) {
        try {
            return (NodeList) expression.evaluate(object, XPathConstants.NODESET);
        } catch (Exception e) {
            throw new NodeException(e);
        }
    }
}
