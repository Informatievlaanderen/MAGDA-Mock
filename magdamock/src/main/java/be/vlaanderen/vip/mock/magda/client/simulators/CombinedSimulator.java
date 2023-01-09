package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.exception.MagdaSendFailed;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

public class CombinedSimulator implements ISOAPSimulator {

    @Data
    @AllArgsConstructor
    public class ServiceVersionTuple {
        private final String service;
        private final String version;
    }

    private static Map<ServiceVersionTuple, ISOAPSimulator> simulators;

    public CombinedSimulator() {
        this.simulators = new HashMap<>();
    }

    public void register(String service, String version, ISOAPSimulator simulator) {
        simulators.put(new ServiceVersionTuple(service, version), simulator);
    }

    @Override
    public MagdaDocument send(MagdaDocument request) throws MagdaSendFailed {
        var service = request.getTargetService();
        var version = request.getTargetVersion();
        var tuple = new ServiceVersionTuple(service, version);

        if(simulators.containsKey(tuple)) {
            return simulators.get(tuple).send(request);
        } else {
            throw new MagdaSendFailed("Er is geen magda simulator geregistreerd voor " + service + "/" + version + "");
        }
    }
}
