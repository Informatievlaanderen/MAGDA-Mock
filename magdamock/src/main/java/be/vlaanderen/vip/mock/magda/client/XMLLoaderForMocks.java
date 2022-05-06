package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.loader.XMLLoader;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@Slf4j

public class XMLLoaderForMocks<T> extends XMLLoader<T> {
    public XMLLoaderForMocks(Class<T> tClass) {
        super(tClass);
    }

    public T vraagXmlOp(String magdaService, String versie, String identificatieNummer, String url) {
        T xmlInhoud = null;
        if (StringUtils.isNotEmpty(identificatieNummer)) {
            String testResource = "/magda_simulator/" + magdaService + "/" + versie + "/" + identificatieNummer + ".xml";
            try {
                xmlInhoud = parseResource(testResource);
            } catch (IOException | JAXBException | ParserConfigurationException | SAXException e) {
                log.warn("XMLLoader kan '{}}' niet lezen en parsen", testResource, e);
            }
        }
        return xmlInhoud;
    }
}
