package be.vlaanderen.vip.mock.starter.connector;

import org.springframework.util.Assert;

import be.vlaanderen.vip.magda.client.MagdaConnector;
import be.vlaanderen.vip.magda.client.MagdaConnectorImpl;
import be.vlaanderen.vip.magda.client.MagdaSoapConnection;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheidServiceImpl;
import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpoints;
import be.vlaanderen.vip.magda.client.security.TwoWaySslException;
import be.vlaanderen.vip.magda.config.MagdaConfigDto;
import be.vlaanderen.vip.magda.legallogging.service.ClientLogService;
import brave.Tracing;

public class RemoteMagdaConnectionBuilder {
    private ClientLogService logService;
    private MagdaConfigDto magdaConfig;
    private MagdaEndpoints endpoints;
    private Tracing tracing;
    
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
    
    public MagdaConnector build() throws TwoWaySslException {
        Assert.notNull(magdaConfig, "magdaConfig must be supplied");
        Assert.notNull(endpoints, "endpoints must be supplied");
        
        if(logService == null) {
            logService = new DebugLogService();
        }
        
        var connection = createConnection();
        var hoedaingheidService = new MagdaHoedanigheidServiceImpl(magdaConfig);
        
        return new MagdaConnectorImpl(connection, logService, hoedaingheidService);
    }
    
    private MagdaSoapConnection createConnection() throws TwoWaySslException {
        if(tracing != null) {
            return new MagdaSoapConnection(endpoints, magdaConfig, tracing);
        }
        return new MagdaSoapConnection(endpoints, magdaConfig);
    }

}
