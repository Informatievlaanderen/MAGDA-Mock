package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.ConnectorMagdaClient;
import be.vlaanderen.vip.magda.client.MagdaConnectorImpl;
import be.vlaanderen.vip.magda.client.diensten.MobilityRegistrationRequest;
import be.vlaanderen.vip.magda.client.domain.mobility.MobilityRegistrationJsonAdapter;
import be.vlaanderen.vip.magda.client.domain.mobility.RestMobilityRegistrationService;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import be.vlaanderen.vip.magda.client.rest.MagdaResponseJson;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class MobilityRegistrationTest {
    @Test
    @SneakyThrows
    void getMobilityRegistration_givesResponse() {
        var request = MobilityRegistrationRequest.builder()
                .plateNr("1ABC123")
                .registrationInfo(
                        MagdaRegistrationInfo.builder().identification("id").hoedanigheidscode("hc").build()
                )
                .enduserId("00000000097")
                .httpDateHeader("Tue, 29 Oct 2024 16:56:32 GMT")
                .build();

        var connection = MagdaMockRestConnection.create();
        var connector = new MagdaConnectorImpl(connection, null, null);
        var client = new ConnectorMagdaClient(connector);
        RestMobilityRegistrationService restMobilityRegistrationService = new RestMobilityRegistrationService(client, new MobilityRegistrationJsonAdapter());
        var antwoord = restMobilityRegistrationService.getRegistrations(request);
        Assertions.assertNotNull(antwoord);
        assertEquals("71640618918", antwoord.getFirst().getTitular().getPerson().getNationalNr().getIdentificator());
    }

    @Test
    @SneakyThrows
    void rawRestCall_givesResponse() {
        var mockConnection = MagdaMockRestConnection.create();
        var response = mockConnection.sendRestRequest("/v1/mobility/registrations", "plateNr=1ABC123", "GET", "", "Tue, 29 Oct 2024 16:56:32 GMT");
        MobilityRegistrationJsonAdapter mobilityRegistrationJsonAdapter = new MobilityRegistrationJsonAdapter();
        var antwoord = mobilityRegistrationJsonAdapter.adapt(new MagdaResponseJson(response.getLeft(), response.getRight()));
        Assertions.assertNotNull(antwoord);
        assertEquals("71640618918", antwoord.getFirst().getTitular().getPerson().getNationalNr().getIdentificator());
    }

    @Test
    @SneakyThrows
    void unknownLicensePlate_givesEmptyListResponse() {
        var request = MobilityRegistrationRequest.builder()
                .plateNr("UNKNOWN")
                .registrationInfo(
                        MagdaRegistrationInfo.builder().identification("id").hoedanigheidscode("hc").build()
                )
                .enduserId("00000000097")
                .build();

        var connection = MagdaMockRestConnection.create();
        var connector = new MagdaConnectorImpl(connection, null, null);
        var client = new ConnectorMagdaClient(connector);
        RestMobilityRegistrationService restMobilityRegistrationService = new RestMobilityRegistrationService(client, new MobilityRegistrationJsonAdapter());
        var antwoord = restMobilityRegistrationService.getRegistrations(request);
        assertTrue(antwoord.isEmpty());
    }
}
