package be.vlaanderen.vip.magda.client.rest;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MagdaRestRequestBuilderTest {
    @Test
    public void testWhenAllNecessaryFieldAreFilledIn_buildsCorrectRequest() {
        MagdaRestRequest.MagdaRestRequestBuilder builder = MagdaRestRequest.builder();
        String senderId = "sender-id";
        builder.senderId(senderId);
        String senderQualityCode = "sender-quality-code";
        builder.senderQualityCode(senderQualityCode);
        String correlationId = "correlation-id";
        builder.correlationId(correlationId);
        String enduserId = "enduser-id";
        builder.enduserId(enduserId);
        MagdaRestRequest request = builder.build();
        Map<String, String> headers = request.getHeaders();
        assertEquals(senderId, headers.get("X-Magda-Sender-ID"));
        assertEquals(senderQualityCode, headers.get("X-Magda-Sender-QualityCode"));
        assertEquals(correlationId, headers.get("X-Magda-Correlation-ID"));
        assertEquals(enduserId, headers.get("X-Magda-Enduser-ID"));
    }

    @Test
    public void testWhenSenderIdIsMissing_throwsIllegalArgumentException() {
        MagdaRestRequest.MagdaRestRequestBuilder builder = MagdaRestRequest.builder();
        String senderQualityCode = "sender-quality-code";
        builder.senderQualityCode(senderQualityCode);
        String correlationId = "correlation-id";
        builder.correlationId(correlationId);
        String enduserId = "enduser-id";
        builder.enduserId(enduserId);
        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void testWhenSenderQualityCodeIsMissing_throwsIllegalArgumentException() {
        MagdaRestRequest.MagdaRestRequestBuilder builder = MagdaRestRequest.builder();
        String senderQualityCode = "sender-quality-code";
        builder.senderQualityCode(senderQualityCode);
        String correlationId = "correlation-id";
        builder.correlationId(correlationId);
        String enduserId = "enduser-id";
        builder.enduserId(enduserId);
        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void testWhenCorrelationIdIsMissing_throwsIllegalArgumentException() {
        MagdaRestRequest.MagdaRestRequestBuilder builder = MagdaRestRequest.builder();
        String senderId = "sender-id";
        builder.senderId(senderId);
        String senderQualityCode = "sender-quality-code";
        builder.senderQualityCode(senderQualityCode);
        String enduserId = "enduser-id";
        builder.enduserId(enduserId);
        assertThrows(IllegalArgumentException.class, builder::build);
    }

    @Test
    public void testWhenEnduserIdIsMissing_throwsIllegalArgumentException() {
        MagdaRestRequest.MagdaRestRequestBuilder builder = MagdaRestRequest.builder();
        String senderId = "sender-id";
        builder.senderId(senderId);
        String senderQualityCode = "sender-quality-code";
        builder.senderQualityCode(senderQualityCode);
        String correlationId = "correlation-id";
        builder.correlationId(correlationId);
        assertThrows(IllegalArgumentException.class, builder::build);
    }
}
