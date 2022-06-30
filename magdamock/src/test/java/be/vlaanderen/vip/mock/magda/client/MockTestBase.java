package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.MagdaConnectorImpl;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheid;
import be.vlaanderen.vip.mock.magda.client.endpoints.MagdaEndpointsMock;
import be.vlaanderen.vip.mock.magda.client.legallogging.AfnemerLogServiceMock;

public abstract class MockTestBase {
    protected MagdaConnectorImpl makeMagdaConnector(AfnemerLogServiceMock afnemerLogService) {
        var connection = new MagdaMockConnection();
        MagdaEndpointsMock magdaEndpoints = new MagdaEndpointsMock();
        MagdaHoedanigheid mockedMagdaHoedanigheid = new MagdaHoedanigheid("Magda Mock", "magdamock.service", "123");
        MagdaHoedanigheidServiceMock magdaHoedanigheidService = new MagdaHoedanigheidServiceMock(mockedMagdaHoedanigheid);
        var connector = new MagdaConnectorImpl(connection, afnemerLogService, magdaEndpoints, magdaHoedanigheidService);
        return connector;
    }
}
