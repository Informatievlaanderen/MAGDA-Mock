package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.MagdaConnectorImpl;
import be.vlaanderen.vip.magda.client.connection.MagdaConnection;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheidService;
import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpoints;
import be.vlaanderen.vip.magda.legallogging.service.AfnemerLogService;

public class MagdaConnectorMock extends MagdaConnectorImpl {
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
