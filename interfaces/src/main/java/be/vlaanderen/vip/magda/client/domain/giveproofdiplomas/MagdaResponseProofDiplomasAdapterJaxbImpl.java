package be.vlaanderen.vip.magda.client.domain.giveproofdiplomas;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.SneakyThrows;

import java.util.Optional;

public class MagdaResponseProofDiplomasAdapterJaxbImpl implements MagdaResponseProofDiplomasAdapter {

    private static MagdaResponseProofDiplomasAdapterJaxbImpl instance;

    public static MagdaResponseProofDiplomasAdapterJaxbImpl getInstance() {
        if(instance == null) {
            instance = new MagdaResponseProofDiplomasAdapterJaxbImpl();
        }

        return instance;
    }

    private final JAXBContext context;

    @SneakyThrows
    private MagdaResponseProofDiplomasAdapterJaxbImpl() {
        context = JAXBContext.newInstance(ProofDiplomasJaxb.class);
    }

    @Override
    public Optional<ProofDiplomas> adapt(MagdaResponseWrapper wrapper) throws MagdaClientException {
        try {
            var node = Optional.ofNullable(wrapper
                    .getResponse()
                    .getDocument()
                    .xpath("//Inhoud")
                    .item(0));
            if(node.isPresent()) {
                return Optional.of((ProofDiplomas) context.createUnmarshaller().unmarshal(node.get()));
            } else {
                return Optional.empty();
            }
        } catch (JAXBException e) {
            throw new MagdaClientException("Could not parse magda response", e);
        }
    }
}
