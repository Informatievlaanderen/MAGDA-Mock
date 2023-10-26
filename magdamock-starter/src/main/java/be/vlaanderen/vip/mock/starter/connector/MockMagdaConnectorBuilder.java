package be.vlaanderen.vip.mock.starter.connector;

import java.io.IOException;
import java.net.URISyntaxException;

import be.vlaanderen.vip.magda.client.MagdaConnector;
import be.vlaanderen.vip.magda.client.MagdaConnectorImpl;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import be.vlaanderen.vip.magda.legallogging.service.ClientLogService;
import be.vlaanderen.vip.mock.magda.client.MagdaHoedanigheidServiceMock;
import be.vlaanderen.vip.mock.magda.client.MagdaMockConnection;

public class MockMagdaConnectorBuilder {
    private ClientLogService logService;

    MockMagdaConnectorBuilder() { }
    
    public MockMagdaConnectorBuilder clientLogService(ClientLogService logService) {
        this.logService = logService;
        return this;
    }
    
    public MagdaConnector build() throws URISyntaxException, IOException {
        if(logService == null) {
            logService = new DebugLogService();
        }

        var connection = MagdaMockConnection.create();
        var registrationInfo = MagdaRegistrationInfo.builder()
                                                    .identification("advies.services")
                                                    .build();
        var hoedaingheidService = new MagdaHoedanigheidServiceMock(registrationInfo);
        
        return new MagdaConnectorImpl(connection, logService, hoedaingheidService);
    }
}
