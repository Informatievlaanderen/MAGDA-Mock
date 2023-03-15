package be.vlaanderen.vip.magda.client.endpoints;

import be.vlaanderen.vip.magda.client.MagdaServiceIdentificatie;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MagdaEndpointsTest {

    @Nested
    class Builder {

        @Test
        void buildsMagdaEndpoints() {
            var magdaEndpoints = MagdaEndpoints.builder()
                    .addMapping("foo", "00.01.00", MagdaEndpoint.of("http://foo/endpoint"))
                    .addMapping("bar", "00.02.00", MagdaEndpoint.of("http://bar/endpoint"))
                    .build();

            assertEquals(URI.create("http://foo/endpoint"), magdaEndpoints.magdaUri(new MagdaServiceIdentificatie("foo", "00.01.00")));
            assertEquals(URI.create("http://bar/endpoint"), magdaEndpoints.magdaUri(new MagdaServiceIdentificatie("bar", "00.02.00")));
            assertThrows(IllegalArgumentException.class, () -> magdaEndpoints.magdaUri(new MagdaServiceIdentificatie("baz", "00.01.00")));
            assertThrows(IllegalArgumentException.class, () -> magdaEndpoints.magdaUri(new MagdaServiceIdentificatie("foo", "00.02.00")));
        }

        @Test
        void relativeUriIsNotAllowed() {
            assertThrows(IllegalArgumentException.class, () -> MagdaEndpoints.builder().addMapping("foo", "00.01.00", MagdaEndpoint.of("/endpoint")));
        }
    }
}
