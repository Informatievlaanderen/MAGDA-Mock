package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static be.vlaanderen.vip.magda.client.diensten.EducationEnrollmentSource.*;
import static be.vlaanderen.vip.magda.client.diensten.TestBase.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;

class GeefHistoriekInschrijvingRequestTest {

    @Nested
    class Builder {

        private GeefHistoriekInschrijvingRequest.Builder builder;

        @BeforeEach
        void setup() {
            builder = GeefHistoriekInschrijvingRequest.builder()
                    .startDate(LocalDate.of(2024, 1, 1))
                    .endDate(LocalDate.of(2024, 12, 31))
                    .sources(Set.of(LP, VWO));
        }

        @Test
        void buildsRequest() {
            builder.insz(TEST_INSZ);
            var request = builder.build();

            assertNotNull(request);
            assertEquals(INSZNumber.of(TEST_INSZ), request.getInsz());
            assertEquals(LocalDate.of(2024, 1, 1), request.getStartDate());
            assertEquals(LocalDate.of(2024, 12, 31), request.getEndDate());
            assertEquals(Set.of(LP, VWO), request.getSources());
        }

        @Test
        void whenInszIsNull_throwsException() {
            assertThrows(IllegalStateException.class, () -> builder.build());
        }

        @Test
        void whenStartDateIsNull_throwsException() {
            builder.insz(TEST_INSZ)
                   .startDate(null);

            assertThrows(IllegalStateException.class, () -> builder.build());
        }

        @Test
        void whenEndDateIsNull_throwsException() {
            builder.insz(TEST_INSZ)
                   .endDate(null);

            assertThrows(IllegalStateException.class, () -> builder.build());
        }

        @Test
        void sourcesIsOptional() {
            builder.insz(TEST_INSZ)
                   .sources(null);

            var request = builder.build();

            assertNotNull(request);
            assertNull(request.getSources());
        }
    }

    @Nested
    class ToMagdaDocument {
        private static final UUID REQUEST_ID = UUID.fromString("64fb1939-0ca7-432b-b7f4-3b53f7fc3789");

        private MagdaRegistrationInfo magdaRegistrationInfo;
        private GeefHistoriekInschrijvingRequest.Builder builder;

        @BeforeEach
        void setup() {
            magdaRegistrationInfo = MagdaRegistrationInfo.builder()
                    .identification("identification")
                    .hoedanigheidscode("hoedanigheidscode")
                    .build();

            builder = GeefHistoriekInschrijvingRequest.builder()
                    .insz(TEST_INSZ)
                    .startDate(LocalDate.of(2024, 1, 1))
                    .endDate(LocalDate.of(2024, 12, 31))
                    .sources(Set.of(LP, VWO));
        }

        @Test
        void fillsInValues() {
            var document = builder.build()
                    .toMagdaDocument(REQUEST_ID, magdaRegistrationInfo);

            assertNotNull(document);
            assertEquals(TEST_INSZ, document.getValue("//Vraag/Inhoud/Criteria/INSZ"));
            assertEquals("2024-01-01", document.getValue("//Vraag/Inhoud/Criteria/Periode/Begin"));
            assertEquals("2024-12-31", document.getValue("//Vraag/Inhoud/Criteria/Periode/Einde"));
            assertThat(document.getValues("//Vraag/Inhoud/Criteria/Bronnen/Bron"), containsInAnyOrder("LP", "VWO"));
        }

        @Test
        void whenSourcesAreNotGiven_documentDoesNotIncludeThem() {
            var document = builder
                    .sources(null)
                    .build()
                    .toMagdaDocument(REQUEST_ID, magdaRegistrationInfo);

            assertNotNull(document);
            assertNull(document.getValue("//Vraag/Inhoud/Criteria/Bronnen"));
        }
    }
}