package be.vlaanderen.vip.magda.client.domain.giveenterprisefunctions;

import be.vlaanderen.vip.jobbonusplus.model.enterprise.EnterpriseFunctionsJaxb;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.SneakyThrows;

import java.util.NoSuchElementException;
import java.util.Optional;

public class MagdaResponseEnterpriseFunctionsAdapterJaxbImpl implements MagdaResponseEnterpriseFunctionsAdapter {

    JAXBContext context;

    @SneakyThrows
    public MagdaResponseEnterpriseFunctionsAdapterJaxbImpl() {
        context = JAXBContext.newInstance(EnterpriseFunctionsJaxb.class);
    }


    @Override
    public EnterpriseFunctions adapt(MagdaResponseWrapper wrapper) throws MagdaClientException {
        try {
            var enterpriseFunctionsNode = Optional.ofNullable(wrapper
                    .getResponse()
                    .getDocument()
                    .xpath("//Functies")
                    .item(0));
            return (EnterpriseFunctions) context.createUnmarshaller()
                    .unmarshal(enterpriseFunctionsNode.orElseThrow());
        } catch (NoSuchElementException | JAXBException e) {
            throw new MagdaClientException("Could not parse magda response", e);
        }
    }
}
