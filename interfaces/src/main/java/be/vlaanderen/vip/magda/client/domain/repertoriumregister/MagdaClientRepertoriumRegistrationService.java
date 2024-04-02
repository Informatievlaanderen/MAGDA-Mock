package be.vlaanderen.vip.magda.client.domain.repertoriumregister;

import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import be.vlaanderen.vip.magda.client.correlation.CorrelationHeaderProvider;
import be.vlaanderen.vip.magda.client.diensten.RegistreerInschrijvingRequest;
import be.vlaanderen.vip.magda.client.domain.dto.INSZ;
import be.vlaanderen.vip.magda.client.domain.dto.RegisteredINSZ;
import be.vlaanderen.vip.magda.client.xml.node.Node;

import java.util.UUID;
import java.util.function.Supplier;

public class MagdaClientRepertoriumRegistrationService implements RepertoriumRegistrationService {

    private final MagdaClient service;
    private final CorrelationHeaderProvider correlationHeaderProvider;
    
    public MagdaClientRepertoriumRegistrationService(
            MagdaClient service,
            CorrelationHeaderProvider correlationHeaderProvider) {
        this.service = service;
        this.correlationHeaderProvider = correlationHeaderProvider;
    }

    @Override
    public RegisteredINSZ register(Supplier<RegistreerInschrijvingRequest> request) throws MagdaClientException {
        RegistreerInschrijvingRequest registreerInschrijvingRequest = request.get();
        correlationHeaderProvider.getXCorrelationId().ifPresent(xCorrelationId -> registreerInschrijvingRequest.setCorrelationId(UUID.fromString(xCorrelationId)));

        var response = service.send(registreerInschrijvingRequest);
        validateResponse(response);

        return new RegisteredINSZ(INSZ.of(registreerInschrijvingRequest.getInsz().getValue()));
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