package be.vlaanderen.vip.magda.loader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class XMLLoader<T> {
    private static final Map<Class<?>, JAXBContext> contexts = new ConcurrentHashMap<>();
    private final Class<T> tClass;
    private JAXBContext jaxbContext;

    public XMLLoader(Class<T> tClass) {
        this.tClass = tClass;
    }

    private JAXBContext getJaxbContext() throws JAXBException {
        if (jaxbContext == null) {
            jaxbContext = contexts.get(this.tClass);
            if (jaxbContext == null) {
                jaxbContext = JAXBContext.newInstance(this.tClass);
                contexts.put(this.tClass, jaxbContext);
            }
        }
        return jaxbContext;
    }

    public T parseNode(Node item) throws JAXBException {
        T antwoord = null;
        final JAXBContext context = getJaxbContext();
        if (context != null) {
            antwoord = unmarshal(context, item);
        }
        return antwoord;
    }

    protected T parseResource(String testResource) throws IOException, JAXBException, ParserConfigurationException, SAXException {
        T xmlInhoud = null;
        try (InputStream resource = this.getClass().getResourceAsStream(testResource)) {
            if (Objects.nonNull(resource)) {
                final JAXBContext context = getJaxbContext();
                if (context != null) {
                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    dbf.setExpandEntityReferences(false);
                    dbf.setIgnoringElementContentWhitespace(true);
                    dbf.setValidating(false);
                    dbf.setNamespaceAware(true);
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    Document document = db.parse(resource);

                    xmlInhoud = unmarshal(context, document);
                }
            }
        }
        return xmlInhoud;
    }
    
    @SuppressWarnings("unchecked")
    private T unmarshal(JAXBContext context, Node item) throws JAXBException {
        var jaxbUnmarshaller = context.createUnmarshaller();
        return (T) jaxbUnmarshaller.unmarshal(item);
	}
}

