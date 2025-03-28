package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.KeyRegistration;
import be.vlaanderen.vip.magda.client.MagdaRequest;
import be.vlaanderen.vip.magda.client.diensten.RegistreerInschrijvingRequest.Builder;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

class RegistreerInschrijvingRequestTest {

    @Nested
    class RegistreerInschrijvingRequestBuilder {

        @Test
        void buildsRequest() {
            var builder = RegistreerInschrijvingRequest.builder()
                    .insz(INSZNumber.of("123"))
                    .registration("456")
                    .startDate(LocalDate.of(2023, 1, 1))
                    .endDate(LocalDate.of(2023, 4, 1));
            var request = builder.build();

            assertNull(request.getCorrelationId());
            assertEquals(INSZNumber.of("123"), request.getInsz());
            var keyRegistration = assertInstanceOf(KeyRegistration.class, request.getRegistration());
            assertEquals("456", keyRegistration.getKey());
        }

        @Test
        void registration_HasDefaultValue() {
            var builder = RegistreerInschrijvingRequest.builder()
                    .insz(INSZNumber.of("123"))
                    .startDate(LocalDate.of(2023, 1, 1))
                    .endDate(LocalDate.of(2023, 4, 1));
            var request = builder.build();

            var keyRegistration = assertInstanceOf(KeyRegistration.class, request.getRegistration());
            assertEquals(MagdaRequest.DEFAULT_REGISTRATION, keyRegistration.getKey());
        }

        @Test
        void insz_canBeGivenAsString() {
            var builder = RegistreerInschrijvingRequest.builder()
                    .insz("123")
                    .startDate(LocalDate.of(2023, 1, 1))
                    .endDate(LocalDate.of(2023, 4, 1));
            var request = builder.build();

            assertEquals(INSZNumber.of("123"), request.getInsz());
        }

        @Test
        void insz_mustBeGiven() {
            var builder = RegistreerInschrijvingRequest.builder()
                    .startDate(LocalDate.of(2023, 1, 1))
                    .endDate(LocalDate.of(2023, 4, 1));

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void startDate_mustBeGiven() {
            var builder = RegistreerInschrijvingRequest.builder()
                    .insz(INSZNumber.of("123"))
                    .endDate(LocalDate.of(2023, 4, 1));

            assertThrows(IllegalStateException.class, builder::build);
        }

    }
    
    @Nested
    class ToMagdaDocument {
        private static final UUID REQUEST_ID = UUID.fromString("64fb1939-0ca7-432b-b7f4-3b53f7fc3789");

        private MagdaRegistrationInfo info;
        private Builder builder;
        private LocalDate start, end;
        
        @BeforeEach
        void setup() {
            info = MagdaRegistrationInfo.builder()
                                        .identification("identification")
                                        .hoedanigheidscode("identification")
                                        .build();
            
            start = LocalDate.now();
            end = start.plus(10, ChronoUnit.DAYS);
            
            builder = RegistreerInschrijvingRequest.builder()
                                                   .insz("insz")
                                                   .startDate(start)
                                                   .endDate(end);
        }
        
        @Test
        void setsFields() {
            var request = builder.build()
                                 .toMagdaDocument(REQUEST_ID, info);
            
            assertAll(
                    () -> assertThat(request.getValue("//Vraag/Inhoud/Inschrijving/Periode/Begin"), is(equalTo(start.format(DateTimeFormatter.ISO_LOCAL_DATE)))),
                    () -> assertThat(request.getValue("//Vraag/Inhoud/Inschrijving/Periode/Einde"), is(equalTo(end.format(DateTimeFormatter.ISO_LOCAL_DATE)))));
        }
        
        @Test
        void noEndDate_whenMissing() {
            var request = builder.endDate(null)
                                 .build()
                                 .toMagdaDocument(REQUEST_ID, info);
            
            var result = request.xpath("//Vraag/Inhoud/Inschrijving/Periode/Einde");
            assertThat(result.getLength(), is(0));
        }
    }
}