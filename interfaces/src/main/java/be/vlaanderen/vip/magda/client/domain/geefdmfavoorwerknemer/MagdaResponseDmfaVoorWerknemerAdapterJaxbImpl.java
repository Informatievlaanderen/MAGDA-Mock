package be.vlaanderen.vip.magda.client.domain.geefdmfavoorwerknemer;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import be.vlaanderen.vip.magda.client.domain.model.dmfa.DmfaAttestJaxb;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.SneakyThrows;

import java.util.NoSuchElementException;
import java.util.Optional;

public class MagdaResponseDmfaVoorWerknemerAdapterJaxbImpl implements MagdaResponseDmfaVoorWerknemerAdapter {
    JAXBContext context;

    @SneakyThrows
    public MagdaResponseDmfaVoorWerknemerAdapterJaxbImpl() {
        context = JAXBContext.newInstance(DmfaAttestJaxb.class);
    }

    @Override
    public DmfaAttest adapt(MagdaResponseWrapper wrapper) throws MagdaClientException {
        try {
            var contentNode = Optional.ofNullable(wrapper
                    .getResponse()
                    .getDocument()
                    .xpath("//Inhoud")
                    .item(0));
            return (DmfaAttest) context.createUnmarshaller()
                    .unmarshal(contentNode.orElseThrow());
        } catch (NoSuchElementException | JAXBException e) {
            throw new MagdaClientException("Could not parse magda response", e);
        }
    }
}
