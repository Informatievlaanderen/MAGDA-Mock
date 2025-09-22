package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpoints;
import be.vlaanderen.vip.magda.client.security.TwoWaySslException;
import be.vlaanderen.vip.magda.client.security.TwoWaySslProperties;
import brave.Tracing;
import brave.httpclient5.HttpClient5TracingBuilder;

/**
 * @deprecated use {@link MagdaSoapConnectionBuilder}
 */
@Deprecated
public class MagdaBraveTracingSoapConnection extends MagdaSoapConnection {
    public MagdaBraveTracingSoapConnection(MagdaEndpoints magdaEndpoints, TwoWaySslProperties config, Tracing tracing) throws TwoWaySslException {
        super(magdaEndpoints,
                config,
                httpBuilder -> HttpClient5TracingBuilder.newBuilder(tracing).addTracer(httpBuilder)
        );
    }
}
