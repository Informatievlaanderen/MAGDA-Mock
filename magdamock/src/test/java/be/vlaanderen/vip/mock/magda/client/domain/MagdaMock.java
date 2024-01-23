package be.vlaanderen.vip.mock.magda.client.domain;

import be.vlaanderen.vip.magda.client.MagdaConnector;
import be.vlaanderen.vip.magda.client.MagdaConnectorImpl;
import be.vlaanderen.vip.magda.client.MagdaRequest;
import be.vlaanderen.vip.magda.client.MagdaResponse;
import be.vlaanderen.vip.magda.client.diensten.GeefAanslagbiljetPersonenbelastingRequest;
import be.vlaanderen.vip.magda.client.diensten.GeefPersoonRequest;
import be.vlaanderen.vip.magda.client.diensten.GeefSociaalStatuutRequest;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import be.vlaanderen.vip.mock.magda.client.MagdaHoedanigheidServiceMock;
import be.vlaanderen.vip.mock.magda.client.MagdaMockConnection;
import be.vlaanderen.vip.mock.magda.client.legallogging.ClientLogServiceMock;

import java.time.OffsetDateTime;
import java.time.Year;

public class MagdaMock {
    private static MagdaMock mock;
    
    public static MagdaMock getInstance() {
        if(mock == null) {
            mock = new MagdaMock();
        }
        return mock;
    }
    
    private MagdaConnector connector;
    
    public MagdaMock() {
        try {
            connector = createMockConnector();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private static MagdaConnector createMockConnector() throws Exception {
        var logService = new ClientLogServiceMock();
        var connection = MagdaMockConnection.create();
        var registrationInfo = MagdaRegistrationInfo.builder()
                                                    .identification("advies.services")
                                                    .build();
        var hoedaingheidService = new MagdaHoedanigheidServiceMock(registrationInfo);
        
        return new MagdaConnectorImpl(connection, logService, hoedaingheidService);
    }
    
    public MagdaResponse send(MagdaRequest request) {
        return connector.send(request);
    }
    
    public MagdaResponse getSocialStatute(String insz, String socialStatute) {
        return send(GeefSociaalStatuutRequest.builder()
                                             .insz(insz)
                                             .sociaalStatuut(socialStatute)
                                             .datum(OffsetDateTime.now())
                                             .build());
    }
    
    public MagdaResponse getPerson(String insz) {
        return send(GeefPersoonRequest.builder()
                                      .insz(insz)
                                      .build());
    }
    
    public MagdaResponse getAssesmentNoticePersonTax(String insz) {
        return send(GeefAanslagbiljetPersonenbelastingRequest.builder()
                                                             .insz(insz)
                                                             .build());
    }

    public MagdaResponse getTaxRecords(String insz, Year incomeYear) {
        return send(GeefAanslagbiljetPersonenbelastingRequest.builder2()
                .insz(insz)
                .incomeYear(incomeYear)
                .build());
    }
}
