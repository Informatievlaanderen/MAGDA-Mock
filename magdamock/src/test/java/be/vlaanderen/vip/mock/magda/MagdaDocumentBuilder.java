package be.vlaanderen.vip.mock.magda;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.util.Map;

public class MagdaDocumentBuilder {
    private static final XmlMapper mapper = new XmlMapper();
    
    private MagdaDocumentBuilder() {}
    
    public static MagdaDocument request(Map<Object, Object> map) {
        return MagdaDocument.fromString(toXmlString("Verzoek", map));
    }
    
    private static String toXmlString(String root, Object obj) {
        try {
            return mapper.writerWithDefaultPrettyPrinter()
                         .withRootName(root)
                         .writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to map object to xml", e);
        }
    }
}
