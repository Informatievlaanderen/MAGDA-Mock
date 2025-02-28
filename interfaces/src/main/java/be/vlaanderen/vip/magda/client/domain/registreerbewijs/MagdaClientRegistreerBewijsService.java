package be.vlaanderen.vip.magda.client.domain.registreerbewijs;

import be.vlaanderen.vip.magda.client.MagdaClient;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import be.vlaanderen.vip.magda.client.diensten.RegistreerBewijsRequest;
import be.vlaanderen.vip.magda.client.xml.node.Node;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MagdaClientRegistreerBewijsService implements RegistreerBewijsService {

    private final MagdaClient service;

    @Override
    public void registreerBewijs(RegistreerBewijsRequest request) throws MagdaClientException {
        var response = service.send(request);
        validateResponse(response);
    }

    private void validateResponse(MagdaResponseWrapper response) throws MagdaClientException {
        var result = response.getNode("//Antwoord/Inhoud/Melding")
                .flatMap(Node::getValue)
                .orElse("");
        if(!result.equals("OK")) {
            throw new MagdaClientException("Registration in LED failed; expected a Melding 'OK', but was '%s'.".formatted(result));
        }
    }
}
