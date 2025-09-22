package be.vlaanderen.vip.mock.starter.connector;

import be.vlaanderen.vip.magda.client.*;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheidServiceImpl;
import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpoints;
import be.vlaanderen.vip.magda.client.security.TwoWaySslException;
import be.vlaanderen.vip.magda.config.MagdaConfigDto;
import be.vlaanderen.vip.magda.legallogging.service.ClientLogService;
import brave.Tracing;
import io.micrometer.observation.ObservationRegistry;
import org.springframework.util.Assert;
import org.zalando.logbook.Logbook;

public class RemoteMagdaConnectionBuilder {
    private ClientLogService logService;
    private MagdaConfigDto magdaConfig;
    private MagdaEndpoints endpoints;
    private Tracing tracing;
    private ObservationRegistry observationRegistry;
    private Logbook logbook;
    
    RemoteMagdaConnectionBuilder() {}
    
    public RemoteMagdaConnectionBuilder clientLogService(ClientLogService logService) {
        this.logService = logService;
        return this;
    }
    
    public RemoteMagdaConnectionBuilder magdaConfig(MagdaConfigDto magdaConfig) {
        this.magdaConfig = magdaConfig;
        return this;
    }
    
    public RemoteMagdaConnectionBuilder magdaEndpoints(MagdaEndpoints endpoints) {
        this.endpoints = endpoints;
        return this;
    }
    
    public RemoteMagdaConnectionBuilder tracing(Tracing tracing) {
        this.tracing = tracing;
        return this;
    }

    public RemoteMagdaConnectionBuilder micrometer(ObservationRegistry observationRegistry) {
        this.observationRegistry = observationRegistry;
        return this;
    }

    public RemoteMagdaConnectionBuilder logbook(Logbook logbook) {
        this.logbook = logbook;
        return this;
    }
    
    public MagdaConnector build() throws TwoWaySslException {
        Assert.notNull(magdaConfig, "magdaConfig must be supplied");
        Assert.notNull(endpoints, "endpoints must be supplied");
        
        if(logService == null) {
            logService = new DebugLogService();
        }

        var connection = new MagdaSignedConnection(createSoapConnection(), magdaConfig);
        var hoedaingheidService = new MagdaHoedanigheidServiceImpl(magdaConfig);
        
        return new MagdaConnectorImpl(connection, logService, hoedaingheidService);
    }
    
    private MagdaSoapConnection createSoapConnection() throws TwoWaySslException {
        var builder = new MagdaSoapConnectionBuilder()
                .withEndpoints(endpoints)
                .withSslConfig(magdaConfig.getKeystore())
                .withLogbook(logbook);

        if(tracing != null) {
            builder.withTracing(tracing);
        } else if(observationRegistry != null) {
            builder.withObservationRegistry(observationRegistry);
        }

        return builder.build();
    }
}