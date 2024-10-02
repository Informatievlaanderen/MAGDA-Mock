package be.vlaanderen.vip.magda.client.domain.giveannualaccounts;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.SneakyThrows;

import java.util.NoSuchElementException;
import java.util.Optional;

public class MagdaResponseAnnualAccountsAdapterJaxbImpl implements MagdaResponseAnnualAccountsAdapter {

    JAXBContext context;

    @SneakyThrows
    public MagdaResponseAnnualAccountsAdapterJaxbImpl() {
        context = JAXBContext.newInstance(AnnualAccountsJaxb.class);
    }

    @Override
    public AnnualAccounts adapt(MagdaResponseWrapper wrapper) throws MagdaClientException {
        try {
            var node = Optional.ofNullable(wrapper
                    .getResponse()
                    .getDocument()
                    .xpath("//Onderneming") // XXX
                    .item(0));
            return (AnnualAccounts) context.createUnmarshaller()
                    .unmarshal(node.orElseThrow());
        } catch (NoSuchElementException | JAXBException e) {
            throw new MagdaClientException("Could not parse magda response", e);
        }
    }
}
