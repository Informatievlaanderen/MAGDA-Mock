package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.diensten.GeefSociaalStatuutRequest.Builder;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class GeefSociaalStatuutRequestTest {

    @Nested
    class GeefSociaalStatuutRequestBuilder {

        @Test
        void buildsRequestWithDate() {
            var date = LocalDate.now();

            var request = GeefSociaalStatuutRequest.builder()
                    .insz("insz")
                    .socialStatusName("sociaal-statuut")
                    .date(date)
                    .locationName("REGIO_GENT")
                    .build();

            assertEquals(INSZNumber.of("insz"), request.getInsz());
            assertEquals("sociaal-statuut", request.getSocialStatusName());
            assertEquals(date, request.getDate());
            assertEquals("REGIO_GENT", request.getLocationName());
        }

        @Test
        void buildsRequestWithPeriod() {
            var startDate = LocalDate.now();
            var endDate = LocalDate.now().plusYears(1);

            var request = GeefSociaalStatuutRequest.builder()
                    .insz("insz")
                    .socialStatusName("sociaal-statuut")
                    .startDate(startDate)
                    .endDate(endDate)
                    .locationName("REGIO_GENT")
                    .build();

            assertEquals(INSZNumber.of("insz"), request.getInsz());
            assertEquals("sociaal-statuut", request.getSocialStatusName());
            assertEquals(startDate, request.getStartDate());
            assertEquals(endDate, request.getEndDate());
            assertEquals("REGIO_GENT", request.getLocationName());
        }

        @Test
        void throwsException_whenInszNull() {
            var builder = GeefSociaalStatuutRequest.builder()
                    .socialStatusName("sociaal-statuut")
                    .date(LocalDate.now());

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void throwsException_whenSocialStatusNameNull() {
            var builder = GeefSociaalStatuutRequest.builder()
                    .insz("insz")
                    .date(LocalDate.now());

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void locationNameIsOptional() {
            var date = LocalDate.now();

            var request = GeefSociaalStatuutRequest.builder()
                    .insz("insz")
                    .socialStatusName("sociaal-statuut")
                    .date(date)
                    .build();

            assertNull(request.getLocationName());
        }

        @Test
        void throwsException_whenDateAndStartDateNull() {
            var builder = GeefSociaalStatuutRequest.builder()
                    .insz("insz")
                    .socialStatusName("sociaal-statuut");

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void throwsException_whenDateAndStartDateNotNull() {
            var builder = GeefSociaalStatuutRequest.builder()
                    .insz("insz")
                    .socialStatusName("sociaal-statuut")
                    .date(LocalDate.now())
                    .startDate(LocalDate.now());

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void throwsException_whenStartDateNullAndEndDateNotNull() {
            var builder = GeefSociaalStatuutRequest.builder()
                    .insz("insz")
                    .socialStatusName("sociaal-statuut")
                    .date(LocalDate.now())
                    .endDate(LocalDate.now());

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void buildsRequest_withDateOfRequestFlag() {
            var request = GeefSociaalStatuutRequest.builder()
                    .insz("insz")
                    .socialStatusName("sociaal-statuut")
                    .dateOfRequest(true)
                    .build();

            assertTrue(request.isDateOfRequest());
        }

        @Test
        void throwsException_whenDateOfRequestIsUsedTogetherWithDate() {
            var date = LocalDate.now();

            var builder = GeefSociaalStatuutRequest.builder()
                    .insz("insz")
                    .socialStatusName("sociaal-statuut")
                    .dateOfRequest(true)
                    .date(date);

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void throwsException_whenDateOfRequestIsUsedTogetherWithStartDate() {
            var date = LocalDate.now();

            var builder = GeefSociaalStatuutRequest.builder()
                    .insz("insz")
                    .socialStatusName("sociaal-statuut")
                    .dateOfRequest(true)
                    .startDate(date);

            assertThrows(IllegalStateException.class, builder::build);
        }
    }

    @Nested
    class ToMagdaDocument {
        private static final UUID REQUEST_ID = UUID.fromString("64fb1939-0ca7-432b-b7f4-3b53f7fc3789");

        private MagdaRegistrationInfo info;
        private Builder builder;

        @BeforeEach
        void setup() {
            info = MagdaRegistrationInfo.builder()
                    .identification("identification")
                    .hoedanigheidscode("identification")
                    .build();

            builder = GeefSociaalStatuutRequest.builder()
                    .insz("insz")
                    .socialStatusName("sociaal-statuut")
                    .date(LocalDate.now());
        }

        @Test
        void setsSocialStatusName() {
            var request = builder.build()
                    .toMagdaDocument(REQUEST_ID, info);

            assertThat(request.getValue("//SociaalStatuut/Naam"), is(equalTo("sociaal-statuut")));
        }

        @Test
        void setsDateOfRequestIfSpecified() {
            var request = builder
                    .date((LocalDate) null)
                    .dateOfRequest(true)
                    .build()
                    .toMagdaDocument(REQUEST_ID, info);

            var dateInRequestHeader = request.getValue("//Context/Bericht/Tijdstip/Datum");
            assertNotNull(dateInRequestHeader);
            assertTrue(dateInRequestHeader.matches("\\d{4}-\\d{2}-\\d{2}"));
            assertThat(request.getValue("//SociaalStatuut/Datum/Datum"), is(equalTo(dateInRequestHeader)));
        }

        @Test
        void setsDateIfSpecified() {
            var request = builder.date(LocalDate.of(2024,4,1))
                    .build()
                    .toMagdaDocument(REQUEST_ID, info);

            assertThat(request.getValue("//SociaalStatuut/Datum/Datum"), is(equalTo("2024-04-01")));
        }

        @Test
        void doesNotSetDateIfNotSpecified() {
            var request = builder
                    .date((LocalDate)null)
                    .startDate(LocalDate.of(2024,4,1))
                    .endDate(LocalDate.of(2025,4,1))
                    .build()
                    .toMagdaDocument(REQUEST_ID, info);

            assertThat(request.getValue("//SociaalStatuut/Datum/Datum"), is(nullValue()));
        }

        @Test
        void setsPeriodIfSpecified() {
            var request = builder
                    .date((LocalDate)null)
                    .startDate(LocalDate.of(2024,4,1))
                    .endDate(LocalDate.of(2025,4,1))
                    .build()
                    .toMagdaDocument(REQUEST_ID, info);

            assertThat(request.getValue("//SociaalStatuut/Datum/Periode/Begindatum"), is(equalTo("2024-04-01")));
            assertThat(request.getValue("//SociaalStatuut/Datum/Periode/Einddatum"), is(equalTo("2025-04-01")));
        }

        @Test
        void doesNotSetPeriodIfNotSpecified() {
            var request = builder.build()
                    .toMagdaDocument(REQUEST_ID, info);

            assertThat(request.getValue("//SociaalStatuut/Datum/Periode"), is(nullValue()));
        }

        @Test
        void doesNotSetPeriodEndDateIfNotSpecified() {
            var request = builder
                    .date((LocalDate)null)
                    .startDate(LocalDate.of(2024,4,1))
                    .build()
                    .toMagdaDocument(REQUEST_ID, info);

            assertThat(request.getValue("//SociaalStatuut/Datum/Periode/Begindatum"), is(equalTo("2024-04-01")));
            assertThat(request.getValue("//SociaalStatuut/Datum/Periode/Einddatum"), is(nullValue()));
        }

        @Test
        void setsLocationNameIfSpecified() {
            var request = builder.locationName("REGIO_GENT").build()
                    .toMagdaDocument(REQUEST_ID, info);

            assertThat(request.getValue("//SociaalStatuut/Locatie/Naam"), is(equalTo("REGIO_GENT")));
        }

        @Test
        void doesNotSetLocationNameIfNotSpecified() {
            var request = builder.build()
                    .toMagdaDocument(REQUEST_ID, info);

            assertThat(request.getValue("//SociaalStatuut/Locatie"), is(nullValue()));
        }
    }

    @Nested
    class Deprecated {

        @Nested
        class GeefSociaalStatuutRequestBuilder {

            @Test
            void throwsException_whenInszNull() {
                var builder = GeefSociaalStatuutRequest.builder()
                        .sociaalStatuut("sociaal-statuut")
                        .datum(OffsetDateTime.now());

                assertThrows(IllegalStateException.class, builder::build);
            }

            @Test
            void throwsException_whenSociaalStatuutNull() {
                var builder = GeefSociaalStatuutRequest.builder()
                        .insz("insz")
                        .datum(OffsetDateTime.now());

                assertThrows(IllegalStateException.class, builder::build);
            }

            @Test
            void throwsException_whenDatumNull() {
                var builder = GeefSociaalStatuutRequest.builder()
                        .insz("insz")
                        .sociaalStatuut("sociaal-statuut");

                assertThrows(IllegalStateException.class, builder::build);
            }

            @Test
            void isOk_whenRequiredPropertiesPresent() {
                assertDoesNotThrow(() -> GeefSociaalStatuutRequest.builder()
                        .insz("insz")
                        .sociaalStatuut("sociaal-statuut")
                        .datum(OffsetDateTime.now())
                        .build());
            }

        }

        @Nested
        class ToMagdaDocument {
            private static final UUID REQUEST_ID = UUID.fromString("64fb1939-0ca7-432b-b7f4-3b53f7fc3789");

            private MagdaRegistrationInfo info;
            private Builder builder;

            @BeforeEach
            void setup() {
                info = MagdaRegistrationInfo.builder()
                        .identification("identification")
                        .hoedanigheidscode("identification")
                        .build();

                builder = GeefSociaalStatuutRequest.builder()
                        .insz("insz")
                        .sociaalStatuut("sociaal-statuut")
                        .datum(OffsetDateTime.now());
            }

            @Test
            void setsSociaalStatuus() {
                var request = builder.build()
                        .toMagdaDocument(REQUEST_ID, info);

                assertThat(request.getValue("//SociaalStatuut/Naam"), is(equalTo("sociaal-statuut")));
            }

            @Test
            void setsDatum() {
                var request = builder.datum(OffsetDateTime.parse("2023-01-22T00:00:00.000+00:00"))
                        .build()
                        .toMagdaDocument(REQUEST_ID, info);

                assertThat(request.getValue("//SociaalStatuut/Datum/Datum"), is(equalTo("2023-01-22")));
            }
        }
    }
}
