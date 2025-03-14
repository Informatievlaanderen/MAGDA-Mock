package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpoints;
import be.vlaanderen.vip.magda.client.security.TwoWaySslException;
import be.vlaanderen.vip.magda.client.security.TwoWaySslProperties;
import io.micrometer.core.instrument.binder.httpcomponents.hc5.ObservationExecChainHandler;
import io.micrometer.observation.ObservationRegistry;

public class MagdaMicrometerObservableSoapConnection extends MagdaSoapConnection {
    public MagdaMicrometerObservableSoapConnection(MagdaEndpoints magdaEndpoints, TwoWaySslProperties config, ObservationRegistry observationRegistry) throws TwoWaySslException {
        super(magdaEndpoints,
                config,
                httpClientBuilder -> httpClientBuilder.addExecInterceptorLast("micrometer", new ObservationExecChainHandler(observationRegistry))
        );
    }
}
