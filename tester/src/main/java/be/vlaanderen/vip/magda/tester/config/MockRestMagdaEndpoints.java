package be.vlaanderen.vip.magda.tester.config;

import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpoints;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.hc.core5.net.URIBuilder;

import java.net.URI;

@RequiredArgsConstructor
public class MockRestMagdaEndpoints implements MagdaEndpoints {
    private final URI uri;

    @SneakyThrows
    @Override
    public URI magdaUri(MagdaServiceIdentification serviceId) {
        if (serviceId.getName().equals("mobility-registrations")) {
            return new URIBuilder(uri).appendPath("/v1/mobility/registrations").build();
        }
        return uri;
    }
}