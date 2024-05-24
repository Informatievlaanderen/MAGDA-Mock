package be.vlaanderen.vip.magda.client.domain.giveperson;

import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefPersoonRequest;

public class MagdaClientGivePersonService implements GivePersonService {

    private final MagdaClient service;

    public MagdaClientGivePersonService(
            MagdaClient service) {
        this.service = service;
    }

    @Override
    public Person getPerson(GeefPersoonRequest request) throws MagdaClientException {
        return new MagdaResponsePerson(service.send(request));
    }
}
