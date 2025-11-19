package be.vlaanderen.vip.magda.tester.tests;

import be.vlaanderen.vip.magda.client.diensten.MobilityRegistrationRequest;
import be.vlaanderen.vip.magda.client.domain.mobility.MobilityRegistrationJsonAdapter;
import be.vlaanderen.vip.magda.client.domain.mobility.Registration;
import be.vlaanderen.vip.magda.client.domain.mobility.RestMobilityRegistrationService;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpoints;
import be.vlaanderen.vip.magda.restclient.MagdaRestClientBuilder;
import be.vlaanderen.vip.magda.restclient.MagdaRestClientImpl;
import be.vlaanderen.vip.magda.tester.config.MockMagdaEndpoints;
import be.vlaanderen.vip.magda.tester.config.MockRestMagdaEndpoints;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
public class MockRestServerTest extends MockServerTest {

    private static final UUID CORRELATION_ID = UUID.fromString("3cd51ce6-c533-11f0-a0f2-04cf4b22694c");
    private MagdaRegistrationInfo registrationInfo;
    private RestMobilityRegistrationService service;

    @BeforeAll
    @SneakyThrows
    void beforeAll() {
        assertServiceAvailable();
    }

    @BeforeEach
    @SneakyThrows
    void setup() {
        var magdaEndpoints = makeMockEndpoints();
        MagdaRestClientImpl client = new MagdaRestClientBuilder().withEndpoints(magdaEndpoints).build();
        registrationInfo = MagdaRegistrationInfo.builder().identification("id").hoedanigheidscode("hc").build();
        service = new RestMobilityRegistrationService(client, new MobilityRegistrationJsonAdapter());
    }

    @Test
    @SneakyThrows
    void callMobilityRegistrations() {
        MobilityRegistrationRequest.MobilityRegistrationRequestBuilder builder = MobilityRegistrationRequest.builder();
        builder.registrationInfo(registrationInfo);
        builder.correlationId(CORRELATION_ID);
        builder.enduserId("00000000097");
        builder.plateNr("1ABC123");
        MobilityRegistrationRequest mobilityRegistrationRequest = builder.build();
        List<Registration> registrationList = service.getRegistrations(mobilityRegistrationRequest);
        assertThat(registrationList).hasSize(1);
        Registration registration = registrationList.getFirst();
        assertEquals("71640618918", registration.getTitular().getPerson().getNationalNr().getIdentificator());
    }

    private MagdaEndpoints makeMockEndpoints() {
        return new MockRestMagdaEndpoints(testerConfig.getServiceUrl().resolve("/api/Magda-v1/rest"));
    }
}
