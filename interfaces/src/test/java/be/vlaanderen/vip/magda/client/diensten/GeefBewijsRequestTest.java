package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.DirectRegistration;
import be.vlaanderen.vip.magda.client.KeyRegistration;
import be.vlaanderen.vip.magda.client.MagdaRequest;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GeefBewijsRequestTest {

    @Nested
    class Builder {

        @Test
        void buildsRequest() {
            var builder = GeefBewijsRequest.builder()
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
            var builder = GeefBewijsRequest.builder()
                    .insz(INSZNumber.of("123"));
            var request = builder.build();

            var keyRegistration = assertInstanceOf(KeyRegistration.class, request.getRegistration());
            assertEquals(MagdaRequest.DEFAULT_REGISTRATION, keyRegistration.getKey());
        }

        @Test
        void registration_canBeGivenDirectlyAsInfo() {
            var builder = GeefBewijsRequest.builder()
                    .insz(INSZNumber.of("123"))
                    .registration(MagdaRegistrationInfo.builder()
                            .identification("identification")
                            .hoedanigheidscode("hoedanigheidscode")
                            .build());
            var request = builder.build();

            var directRegistration = assertInstanceOf(DirectRegistration.class, request.getRegistration());
            var registrationInfo = directRegistration.getInfo();
            assertEquals("identification", registrationInfo.getIdentification());
            assertEquals(Optional.of("hoedanigheidscode"), registrationInfo.getHoedanigheidscode());
        }

        @Test
        void registration_cannotBeGivenBothAsKeyAndAsInfo() {
            var builder = GeefBewijsRequest.builder()
                    .insz(INSZNumber.of("123"))
                    .registration("456")
                    .registration(MagdaRegistrationInfo.builder()
                            .identification("identification")
                            .hoedanigheidscode("hoedanigheidscode")
                            .build());

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void insz_canBeGivenAsString() {
            var builder = GeefBewijsRequest.builder()
                    .insz("123");
            var request = builder.build();

            assertEquals(INSZNumber.of("123"), request.getInsz());
        }

        @Test
        void insz_mustBeGiven() {
            var builder = GeefBewijsRequest.builder();

            assertThrows(IllegalStateException.class, builder::build);
        }
    }
}