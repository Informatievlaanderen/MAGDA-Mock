package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentificatie;
import be.vlaanderen.vip.magda.exception.MagdaSendFailed;

import java.util.HashMap;
import java.util.Map;

public class CombinedSimulator implements ISOAPSimulator {

    private static Map<MagdaServiceIdentificatie, ISOAPSimulator> simulators;

    public CombinedSimulator() {
        this.simulators = new HashMap<>();
    }

    public void register(String service, String version, ISOAPSimulator simulator) {
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
