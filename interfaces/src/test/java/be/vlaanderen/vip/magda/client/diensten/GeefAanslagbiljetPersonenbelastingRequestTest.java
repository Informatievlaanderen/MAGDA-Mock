package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaRequest;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeefAanslagbiljetPersonenbelastingRequestTest {

    @Nested
    class Builder {

        @Test
        void buildsRequest() {
            var builder = GeefAanslagbiljetPersonenbelastingRequest.builder()
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
            var builder = GeefAanslagbiljetPersonenbelastingRequest.builder()
                    .insz(INSZNumber.of("123"));
            var request = builder.build();

            assertEquals(MagdaRequest.DEFAULT_REGISTRATION, request.getRegistration());
        }

        @Test
        void insz_canBeGivenAsString() {
            var builder = GeefAanslagbiljetPersonenbelastingRequest.builder()
                    .insz("123");
            var request = builder.build();

            assertEquals(INSZNumber.of("123"), request.getInsz());
        }

        @Test
        void insz_mustBeGiven() {
            var builder = GeefAanslagbiljetPersonenbelastingRequest.builder();

            assertThrows(IllegalStateException.class, builder::build);
        }
    }
}