package be.vlaanderen.vip.mock.magda;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import be.vlaanderen.vip.magda.client.MagdaDocument;

public class MagdaDocumentBuilder {
    private static XmlMapper mapper = new XmlMapper();
    
    MagdaDocumentBuilder() {}

    
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
