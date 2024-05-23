package be.vlaanderen.vip.magda.client.domain.givesocialstatute;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import lombok.SneakyThrows;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;

import java.util.NoSuchElementException;
import java.util.Optional;

public class MagdaResponseSocialStatutesAdapterJaxbImpl implements MagdaResponseSocialStatutesAdapter {

    JAXBContext context;

    @SneakyThrows
    public MagdaResponseSocialStatutesAdapterJaxbImpl() {
        context = JAXBContext.newInstance(SocialStatutesJaxb.class);
    }


    @Override
    public SocialStatutes adapt(MagdaResponseWrapper wrapper) throws MagdaClientException {
        try {
            var node = Optional.ofNullable(wrapper
                    .getResponse()
                    .getDocument()
                    .xpath("//Inhoud/Persoon")
                    .item(0));
            return (SocialStatutes) context.createUnmarshaller()
                    .unmarshal(node.orElseThrow());
        } catch (NoSuchElementException | JAXBException e) {
            throw new MagdaClientException("Could not parse magda response", e);
        }
    }

}
