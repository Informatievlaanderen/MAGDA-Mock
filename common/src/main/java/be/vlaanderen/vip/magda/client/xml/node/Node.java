package be.vlaanderen.vip.magda.client.xml.node;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

public interface Node {

    Optional<String> getValue();
    
    /**
     * If node consists of multiple nodes, return them as a stream
     */
    Stream<Node> stream();
    
    Optional<Node> get(String path);
    
    Map<String, String> attributes();
    
}
