package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.mock.magda.client.exceptions.MagdaMockException;

import java.util.HashMap;
import java.util.Map;

public class CombinedSimulator implements SOAPSimulator {
    private final Map<MagdaServiceIdentification, SOAPSimulator> simulators = new HashMap<>();

    public void register(String service, String version, SOAPSimulator simulator) {
        simulators.put(new MagdaServiceIdentification(service, version), simulator);
    }

    @Override
    public MagdaDocument send(MagdaDocument request) throws MagdaMockException {
        var serviceIdentification = request.getServiceIdentification();

        if(simulators.containsKey(serviceIdentification)) {
            return simulators.get(serviceIdentification).send(request);
        } else {
            throw new MagdaMockException("No magda simulator is registered for %s/%s".formatted(serviceIdentification.getNaam(), serviceIdentification.getVersie()));
        }
    }
}
