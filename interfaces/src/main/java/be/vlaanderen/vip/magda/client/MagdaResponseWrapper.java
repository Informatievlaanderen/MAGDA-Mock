package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.client.xml.node.Node;
import be.vlaanderen.vip.magda.client.xml.node.NodeListWrapper;
import be.vlaanderen.vip.magda.client.xml.node.XPathResolver;
import lombok.Getter;

import java.util.Optional;
import java.util.stream.Stream;

public class MagdaResponseWrapper {
    private final XPathResolver resolver = new XPathResolver();
    
    @Getter
    private MagdaResponse response;
    
    public MagdaResponseWrapper(
            MagdaResponse response) {
        this.response = response;
    }
    
    public Optional<Node> getNode(String path) {
        return resolver.getNode(response.getDocument().getXml(), path);
    }

    public Stream<Node> getNodes(String path) {
        return resolver.getNodeList(response.getDocument().getXml(), path)
                .map(NodeListWrapper::new)
                .map(NodeListWrapper::stream)
                .orElse(Stream.empty());
    }

}
