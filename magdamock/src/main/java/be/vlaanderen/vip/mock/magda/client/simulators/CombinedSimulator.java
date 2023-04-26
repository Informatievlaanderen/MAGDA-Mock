package be.vlaanderen.vip.mock.magda.client.simulators;

import java.util.HashMap;
import java.util.Map;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentificatie;
import be.vlaanderen.vip.magda.exception.MagdaSendFailed;

public class CombinedSimulator implements SOAPSimulator {
    private Map<MagdaServiceIdentificatie, SOAPSimulator> simulators = new HashMap<>();

    public void register(String service, String version, SOAPSimulator simulator) {
        simulators.put(new MagdaServiceIdentificatie(service, version), simulator);
    }

    @Override
    public MagdaDocument send(MagdaDocument request) throws MagdaSendFailed {
        var serviceIdentification = request.getServiceIdentification();

        if(simulators.containsKey(serviceIdentification)) {
            return simulators.get(serviceIdentification).send(request);
        } else {
            throw new MagdaSendFailed("Er is geen magda simulator geregistreerd voor " + serviceIdentification.getNaam() + "/" + serviceIdentification.getVersie());
        }
    }
}
