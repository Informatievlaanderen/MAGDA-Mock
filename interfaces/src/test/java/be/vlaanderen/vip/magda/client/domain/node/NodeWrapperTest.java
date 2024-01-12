package be.vlaanderen.vip.magda.client.domain.node;

import be.vlaanderen.vip.magda.DomUtils;
import be.vlaanderen.vip.magda.client.xml.node.NodeWrapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;

class NodeWrapperTest {

    @Nested
    class GetValue {
        
        @Test
        void returnsValue() {
            var node = createNode("<Root>value</Root>", "/Root");
            
            var result = node.getValue();
            
            assertAll(
                    () -> assertThat(result.isPresent(), is(true)),
                    () -> assertThat(result.get(), is(equalTo("value"))));
        }
        
        @Test
        void emptyWhenNoValue() {
            var node = createNode("""
                    <Root>
                      <SubNode1>value1</SubNode1>
                      <SubNode2>value2</SubNode2>
                    </Root>
                    """, "/Root/SubNode3");

            var result = node.getValue();
            
            assertThat(result.isEmpty(), is(true));
        }
        
    }
    
    @Nested
    class Stream {
        
        @Test
        void isAStreamOfTheNode() {
            var node = createNode("<Root>value</Root>", "/Root");
            
            var result = node.stream()
                             .toList();
            
            assertThat(result, contains(node));
        }
        
    }
    
    @Nested
    class Get {
        
        @Test
        void executesXPathOnNode() {
            var node = createNode("""
                        <Root>
                            <Sub>
                                <MoreSub>value</MoreSub>
                            </Sub>
                        </Root>
                    """, "/Root");
            
            var subNode = node.get("Sub/MoreSub");
            
            assertAll(
                    () -> assertThat(subNode.isPresent(), is(true)),
                    () -> assertThat(subNode.get().getValue().get(), is(equalTo("value"))));
        }
        
        @Test
        void emptyWhenMissing() {
            var node = createNode("""
                    <Root>
                        <Sub>
                            <MoreSub>value</MoreSub>
                        </Sub>
                    </Root>
                """, "/Root");
            
            var result = node.get("SomethingWrong");
            
            assertThat(result.isPresent(), is(false));
        }
        
    }
    
    @Nested
    class Attributes {
        
        @Test
        void returnsAttributes() {
            var node = createNode("""
                    <Root>
                        <Sub Attr1="1" Attr2="2">
                            value
                        </Sub>
                    </Root>
                    """, "/Root/Sub");

            assertAll(
                    () -> assertThat(node.attributes(), hasEntry("Attr1", "1")),
                    () -> assertThat(node.attributes(), hasEntry("Attr2", "2")));
        }
        
    }

    private NodeWrapper createNode(String document, String path) {
        var doc = DomUtils.createDocument(document);
        return new NodeWrapper(DomUtils.getNode(doc, path));
    }
    
}
