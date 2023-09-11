package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.diensten.GeefSociaalStatuutRequest.Builder;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class GeefSociaalStatuutRequestTest {

    @Nested
    class GeefSociaalStatuutRequestBuilder {

        @Test
        void buildsRequest() {
            var date = OffsetDateTime.now();

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
        void locationNameIsOptional() {
            var date = OffsetDateTime.now();

            var request = GeefSociaalStatuutRequest.builder()
                    .insz("insz")
                    .socialStatusName("sociaal-statuut")
                    .date(date)
                    .build();

            assertNull(request.getLocationName());
        }

        @Test
        void throwsException_whenInszNull() {
            var builder = GeefSociaalStatuutRequest.builder()
                    .socialStatusName("sociaal-statuut")
                    .date(OffsetDateTime.now());

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void throwsException_whenSocialStatusNameNull() {
            var builder = GeefSociaalStatuutRequest.builder()
                    .insz("insz")
                    .date(OffsetDateTime.now());

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void throwsException_whenDateNull() {
            var builder = GeefSociaalStatuutRequest.builder()
                    .insz("insz")
                    .socialStatusName("sociaal-statuut");

            assertThrows(IllegalStateException.class, builder::build);
        }
    }

    @Nested
    class ToMagdaDocument {
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
                    .date(OffsetDateTime.now());
        }

        @Test
        void setsSocialStatusName() {
            var request = builder.build()
                    .toMagdaDocument(info);

            assertThat(request.getValue("//SociaalStatuut/Naam"), is(equalTo("sociaal-statuut")));
        }

        @Test
        void setsDate() {
            var request = builder.date(OffsetDateTime.parse("2023-01-22T00:00:00.000+00:00"))
                    .build()
                    .toMagdaDocument(info);

            assertThat(request.getValue("//SociaalStatuut/Datum/Datum"), is(equalTo("2023-01-22")));
        }

        @Test
        void setsLocationNameIfSpecified() {
            var request = builder.locationName("REGIO_GENT").build()
                    .toMagdaDocument(info);

            assertThat(request.getValue("//SociaalStatuut/Locatie/Naam"), is(equalTo("REGIO_GENT")));
        }

        @Test
        void doesNotSetLocationNameIfNotSpecified() {
            var request = builder.build()
                    .toMagdaDocument(info);

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
                        .toMagdaDocument(info);

                assertThat(request.getValue("//SociaalStatuut/Naam"), is(equalTo("sociaal-statuut")));
            }

            @Test
            void setsDatum() {
                var request = builder.datum(OffsetDateTime.parse("2023-01-22T00:00:00.000+00:00"))
                        .build()
                        .toMagdaDocument(info);

                assertThat(request.getValue("//SociaalStatuut/Datum/Datum"), is(equalTo("2023-01-22")));
            }
        }
    }
}
