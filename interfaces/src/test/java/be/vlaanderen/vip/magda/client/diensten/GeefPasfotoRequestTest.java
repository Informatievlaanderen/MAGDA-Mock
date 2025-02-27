package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.KeyRegistration;
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
            assertEquals(INSZNumber.of("123"), request.getInsz());
            var keyRegistration = assertInstanceOf(KeyRegistration.class, request.getRegistration());
            assertEquals("456", keyRegistration.getKey());
        }

        @Test
        void registration_HasDefaultValue() {
            var builder = GeefPasfotoRequest.builder()
                    .insz(INSZNumber.of("123"));
            var request = builder.build();

            var keyRegistration = assertInstanceOf(KeyRegistration.class, request.getRegistration());
            assertEquals(MagdaRequest.DEFAULT_REGISTRATION, keyRegistration.getKey());
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