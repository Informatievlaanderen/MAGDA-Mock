package be.vlaanderen.vip.magda.client.domain.giveworkrelations;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.SneakyThrows;

import java.util.NoSuchElementException;
import java.util.Optional;

public class MagdaResponseWorkRelationsAdapterJaxbImpl implements MagdaResponseWorkRelationsAdapter {
    JAXBContext context;

    @SneakyThrows
    public MagdaResponseWorkRelationsAdapterJaxbImpl() {
        context = JAXBContext.newInstance(WorkRelationsJaxb.class);
    }

    @Override
    public WorkRelations adapt(MagdaResponseWrapper wrapper) throws MagdaClientException {
        try {
            var contentNode = Optional.ofNullable(wrapper
                    .getResponse()
                    .getDocument()
                    .xpath("//Inhoud")
                    .item(0));
            return (WorkRelations) context.createUnmarshaller()
                    .unmarshal(contentNode.orElseThrow());
        } catch (NoSuchElementException | JAXBException e) {
            throw new MagdaClientException("Could not parse magda response", e);
        }
    }
}
