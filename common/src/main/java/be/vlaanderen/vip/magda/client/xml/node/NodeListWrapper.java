package be.vlaanderen.vip.magda.client.xml.node;

import org.w3c.dom.NodeList;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class NodeListWrapper implements Node {
    private NodeList nodeList;
    
    public NodeListWrapper(NodeList nodeList) {
        this.nodeList = nodeList;
    }
    
    @Override
    public Optional<String> getValue() {
        return Optional.of(nodeList)
                       .filter(list -> list.getLength() == 1)
                       .map(list -> list.item(0))
                       .map(org.w3c.dom.Node::getTextContent);
    }

    @Override
    public Stream<Node> stream() {
        return IntStream.range(0, nodeList.getLength())
                        .mapToObj(i -> nodeList.item(i))
                        .map(NodeWrapper::new);
    }

    @Override
    public Optional<Node> get(String path) {
        return Optional.empty();
    }
    
    @Override
    public Map<String, String> attributes() {
        return new HashMap<>();
    }
}
