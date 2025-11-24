package be.vlaanderen.vip.mock.starter.connector;

import be.vlaanderen.vip.magda.client.MagdaConnector;
import be.vlaanderen.vip.magda.client.MagdaConnectorImpl;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import be.vlaanderen.vip.magda.legallogging.service.ClientLogService;
import be.vlaanderen.vip.mock.magda.client.MagdaHoedanigheidServiceMock;
import be.vlaanderen.vip.mock.magda.client.MagdaMockConnection;
import be.vlaanderen.vip.mock.magda.client.MagdaMockRestConnection;

import java.io.IOException;
import java.net.URISyntaxException;

public class MockMagdaRestConnectorBuilder {
    private ClientLogService logService;

    MockMagdaRestConnectorBuilder() { }
    
    public MockMagdaRestConnectorBuilder clientLogService(ClientLogService logService) {
        this.logService = logService;
        return this;
    }
    
    public MagdaConnector build() throws URISyntaxException, IOException {
        if(logService == null) {
            logService = new DebugLogService();
        }

        var connection = MagdaMockRestConnection.create();
        var registrationInfo = MagdaRegistrationInfo.builder()
                                                    .identification("advies.services")
                                                    .build();
        var hoedaingheidService = new MagdaHoedanigheidServiceMock(registrationInfo);
        
        return new MagdaConnectorImpl(connection, logService, hoedaingheidService);
    }
}
