package be.vlaanderen.vip.magda.client.rest;

import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.hc.core5.http.Method;

import java.util.HashMap;
import java.util.Map;

@Builder(toBuilder = true, buildMethodName = "internalBuild")
@Getter
@ToString
@EqualsAndHashCode
public class MagdaRestRequest {
    private MagdaServiceIdentification dienst;
    private Method method;
    private Map<String, String> urlQueryParams;
    private Map<String, String> headers;
    @Setter
    private String senderId;
    @Setter
    private String senderQualityCode;
    private String correlationId;
    private String enduserId;

    public static class MagdaRestRequestBuilder {
        public MagdaRestRequest build() {
            if (this.headers == null) {
                this.headers = new HashMap<>();
            }
            if (senderId == null) {
                throw new IllegalArgumentException("Sender ID is required");
            }
            this.headers.put("X-Magda-Sender-ID", senderId);
            if (senderQualityCode == null) {
                throw new IllegalArgumentException("Sender Quality Code is required");
            }
            this.headers.put("X-Magda-Sender-QualityCode", senderQualityCode);
            if (correlationId == null) {
                throw new IllegalArgumentException("Correlation ID is required");
            }
            this.headers.put("X-Magda-Correlation-ID", correlationId);
            if (enduserId == null) {
                throw new IllegalArgumentException("Enduser ID is required");
            }
            this.headers.put("X-Magda-Enduser-ID", enduserId);
            return this.internalBuild();
        }
    }
}
