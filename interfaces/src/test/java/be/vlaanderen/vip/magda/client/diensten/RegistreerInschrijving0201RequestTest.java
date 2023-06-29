package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaRequest;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.diensten.subject.KBONumber;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class RegistreerInschrijving0201RequestTest {

    @Nested
    class Builder {

        @Test
        void buildsRequest() {
            var builder = RegistreerInschrijving0201Request.builder()
                    .subject(INSZNumber.of("123"))
                    .registration("456")
                    .startDate(LocalDate.of(2023, 1, 1))
                    .endDate(LocalDate.of(2023, 4, 1));
            var request = builder.build();

            assertNull(request.getCorrelationId());
            assertNotNull(request.getRequestId());
            assertEquals(INSZNumber.of("123"), request.getSubject());
            assertEquals("456", request.getRegistration());
        }

        @Test
        void registration_HasDefaultValue() {
            var builder = RegistreerInschrijving0201Request.builder()
                    .subject(INSZNumber.of("123"))
                    .startDate(LocalDate.of(2023, 1, 1))
                    .endDate(LocalDate.of(2023, 4, 1));
            var request = builder.build();

            assertEquals(MagdaRequest.DEFAULT_REGISTRATION, request.getRegistration());
        }

        @Test
        void subject_canBeInszNumber() {
            var builder = RegistreerInschrijving0201Request.builder()
                    .subject(INSZNumber.of("123"))
                    .startDate(LocalDate.of(2023, 1, 1))
                    .endDate(LocalDate.of(2023, 4, 1));
            var request = builder.build();

            assertEquals(INSZNumber.of("123"), request.getSubject());
        }

        @Test
        void subject_canBeKboNumber() {
            var builder = RegistreerInschrijving0201Request.builder()
                    .subject(KBONumber.of("123"))
                    .startDate(LocalDate.of(2023, 1, 1))
                    .endDate(LocalDate.of(2023, 4, 1));
            var request = builder.build();

            assertEquals(KBONumber.of("123"), request.getSubject());
        }

        @Test
        void subject_canBeGivenAsInszString() {
            var builder = RegistreerInschrijving0201Request.builder()
                    .insz("123")
                    .startDate(LocalDate.of(2023, 1, 1))
                    .endDate(LocalDate.of(2023, 4, 1));
            var request = builder.build();

            assertEquals(INSZNumber.of("123"), request.getSubject());
        }

        @Test
        void subject_canBeGivenAsKboString() {
            var builder = RegistreerInschrijving0201Request.builder()
                    .kbo("123")
                    .startDate(LocalDate.of(2023, 1, 1))
                    .endDate(LocalDate.of(2023, 4, 1));
            var request = builder.build();

            assertEquals(KBONumber.of("123"), request.getSubject());
        }

        @Test
        void subject_mustBeGiven() {
            var builder = RegistreerInschrijving0201Request.builder()
                    .startDate(LocalDate.of(2023, 1, 1))
                    .endDate(LocalDate.of(2023, 4, 1));

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void startDate_mustBeGiven() {
            var builder = RegistreerInschrijving0201Request.builder()
                    .subject(INSZNumber.of("123"))
                    .endDate(LocalDate.of(2023, 4, 1));

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void endDate_mustBeGiven() {
            var builder = RegistreerInschrijving0201Request.builder()
                    .subject(INSZNumber.of("123"))
                    .startDate(LocalDate.of(2023, 1, 1));

            assertThrows(IllegalStateException.class, builder::build);
        }
    }
}