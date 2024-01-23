package be.vlaanderen.vip.magda.client.domain.node;

import be.vlaanderen.vip.magda.DomUtils;
import be.vlaanderen.vip.magda.client.xml.node.Node;
import be.vlaanderen.vip.magda.client.xml.node.NodeListWrapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

class NodeListWrapperTest {

    @Nested
    class GetValue {
        
        @Test
        void returnsItemContent_whenListSize1() {
            var node = createNode("""
                    <Root>
                        <Sub>value1</Sub>
                    </Root>
                    """, "/Root/Sub");
            
            var result = node.getValue().orElse(null);
            
            assertThat(result, is(equalTo("value1")));
            
        }
        
        @Test
        void returnsEmpty_whenListSizeNot1() {
            var node = createNode("""
                    <Root>
                        <Sub>value1</Sub>
                        <Sub>value2</Sub>
                        <Sub>value3</Sub>
                    </Root>
                    """, "/Root/Sub");

            var result = node.getValue();
            
            assertThat(result.isEmpty(), is(true));            
        }
        
    }

    @Nested
    class Stream {
        
        @Test
        void streamForEveryNodeInList() {
            var node = createNode("""
                    <Root>
                        <Sub>value1</Sub>
                        <Sub>value2</Sub>
                        <Sub>value3</Sub>
                    </Root>
                    """, "/Root/Sub");
            
            var result = node.stream()
                             .map(Node::getValue)
                             .flatMap(Optional::stream)
                             .toList();
            
            assertThat(result, contains("value1", "value2", "value3"));
        }
        
    }

    @Nested
    class Get {
        
        @Test
        void isEmpty() {
            var node = createNode("""
                    <Root>
                        <Sub><ExtraSub>value1</ExtraSub></Sub>
                        <Sub><ExtraSub>value2</ExtraSub></Sub>
                        <Sub><ExtraSub>value3</ExtraSub></Sub>
                    </Root>
                    """, "/Root/Sub");
            
            var result = node.get("ExtraSub");
            
            assertThat(result.isEmpty(), is(true));
        }
        
    }


    private NodeListWrapper createNode(String document, String path) {
        var doc = DomUtils.createDocument(document);
        return new NodeListWrapper(DomUtils.getNodeList(doc, path));
    }

}
