package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DynamicContainer;
import org.junit.jupiter.api.DynamicNode;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.XMLConstants;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicContainer.dynamicContainer;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class TemplateXmlValidationTest {

    private static final String baseXsd = "template_xsd/";
    private static final String baseXml = "templates/";

    private final Map<String, String> XML_FOLDERS_AND_XSDS = data(
            "GeefJaarrekeningen/02.00.0000", "GeefJaarrekeningen/02.00.0000/GeefJaarrekeningen.xsd");

    @TestFactory
    Stream<DynamicNode> validateXmls() {
        return XML_FOLDERS_AND_XSDS.entrySet()
                                   .stream()
                                   .map(e -> forXsd(e.getKey(), e.getValue()));
    }
    
    DynamicContainer forXsd(String xmlFolder, String xsd) {
        var validator = getValidator(testFile(baseXsd + xsd));
        return dynamicContainer(xmlFolder, 
                                findXmls(mainFile(baseXml + xmlFolder))
                                    .map(xml -> forXml(xml, validator)));
    }
    
    DynamicTest forXml(File xml, Validator validator) {
        return dynamicTest(xml.getName(), () -> validateXml(xml, validator));
    }
    
    @SneakyThrows
    private Stream<File> findXmls(File file) {
        if(file.isDirectory()) {
            return Arrays.stream(Objects.requireNonNull(file.listFiles()))
                         .flatMap(this::findXmls);
        }
        else if(isXml(file)) {
            return Stream.of(file);
        }
        return Stream.empty();
    }
    
    private boolean isXml(File file) {
        return file.getName().endsWith("xml");
    }
    
    private static Map<String, String> data(String... data) {
        return IntStream.iterate(0, i -> i < data.length, i -> i + 2)
                        .collect(HashMap::new, 
                                 (m, i) -> m.put(data[i], data[i+1]),
                                 HashMap::putAll);
    }
    
    void validateXml(File xml, Validator validator) {
        try {
            validator.validate(new DOMSource(findRequestRootNote(MagdaDocument.fromStream(new FileInputStream(xml)))));
        } catch (Exception e) {
            fail("Validation failure for %s: %s".formatted(xml.getName(), e.getMessage()));
        }
    }

    private Node findRequestRootNote(MagdaDocument doc) {
        return ((NodeList) doc.xpath("/soapenv:Envelope/soapenv:Body").item(0)).item(0);
    }

    private Validator getValidator(File xsd) {
        try {
            var factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            var schema = factory.newSchema(xsd);
            var validator =  schema.newValidator();
            validator.setErrorHandler(new XsdErrorHandler());
            return validator;
        } catch (Exception e) {
            fail("Cannot create xml validator for %s: %s".formatted(xsd.getName(), e.getMessage()));
            return null;
        }
    }
    
    private File mainFile(String path) {
        return new File("src/main/resources/" + path);
    }
    
    private File testFile(String path) {
        return new File("src/test/resources/" + path);
    }
    
    public static class XsdErrorHandler implements ErrorHandler {

        @Override
        public void warning(SAXParseException exception) { }

        @Override
        public void error(SAXParseException exception) throws SAXException {
            handleMessage(exception);
        }

        @Override
        public void fatalError(SAXParseException exception) throws SAXException {
            handleMessage(exception);
        }

        private void handleMessage(SAXParseException e) throws SAXException {
            throw new SAXException("Error occurred on line %s: %s".formatted(e.getLineNumber(), e.getMessage()));
        }
    }
}
