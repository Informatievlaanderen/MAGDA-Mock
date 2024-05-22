package be.vlaanderen.vip.magda.client.domain.giveenterprise;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import be.vlaanderen.vip.magda.client.domain.model.enterprise.EnterpriseJaxb;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.SneakyThrows;

import java.util.NoSuchElementException;
import java.util.Optional;

public class MagdaResponseEnterpriseAdapterJaxbImpl implements MagdaResponseEnterpriseAdapter {

    JAXBContext context;

    @SneakyThrows
    public MagdaResponseEnterpriseAdapterJaxbImpl() {
        context = JAXBContext.newInstance(EnterpriseJaxb.class);
    }

    @Override
    public Enterprise adapt(MagdaResponseWrapper wrapper) throws MagdaClientException {
        try {
            var selfEmployedNode = Optional.ofNullable(wrapper
                    .getResponse()
                    .getDocument()
                    .xpath("//Onderneming")
                    .item(0));
            return (Enterprise) context.createUnmarshaller()
                    .unmarshal(selfEmployedNode.orElseThrow());
        } catch (NoSuchElementException | JAXBException e) {
            throw new MagdaClientException("Could not parse magda response", e);
        }
    }
}
