package be.vlaanderen.vip.magda.client.integration;

import be.vlaanderen.vip.magda.client.ConnectorMagdaClient;
import be.vlaanderen.vip.magda.client.MagdaConnector;
import be.vlaanderen.vip.magda.client.MagdaConnectorImpl;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.MagdaSoapConnectionBuilder;
import be.vlaanderen.vip.magda.client.TestHelpers;
import be.vlaanderen.vip.magda.client.connection.MagdaConnection;
import be.vlaanderen.vip.magda.client.diensten.MobilityRegistrationRequest;
import be.vlaanderen.vip.magda.client.domain.mobility.MobilityRegistrationJsonAdapter;
import be.vlaanderen.vip.magda.client.domain.mobility.MobilityRegistrationService;
import be.vlaanderen.vip.magda.client.domain.mobility.Registration;
import be.vlaanderen.vip.magda.client.domain.mobility.RestMobilityRegistrationService;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpoint;
import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpoints;
import com.github.tomakehurst.wiremock.WireMockServer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * In this test class we will test the RestMobilityRegistrationService with the MagdaClient implementation for REST.
 */
public class MobilityRegistrationRestIntegrationTest {
    @Test
    @SneakyThrows
    public void testMobilityRegistrationsEndpoint_returnsRegistrationsList() {
        String input = TestHelpers.getResourceAsString(getClass(), "/mobility/registrations/1ABC123.json");
        MagdaServiceIdentification dienst = new MagdaServiceIdentification("REST /v1/mobility/registrations", "00.01");
        WireMockServer wireMockServer = new WireMockServer(0);
        wireMockServer.start();
        wireMockServer.stubFor(get(urlEqualTo("/v1/mobility/registrations?plateNr=1XNN230"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(input)
                ));
        MagdaEndpoints endpoints = MagdaEndpoints.builder()
                .addMapping(dienst.getName(), dienst.getVersion(), MagdaEndpoint.of(wireMockServer.baseUrl() + "/v1/mobility/registrations"))
                .build();
        MagdaConnection magdaConnection = new MagdaSoapConnectionBuilder()
                .withEndpoints(endpoints)
                .build();
        MagdaConnector magdaConnector = new MagdaConnectorImpl(magdaConnection, null, null);
        ConnectorMagdaClient client = new ConnectorMagdaClient(magdaConnector);
        MobilityRegistrationService mobilityRegistrationService = new RestMobilityRegistrationService(client, new MobilityRegistrationJsonAdapter());
        List<Registration> registrations = mobilityRegistrationService.getRegistrations(
                MobilityRegistrationRequest.builder()
                        .plateNr("1XNN230")
                        .registrationInfo(MagdaRegistrationInfo.builder().identification("identificatie").hoedanigheidscode("HC").build())
                        .correlationId(UUID.fromString("afbf89cc-b8bc-11f0-a0f2-04cf4b22694c"))
                        .enduserId("00000000097")
                        .build());
        assertEquals(1, registrations.size());
        Registration registration = registrations.getFirst();
        Registration.Titular titular = registration.getTitular();
        assertNotNull(titular);
        Registration.Titular.Person person = titular.getPerson();
        assertNotNull(person);
        Registration.Titular.Person.NationalNr nationalNr = person.getNationalNr();
        assertNotNull(nationalNr);
        assertEquals("71640618918", nationalNr.getIdentificator());
        wireMockServer.stop();
    }
}
