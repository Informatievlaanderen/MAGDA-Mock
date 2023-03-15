package be.vlaanderen.vip.magda.tester.config;

import be.vlaanderen.vip.magda.client.MagdaServiceIdentificatie;
import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpoints;
import lombok.RequiredArgsConstructor;

import java.net.URI;

@RequiredArgsConstructor
public class MockMagdaEndpoints implements MagdaEndpoints {
    private final URI uri;

    @Override
    public URI magdaUri(MagdaServiceIdentificatie aanvraag) {
        return uri;
    }
}
