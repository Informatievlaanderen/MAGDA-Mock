package be.vlaanderen.vip.magda.client.rest;

import be.vlaanderen.vip.magda.client.KeyRegistration;
import be.vlaanderen.vip.magda.client.Registration;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MagdaRestRequestBuilderTest {
    @Test
    public void testWhenAllNecessaryFieldAreFilledIn_buildsCorrectRequest() {
        MagdaRestRequest.MagdaRestRequestBuilder builder = MagdaRestRequest.builder();
        Registration registration = new KeyRegistration("registration");
        builder.registration(registration);
        String correlationId = "correlation-id";
        builder.correlationId(correlationId);
        String enduserId = "enduser-id";
        builder.enduserId(enduserId);
        String bearerToken = "bearerToken";
        builder.bearerToken(bearerToken);
        MagdaRestRequest request = builder.build();
        Map<String, String> headers = request.getHeaders();
        assertEquals(correlationId, headers.get("X-Magda-Correlation-ID"));
        assertEquals(enduserId, headers.get("X-Magda-Enduser-ID"));
        assertEquals("Bearer bearerToken", headers.get("Authorization"));
    }

    @Test
    public void testWhenRegistrationIsMissing_throwsIllegalArgumentException() {
        MagdaRestRequest.MagdaRestRequestBuilder builder = MagdaRestRequest.builder();
        String correlationId = "correlation-id";
        builder.correlationId(correlationId);
        String enduserId = "enduser-id";
        builder.enduserId(enduserId);
        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void testWhenCorrelationIdIsMissing_throwsIllegalArgumentException() {
        MagdaRestRequest.MagdaRestRequestBuilder builder = MagdaRestRequest.builder();
        Registration registration = new KeyRegistration("registration");
        builder.registration(registration);
        String enduserId = "enduser-id";
        builder.enduserId(enduserId);
        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void testWhenEnduserIdIsMissing_throwsIllegalArgumentException() {
        MagdaRestRequest.MagdaRestRequestBuilder builder = MagdaRestRequest.builder();
        Registration registration = new KeyRegistration("registration");
        builder.registration(registration);
        String correlationId = "correlation-id";
        builder.correlationId(correlationId);
        assertThrows(IllegalArgumentException.class, builder::build);
    }
}
