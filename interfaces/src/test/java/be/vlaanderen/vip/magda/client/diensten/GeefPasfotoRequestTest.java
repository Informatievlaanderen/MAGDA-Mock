package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaRequest;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeefPasfotoRequestTest {

    @Nested
    class Builder {

        @Test
        void buildsRequest() {
            var builder = GeefPasfotoRequest.builder()
                    .insz(INSZNumber.of("123"))
                    .registration("456");
            var request = builder.build();

            assertNull(request.getCorrelationId());
            assertNotNull(request.getRequestId());
            assertEquals(INSZNumber.of("123"), request.getInsz());
            assertEquals("456", request.getRegistration());
        }

        @Test
        void registration_HasDefaultValue() {
            var builder = GeefPasfotoRequest.builder()
                    .insz(INSZNumber.of("123"));
            var request = builder.build();

            assertEquals(MagdaRequest.DEFAULT_REGISTRATION, request.getRegistration());
        }

        @Test
        void insz_canBeGivenAsString() {
            var builder = GeefPasfotoRequest.builder()
                    .insz("123");
            var request = builder.build();

            assertEquals(INSZNumber.of("123"), request.getInsz());
        }

        @Test
        void insz_mustBeGiven() {
            var builder = GeefPasfotoRequest.builder();

            assertThrows(IllegalStateException.class, builder::build);
        }
    }
}