package be.vlaanderen.vip.magda.client.domain.repertoriumregister;

import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import be.vlaanderen.vip.magda.client.diensten.RegistreerInschrijvingRequest;
import be.vlaanderen.vip.magda.client.domain.dto.INSZ;
import be.vlaanderen.vip.magda.client.domain.dto.RegisteredINSZ;
import be.vlaanderen.vip.magda.client.xml.node.Node;

public class MagdaClientRepertoriumRegistrationService implements RepertoriumRegistrationService {

    private final MagdaClient service;

    public MagdaClientRepertoriumRegistrationService(
            MagdaClient service) {
        this.service = service;
    }

    @Override
    public RegisteredINSZ register(RegistreerInschrijvingRequest request) throws MagdaClientException {

        var response = service.send(request);
        validateResponse(response);

        return new RegisteredINSZ(INSZ.of(request.getInsz().getValue()));
    }
    
    private void validateResponse(MagdaResponseWrapper response) throws MagdaClientException {
        var result = response.getNode("//Antwoord/Inhoud/Resultaat")
                             .flatMap(Node::getValue)
                             .orElse("0");
        if(!result.equals("1")) {
            throw new MagdaClientException("Registration in repertorium failed; expected a result '1', but was '0'.");
        }
    }
}