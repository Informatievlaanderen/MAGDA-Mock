package be.vlaanderen.vip.magda.client.xml.node;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class NodeWrapper implements Node {
    private final XPathResolver resolver = new XPathResolver();
    private final org.w3c.dom.Node node;
    
    public NodeWrapper(org.w3c.dom.Node node) {
        this.node = node;
    }
    
    @Override
    public Optional<String> getValue() {
        return Optional.ofNullable(node)
                       .map(org.w3c.dom.Node::getTextContent);
    }

    @Override
    public Stream<Node> stream() {
        return Stream.of(this);
    }

    @Override
    public Optional<Node> get(String path) {
        return resolver.getNode(node, path);
    }
    
    public Map<String, String> attributes() {
        var attr = node.getAttributes();
        return IntStream.iterate(0, i -> i < attr.getLength(), i -> i + 1)
                        .mapToObj(attr::item)
                        .collect(HashMap::new, 
                                 (m, i) -> m.put(i.getLocalName(), 
                                                 i.getTextContent()), 
                                 Map::putAll);
    }
    
}
