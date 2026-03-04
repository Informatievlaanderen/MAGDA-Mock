package be.vlaanderen.vip.magda.client.rest;

import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.Registration;
import lombok.*;
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
    private Registration registration;
    private String correlationId;
    private String enduserId;
    @Setter
    private String bearerToken;

    public static class MagdaRestRequestBuilder {
        public MagdaRestRequest build() {
            if (this.headers == null) {
                this.headers = new HashMap<>();
            }
            if (registration == null) {
                throw new IllegalArgumentException("Registration is required");
            }
            if (correlationId == null) {
                throw new IllegalArgumentException("Correlation ID is required");
            }
            this.headers.put("x-correlation-id", correlationId);
            if (enduserId == null) {
                throw new IllegalArgumentException("Enduser ID is required");
            }
            this.headers.put("X-Magda-Enduser-ID", enduserId);
            if (bearerToken != null) {
                this.headers.put("Authorization", "Bearer " + bearerToken);
            }
            return this.internalBuild();
        }
    }
}
