package be.vlaanderen.vip.magda.client.domain.givedisabilitydossier;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import be.vlaanderen.vip.magda.client.domain.model.disability.DisabilityDossierJaxb;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.SneakyThrows;

import java.util.NoSuchElementException;
import java.util.Optional;

public class MagdaResponseDisabilityDossierAdapterJaxbImpl implements MagdaResponseDisabilityDossierAdapter {
    JAXBContext context;

    @SneakyThrows
    public MagdaResponseDisabilityDossierAdapterJaxbImpl() {
        context = JAXBContext.newInstance(DisabilityDossierJaxb.class);
    }

    @Override
    public DisabilityDossier adapt(MagdaResponseWrapper wrapper) throws MagdaClientException {
        try {
            var contentNode = Optional.ofNullable(wrapper
                    .getResponse()
                    .getDocument()
                    .xpath("//Inhoud")
                    .item(0));

            if (contentNode.isEmpty()) {
                return null;
            }
            return (DisabilityDossier) context.createUnmarshaller()
                    .unmarshal(contentNode.orElseThrow());
        } catch (NoSuchElementException | JAXBException e) {
            throw new MagdaClientException("Could not parse magda response", e);
        }
    }
}
