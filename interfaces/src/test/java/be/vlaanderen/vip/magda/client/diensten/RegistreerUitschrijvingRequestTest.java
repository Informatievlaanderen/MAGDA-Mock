package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaRequest;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class RegistreerUitschrijvingRequestTest {

    @Nested
    class Builder {

        @Test
        void buildsRequest() {
            var builder = RegistreerUitschrijvingRequest.builder()
                    .insz(INSZNumber.of("123"))
                    .registration("456")
                    .startDate(LocalDate.of(2023, 1, 1))
                    .endDate(LocalDate.of(2023, 4, 1));
            var request = builder.build();

            assertNull(request.getCorrelationId());
            assertNotNull(request.getRequestId());
            assertEquals(INSZNumber.of("123"), request.getInsz());
            assertEquals("456", request.getRegistration());
        }

        @Test
        void registration_HasDefaultValue() {
            var builder = RegistreerUitschrijvingRequest.builder()
                    .insz(INSZNumber.of("123"))
                    .startDate(LocalDate.of(2023, 1, 1))
                    .endDate(LocalDate.of(2023, 4, 1));
            var request = builder.build();

            assertEquals(MagdaRequest.DEFAULT_REGISTRATION, request.getRegistration());
        }

        @Test
        void insz_canBeGivenAsString() {
            var builder = RegistreerUitschrijvingRequest.builder()
                    .insz("123")
                    .startDate(LocalDate.of(2023, 1, 1))
                    .endDate(LocalDate.of(2023, 4, 1));
            var request = builder.build();

            assertEquals(INSZNumber.of("123"), request.getInsz());
        }

        @Test
        void insz_mustBeGiven() {
            var builder = RegistreerUitschrijvingRequest.builder()
                    .startDate(LocalDate.of(2023, 1, 1))
                    .endDate(LocalDate.of(2023, 4, 1));

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void startDate_isOptional() {
            var builder = RegistreerUitschrijvingRequest.builder()
                    .insz(INSZNumber.of("123"))
                    .endDate(LocalDate.of(2023, 4, 1));
            var request = builder.build();

            assertNull(request.getStartDate());
        }

        @Test
        void endDate_isOptional() {
            var builder = RegistreerUitschrijvingRequest.builder()
                    .insz(INSZNumber.of("123"))
                    .startDate(LocalDate.of(2023, 1, 1));
            var request = builder.build();

            assertNull(request.getEndDate());
        }
    }
}