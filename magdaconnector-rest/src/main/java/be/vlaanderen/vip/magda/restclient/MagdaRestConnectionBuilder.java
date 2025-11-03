package be.vlaanderen.vip.magda.restclient;

import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpoints;
import be.vlaanderen.vip.magda.client.security.TwoWaySslException;
import be.vlaanderen.vip.magda.client.security.TwoWaySslProperties;
import brave.Tracing;
import brave.httpclient5.HttpClient5TracingBuilder;
import io.micrometer.core.instrument.binder.httpcomponents.hc5.ObservationExecChainHandler;
import io.micrometer.observation.ObservationRegistry;
import jakarta.annotation.Nullable;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.httpclient5.LogbookHttpExecHandler;

public class MagdaRestConnectionBuilder {

    private MagdaEndpoints endpoints;
    @Nullable private TwoWaySslProperties sslConfig;
    @Nullable private Tracing tracing;
    @Nullable private ObservationRegistry observationRegistry;
    @Nullable private Logbook logbook;

    public MagdaRestConnectionBuilder withEndpoints(MagdaEndpoints endpoints) {
        this.endpoints = endpoints;
        return this;
    }

    public MagdaRestConnectionBuilder withSslConfig(@Nullable TwoWaySslProperties sslConfig) {
        this.sslConfig = sslConfig;
        return this;
    }

    public MagdaRestConnectionBuilder withTracing(@Nullable Tracing tracing) {
        this.tracing = tracing;
        return this;
    }

    public MagdaRestConnectionBuilder withObservationRegistry(@Nullable ObservationRegistry observationRegistry) {
        this.observationRegistry = observationRegistry;
        return this;
    }

    public MagdaRestConnectionBuilder withLogbook(@Nullable Logbook logbook) {
        this.logbook = logbook;
        return this;
    }

    public MagdaRestConnection build() throws TwoWaySslException {
        if(endpoints == null) { throw new IllegalArgumentException("endpoints must be supplied"); }

        return new MagdaRestConnection(endpoints, sslConfig, httpClientBuilder -> {
            if(tracing != null) {
                HttpClient5TracingBuilder.newBuilder(tracing).addTracer(httpClientBuilder);
            }

            if(observationRegistry != null) {
                httpClientBuilder.addExecInterceptorLast("micrometer", new ObservationExecChainHandler(observationRegistry));
            }

            if(logbook != null) {
                httpClientBuilder.addExecInterceptorFirst("Logbook", new LogbookHttpExecHandler(logbook));
            }

            return httpClientBuilder;
        });
    }
}