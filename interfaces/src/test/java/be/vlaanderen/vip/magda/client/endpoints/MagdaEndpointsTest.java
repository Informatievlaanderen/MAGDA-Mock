package be.vlaanderen.vip.magda.client.endpoints;

import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
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

            assertEquals(URI.create("http://foo/endpoint"), magdaEndpoints.magdaUri(new MagdaServiceIdentification("foo", "00.01.00")));
            assertEquals(URI.create("http://bar/endpoint"), magdaEndpoints.magdaUri(new MagdaServiceIdentification("bar", "00.02.00")));
            var identification1 = new MagdaServiceIdentification("baz", "00.01.00");
            assertThrows(IllegalArgumentException.class, () -> magdaEndpoints.magdaUri(identification1));
            var identification2 = new MagdaServiceIdentification("foo", "00.02.00");
            assertThrows(IllegalArgumentException.class, () -> magdaEndpoints.magdaUri(identification2));
        }

        @Test
        void relativeUriIsNotAllowed() {
            assertThrows(IllegalArgumentException.class, () -> MagdaEndpoint.of("/endpoint"));
        }
    }
}
