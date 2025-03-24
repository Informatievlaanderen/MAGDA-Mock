package be.vlaanderen.vip.magda.client.domain.giveenterprise;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import be.vlaanderen.vip.magda.client.domain.model.enterprise.EnterpriseJaxb;
import jakarta.annotation.Nullable;
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
    @Nullable
    public Enterprise adapt(MagdaResponseWrapper wrapper) throws MagdaClientException {
        try {
            var nodes = wrapper
                    .getResponse()
                    .getDocument()
                    .xpath("//Onderneming");

            if(nodes.getLength() == 0) {
                return null;
            }

            var node = Optional.ofNullable(nodes.item(0));

            return (Enterprise) context.createUnmarshaller()
                    .unmarshal(node.orElseThrow());
        } catch (NoSuchElementException | JAXBException e) {
            throw new MagdaClientException("Could not parse magda response", e);
        }
    }
}
