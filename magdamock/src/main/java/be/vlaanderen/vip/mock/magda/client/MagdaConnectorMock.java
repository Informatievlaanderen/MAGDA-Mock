package be.vlaanderen.vip.mock.magda.client;


import be.vlaanderen.vip.magda.client.MagdaConnector;
import be.vlaanderen.vip.magda.client.connection.MagdaConnection;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheid;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheidService;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheidServiceMock;
import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpoints;
import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpointsMock;
import be.vlaanderen.vip.magda.legallogging.service.AfnemerLogService;
import be.vlaanderen.vip.magda.legallogging.service.AfnemerLogServiceMock;

public class MagdaConnectorMock implements MagdaConnector {
    public MagdaConnectorMock() {
        super(new MagdaMockConnection(),
                new AfnemerLogServiceMock(),
                new MagdaEndpointsMock(),
                new MagdaHoedanigheidServiceMock(new MagdaHoedanigheid("unit-test", "kb.vlaanderen.be/aiv/burgerloket-wwoom-mock", "1300a")));
    }

    public MagdaConnectorMock(AfnemerLogService afnemerLogService) {
        super(new MagdaMockConnection(),
                afnemerLogService,
                new MagdaEndpointsMock(),
                new MagdaHoedanigheidServiceMock(new MagdaHoedanigheid("unit-test", "kb.vlaanderen.be/aiv/burgerloket-wwoom-mock", "1300a")));
    }

    public MagdaConnectorMock(MagdaConnection magdaConnection,
                              AfnemerLogService afnemerLogService,
                              MagdaEndpoints magdaEndpoints,
                              MagdaHoedanigheidService magdaHoedanigheidService) {
        super(magdaConnection,
                afnemerLogService,
                magdaEndpoints,
                magdaHoedanigheidService);
    }
}
