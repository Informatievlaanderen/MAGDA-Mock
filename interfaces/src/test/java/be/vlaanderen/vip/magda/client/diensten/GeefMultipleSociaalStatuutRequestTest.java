package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.diensten.GeefMultipleSociaalStatuutRequest.Builder;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class GeefMultipleSociaalStatuutRequestTest {

    @Nested
    class GeefMultipleSociaalStatuutRequestBuilder {

        @Test
        void buildsRequest_whenOneSocialStatutesGiven() {

            var socialStatutes = Set.of(
                    SociaalStatuutRequestCriteria.builder()
                            .socialStatusName("SOCIAL_STATUTE_NAME")
                            .date(LocalDate.now())
                            .build()
            );

            var request = GeefMultipleSociaalStatuutRequest.builder()
                    .insz("insz")
                    .socialStatutes(socialStatutes)
                    .build();

            assertEquals(INSZNumber.of("insz"), request.getInsz());
            assertEquals(socialStatutes, request.getSocialStatutes());
        }

        @Test
        void buildsRequest_whenMultipleSocialStatutesGiven() {

            var socialStatutes = Set.of(
                    SociaalStatuutRequestCriteria.builder()
                            .socialStatusName("SOCIAL_STATUTE_NAME_1")
                            .date(LocalDate.now())
                            .build(),
                    SociaalStatuutRequestCriteria.builder()
                            .socialStatusName("SOCIAL_STATUTE_NAME_2")
                            .date(LocalDate.now())
                            .build(),
                    SociaalStatuutRequestCriteria.builder()
                            .socialStatusName("SOCIAL_STATUTE_NAME_3")
                            .date(LocalDate.now())
                            .build()
            );

            var request = GeefMultipleSociaalStatuutRequest.builder()
                    .insz("insz")
                    .socialStatutes(socialStatutes)
                    .build();

            assertEquals(INSZNumber.of("insz"), request.getInsz());
            assertEquals(socialStatutes, request.getSocialStatutes());
        }

        @Test
        void throwsException_whenSocialStatutesNull() {

            var builder = GeefMultipleSociaalStatuutRequest.builder()
                    .insz("insz");

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void throwsException_whenMoreThanEightSocialStatutesGiven() {

            var socialStatutes = Set.of(
                    SociaalStatuutRequestCriteria.builder()
                            .socialStatusName("SOCIAL_STATUTE_NAME_1")
                            .date(LocalDate.now())
                            .build(),
                    SociaalStatuutRequestCriteria.builder()
                            .socialStatusName("SOCIAL_STATUTE_NAME_2")
                            .date(LocalDate.now())
                            .build(),
                    SociaalStatuutRequestCriteria.builder()
                            .socialStatusName("SOCIAL_STATUTE_NAME_3")
                            .date(LocalDate.now())
                            .build(),
                    SociaalStatuutRequestCriteria.builder()
                            .socialStatusName("SOCIAL_STATUTE_NAME_4")
                            .date(LocalDate.now())
                            .build(),
                    SociaalStatuutRequestCriteria.builder()
                            .socialStatusName("SOCIAL_STATUTE_NAME_5")
                            .date(LocalDate.now())
                            .build(),
                    SociaalStatuutRequestCriteria.builder()
                            .socialStatusName("SOCIAL_STATUTE_NAME_6")
                            .date(LocalDate.now())
                            .build(),
                    SociaalStatuutRequestCriteria.builder()
                            .socialStatusName("SOCIAL_STATUTE_NAME_7")
                            .date(LocalDate.now())
                            .build(),
                    SociaalStatuutRequestCriteria.builder()
                            .socialStatusName("SOCIAL_STATUTE_NAME_8")
                            .date(LocalDate.now())
                            .build(),
                    SociaalStatuutRequestCriteria.builder()
                            .socialStatusName("SOCIAL_STATUTE_NAME_9")
                            .date(LocalDate.now())
                            .build()
            );

            var builder = GeefMultipleSociaalStatuutRequest.builder()
                    .insz("insz")
                    .socialStatutes(socialStatutes);

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void throwsException_whenInszNull() {
            var socialStatutes = Set.of(
                    SociaalStatuutRequestCriteria.builder()
                            .socialStatusName("SOCIAL_STATUTE_NAME")
                            .date(LocalDate.now())
                            .build()
            );

            var builder = GeefMultipleSociaalStatuutRequest.builder()
                    .socialStatutes(socialStatutes);

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

            builder = GeefMultipleSociaalStatuutRequest.builder()
                    .insz("insz");
        }

        @Test
        void setsSocialStatutes() {
            var socialStatutes = Set.of(
                    SociaalStatuutRequestCriteria.builder()
                            .socialStatusName("SOCIAL_STATUTE_NAME_1")
                            .date(LocalDate.of(2024, 3,1))
                            .build(),
                    SociaalStatuutRequestCriteria.builder()
                            .socialStatusName("SOCIAL_STATUTE_NAME_2")
                            .startDate(LocalDate.of(2024, 3,1))
                            .build(),
                    SociaalStatuutRequestCriteria.builder()
                            .socialStatusName("SOCIAL_STATUTE_NAME_3")
                            .startDate(LocalDate.of(2024,6,30))
                            .endDate(LocalDate.of(2025,4,1))
                            .locationName("LOCATION_3")
                            .build()
            );

            var request = builder
                    .socialStatutes(socialStatutes)
                    .build()
                    .toMagdaDocument(info);

            assertThat(request.getValue("//SociaalStatuut[Naam[text()='SOCIAL_STATUTE_NAME_1']]/Naam"), is(equalTo("SOCIAL_STATUTE_NAME_1")));
            assertThat(request.getValue("//SociaalStatuut[Naam[text()='SOCIAL_STATUTE_NAME_1']]/Datum/Datum"), is(equalTo("2024-03-01")));
            assertThat(request.getValue("//SociaalStatuut[Naam[text()='SOCIAL_STATUTE_NAME_1']]/Datum/Periode"), is(nullValue()));
            assertThat(request.getValue("//SociaalStatuut[Naam[text()='SOCIAL_STATUTE_NAME_1']]/Locatie"), is(nullValue()));

            assertThat(request.getValue("//SociaalStatuut[Naam[text()='SOCIAL_STATUTE_NAME_2']]/Naam"), is(equalTo("SOCIAL_STATUTE_NAME_2")));
            assertThat(request.getValue("//SociaalStatuut[Naam[text()='SOCIAL_STATUTE_NAME_2']]/Datum/Datum"), is(nullValue()));
            assertThat(request.getValue("//SociaalStatuut[Naam[text()='SOCIAL_STATUTE_NAME_2']]/Datum/Periode/Begindatum"), is(equalTo("2024-03-01")));
            assertThat(request.getValue("//SociaalStatuut[Naam[text()='SOCIAL_STATUTE_NAME_2']]/Datum/Periode/Einddatum"), is(nullValue()));
            assertThat(request.getValue("//SociaalStatuut[Naam[text()='SOCIAL_STATUTE_NAME_2']]/Locatie"), is(nullValue()));

            assertThat(request.getValue("//SociaalStatuut[Naam[text()='SOCIAL_STATUTE_NAME_3']]/Naam"), is(equalTo("SOCIAL_STATUTE_NAME_3")));
            assertThat(request.getValue("//SociaalStatuut[Naam[text()='SOCIAL_STATUTE_NAME_3']]/Datum/Datum"), is(nullValue()));
            assertThat(request.getValue("//SociaalStatuut[Naam[text()='SOCIAL_STATUTE_NAME_3']]/Datum/Periode/Begindatum"), is(equalTo("2024-06-30")));
            assertThat(request.getValue("//SociaalStatuut[Naam[text()='SOCIAL_STATUTE_NAME_3']]/Datum/Periode/Einddatum"), is(equalTo("2025-04-01")));
            assertThat(request.getValue("//SociaalStatuut[Naam[text()='SOCIAL_STATUTE_NAME_3']]/Locatie/Naam"), is(equalTo("LOCATION_3")));
        }
    }
}
