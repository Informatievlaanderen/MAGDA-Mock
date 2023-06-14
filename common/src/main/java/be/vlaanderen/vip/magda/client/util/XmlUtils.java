package be.vlaanderen.vip.magda.client.util;

import javax.xml.XMLConstants;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;

public class XmlUtils {

    private static final TransformerFactory TRANSFORMER_FACTORY = createTransformerFactoryWithoutExternalAccess();

    /**
     * Creates an XML TransformerFactory that is safe against XXE attacks
     */
    private static TransformerFactory createTransformerFactoryWithoutExternalAccess() {
        var transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

        return transformerFactory;
    }

    public static Transformer createTransformer() throws TransformerConfigurationException {
        return TRANSFORMER_FACTORY.newTransformer();
    }

    private XmlUtils() {}
}