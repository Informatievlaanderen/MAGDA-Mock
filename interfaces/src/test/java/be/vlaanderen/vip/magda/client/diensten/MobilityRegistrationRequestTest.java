package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.DirectRegistration;
import be.vlaanderen.vip.magda.client.KeyRegistration;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Map;
import java.util.UUID;

import static be.vlaanderen.vip.magda.client.diensten.MobilityRegistrationRequest.DEFAULT_REGISTRATION;
import static org.junit.jupiter.api.Assertions.*;

public class MobilityRegistrationRequestTest {

    private final MagdaRegistrationInfo registrationInfo = MagdaRegistrationInfo.builder().identification("id").hoedanigheidscode("hc").build();

    @Test
    public void basicRequest() {
        MobilityRegistrationRequest.MobilityRegistrationRequestBuilder builder = new MobilityRegistrationRequest.MobilityRegistrationRequestBuilder();
        builder.registrationInfo(registrationInfo);
        UUID correlationId = UUID.randomUUID();
        builder.correlationId(correlationId);
        builder.enduserId("00000000097");
        MobilityRegistrationRequest mobilityRegistrationRequest = builder.build();
        assertEquals(correlationId, mobilityRegistrationRequest.getCorrelationId());
        assertEquals("00000000097", mobilityRegistrationRequest.getEnduserId());
        assertEquals(new DirectRegistration(registrationInfo), mobilityRegistrationRequest.getRegistration());

        Map<String, String> queryParameters = mobilityRegistrationRequest.getQueryParameters();
        assertEquals(0, queryParameters.size());
    }

    @Test
    public void whenRegistrationKeyIsGiven_usesKeyRegistration() {
        MobilityRegistrationRequest.MobilityRegistrationRequestBuilder builder = new MobilityRegistrationRequest.MobilityRegistrationRequestBuilder();
        UUID correlationId = UUID.randomUUID();
        builder.registrationKey("registration-key");
        builder.correlationId(correlationId);
        builder.enduserId("00000000097");
        MobilityRegistrationRequest mobilityRegistrationRequest = builder.build();
        assertEquals(new KeyRegistration("registration-key"), mobilityRegistrationRequest.getRegistration());
    }

    @Test
    public void whenRegistrationIsMissing_usesDefaultRegistration() {
        MobilityRegistrationRequest.MobilityRegistrationRequestBuilder builder = new MobilityRegistrationRequest.MobilityRegistrationRequestBuilder();
        UUID correlationId = UUID.randomUUID();
        builder.correlationId(correlationId);
        builder.enduserId("00000000097");
        MobilityRegistrationRequest mobilityRegistrationRequest = builder.build();
        assertEquals(new KeyRegistration(DEFAULT_REGISTRATION), mobilityRegistrationRequest.getRegistration());
    }

    @Test
    public void whenRegistrationKeyAndInfoAreBothGiven_throwsException() {
        MobilityRegistrationRequest.MobilityRegistrationRequestBuilder builder = new MobilityRegistrationRequest.MobilityRegistrationRequestBuilder();
        UUID correlationId = UUID.randomUUID();
        builder.registrationKey("registration-key");
        builder.registrationInfo(registrationInfo);
        builder.correlationId(correlationId);
        builder.enduserId("00000000097");
        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    public void whenCorrelationIdIsMissing_builderWillGenerateId() {
        MobilityRegistrationRequest.MobilityRegistrationRequestBuilder builder = new MobilityRegistrationRequest.MobilityRegistrationRequestBuilder();
        builder.registrationInfo(registrationInfo);
        builder.enduserId("00000000097");
        MobilityRegistrationRequest mobilityRegistrationRequest = builder.build();
        assertNotNull(mobilityRegistrationRequest.getCorrelationId());
        assertEquals("00000000097", mobilityRegistrationRequest.getEnduserId());
        assertEquals(new DirectRegistration(registrationInfo), mobilityRegistrationRequest.getRegistration());
    }

    @Test
    public void whenUnifierIsGivenButVinIsMissing_shouldThrowException() {
        MobilityRegistrationRequest.MobilityRegistrationRequestBuilder builder = new MobilityRegistrationRequest.MobilityRegistrationRequestBuilder();
        builder.registrationInfo(registrationInfo);
        UUID correlationId = UUID.randomUUID();
        builder.correlationId(correlationId);
        builder.enduserId("00000000097");
        builder.unifier("uni");
        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void fullRequest() {
        MobilityRegistrationRequest.MobilityRegistrationRequestBuilder builder = new MobilityRegistrationRequest.MobilityRegistrationRequestBuilder();
        builder.registrationInfo(registrationInfo);
        UUID correlationId = UUID.randomUUID();
        builder.correlationId(correlationId);
        builder.enduserId("00000000097");

        builder.plateNr("1ABC123");
        builder.plateUID("UID1");
        builder.vin("vin");
        builder.unifier("uni");
        builder.certificateId("ID");
        builder.nationalNr("12345678900");
        builder.companyNr("12345678900");
        OffsetDateTime now = OffsetDateTime.now();
        builder.dateTime(now);
        builder.transactionUID("transactionUID");
        builder.pageSize("3");
        builder.after("yesterday");
        builder.addressEnrichmentOrganization("AA");
        builder.addressEnrichmentPerson(MobilityRegistrationRequest.EnrichmentSource.KSZ);

        MobilityRegistrationRequest request = builder.build();
        Map<String, String> queryParameters = request.getQueryParameters();
        assertEquals(13, queryParameters.size());
        assertEquals("1ABC123", queryParameters.get("plateNr"));
        assertEquals("UID1", queryParameters.get("plateUID"));
        assertEquals("vin", queryParameters.get("vin"));
        assertEquals("uni", queryParameters.get("unifier"));
        assertEquals("ID", queryParameters.get("certificateId"));
        assertEquals("12345678900", queryParameters.get("nationalNr"));
        assertEquals("12345678900", queryParameters.get("companyNr"));
        assertEquals(now.toString(), queryParameters.get("dateTime"));
        assertEquals("transactionUID", queryParameters.get("transactionUID"));
        assertEquals("3", queryParameters.get("pageSize"));
        assertEquals("yesterday", queryParameters.get("after"));
        assertEquals("AA", queryParameters.get("addressEnrichmentOrganization"));
        assertEquals("KSZ", queryParameters.get("addressEnrichmentPerson"));
    }
}
