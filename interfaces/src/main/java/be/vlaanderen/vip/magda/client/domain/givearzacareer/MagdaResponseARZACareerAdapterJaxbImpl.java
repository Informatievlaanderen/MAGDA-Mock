package be.vlaanderen.vip.magda.client.domain.givearzacareer;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.SneakyThrows;

import java.util.NoSuchElementException;
import java.util.Optional;

public class MagdaResponseARZACareerAdapterJaxbImpl implements MagdaResponseARZACareerAdapter{

    JAXBContext context;

    @SneakyThrows
    public MagdaResponseARZACareerAdapterJaxbImpl() {
        context = JAXBContext.newInstance(ARZACareerJaxb.class);
    }

    @Override
    public ARZACareer adapt(MagdaResponseWrapper wrapper) throws MagdaClientException {
        try {
            var selfEmployedNode = Optional.ofNullable(wrapper
                    .getResponse()
                    .getDocument()
                    .xpath("//Zelfstandige")
                    .item(0));
            return (ARZACareer) context.createUnmarshaller()
                    .unmarshal(selfEmployedNode.orElseThrow());
        } catch (NoSuchElementException | JAXBException e) {
            throw new MagdaClientException("Could not parse magda response", e);
        }    }
}
