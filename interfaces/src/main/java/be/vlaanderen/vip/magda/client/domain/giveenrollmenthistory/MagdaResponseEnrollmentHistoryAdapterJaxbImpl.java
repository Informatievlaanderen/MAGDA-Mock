package be.vlaanderen.vip.magda.client.domain.giveenrollmenthistory;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import lombok.SneakyThrows;

import java.util.NoSuchElementException;
import java.util.Optional;

public class MagdaResponseEnrollmentHistoryAdapterJaxbImpl implements MagdaResponseEnrollmentHistoryAdapter {

    private static MagdaResponseEnrollmentHistoryAdapterJaxbImpl instance;

    public static MagdaResponseEnrollmentHistoryAdapterJaxbImpl getInstance() {
        if(instance == null) {
            instance = new MagdaResponseEnrollmentHistoryAdapterJaxbImpl();
        }

        return instance;
    }

    private final JAXBContext context;

    @SneakyThrows
    private MagdaResponseEnrollmentHistoryAdapterJaxbImpl() {
        context = JAXBContext.newInstance(EnrollmentHistoryJaxb.class);
    }

    @Override
    public EnrollmentHistory adapt(MagdaResponseWrapper wrapper) throws MagdaClientException {
        try {
            var node = Optional.ofNullable(wrapper
                    .getResponse()
                    .getDocument()
                    .xpath("//Inhoud")
                    .item(0));
            return (EnrollmentHistory) context.createUnmarshaller()
                    .unmarshal(node.orElseThrow());
        } catch (NoSuchElementException | JAXBException e) {
            throw new MagdaClientException("Could not parse magda response", e);
        }
    }
}
