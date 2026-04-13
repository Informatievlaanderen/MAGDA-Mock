package be.vlaanderen.vip.magda.client.domain.geefdmfavoorwerknemer;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import be.vlaanderen.vip.magda.client.domain.model.dmfa.DmfaAttestJaxb;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.SneakyThrows;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.NoSuchElementException;
import java.util.Optional;

public class MagdaResponseDmfaVoorWerknemerAdapterJaxbImpl implements MagdaResponseDmfaVoorWerknemerAdapter {
    private final XPath xpath = XPathFactory.newInstance().newXPath();

    JAXBContext context;

    @SneakyThrows
    public MagdaResponseDmfaVoorWerknemerAdapterJaxbImpl() {
        context = JAXBContext.newInstance(DmfaAttestJaxb.class);
    }

    @Override
    public DmfaAttest adapt(MagdaResponseWrapper wrapper) throws MagdaClientException {
        try {
            var document = wrapper
                    .getResponse()
                    .getDocument();
            var contentNode = Optional.ofNullable(document
                    .xpath("//Inhoud")
                    .item(0));

            if(contentNode.isEmpty()) {
                return null;
            }

            var dmfaAttestJaxb = (DmfaAttestJaxb) context.createUnmarshaller()
                    .unmarshal(contentNode.orElseThrow());

            String prefix = "//Uitzonderingen/Uitzondering";
            var uitzonderingNodes = document.xpath(prefix);
            for(var i = 0; i < uitzonderingNodes.getLength(); i++) {
                var uitzonderingNode = uitzonderingNodes.item(i);

                if (keyMatchesValue(uitzonderingNode, prefix, "Type", "INFORMATIE")
                        && keyMatchesValue(uitzonderingNode, prefix, "Oorsprong", "MAGDA")
                        && keyMatchesValue(uitzonderingNode, prefix, "Identificatie", "30040")) {
                    dmfaAttestJaxb.moreInformationAvailable = true;
                    break;
                }
            }

            return dmfaAttestJaxb;
        } catch (NoSuchElementException | JAXBException e) {
            throw new MagdaClientException("Could not parse magda response", e);
        }
    }

    private boolean keyMatchesValue(org.w3c.dom.Node node, String prefix, String key, String value) {
        try {
            return Boolean.TRUE.equals(xpath.evaluate("boolean(%s/%s[. = '%s'])".formatted(prefix, key, value), node, XPathConstants.BOOLEAN));
        } catch(NoSuchElementException | XPathExpressionException ex) {
            throw new IllegalStateException("BUG: Exception on XPath evaluation", ex);
        }
    }
}
