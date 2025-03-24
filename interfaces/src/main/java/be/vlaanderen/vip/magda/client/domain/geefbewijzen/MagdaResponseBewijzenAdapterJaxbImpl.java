package be.vlaanderen.vip.magda.client.domain.geefbewijzen;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.SneakyThrows;

import java.util.Optional;

public class MagdaResponseBewijzenAdapterJaxbImpl implements MagdaResponseBewijzenAdapter {

    private static MagdaResponseBewijzenAdapterJaxbImpl instance;

    public static MagdaResponseBewijzenAdapterJaxbImpl getInstance() {
        if(instance == null) {
            instance = new MagdaResponseBewijzenAdapterJaxbImpl();
        }

        return instance;
    }

    private final JAXBContext context;

    @SneakyThrows
    private MagdaResponseBewijzenAdapterJaxbImpl() {
        context = JAXBContext.newInstance(BewijzenJaxb.class);
    }

    @Override
    public Optional<Bewijzen> adapt(MagdaResponseWrapper wrapper) throws MagdaClientException {
        try {
            var node = Optional.ofNullable(wrapper
                    .getResponse()
                    .getDocument()
                    .xpath("//Inhoud")
                    .item(0));
            if(node.isPresent()) {
                return Optional.of((Bewijzen) context.createUnmarshaller().unmarshal(node.get()));
            } else {
                return Optional.empty();
            }
        } catch (JAXBException e) {
            throw new MagdaClientException("Could not parse magda response", e);
        }
    }
}
