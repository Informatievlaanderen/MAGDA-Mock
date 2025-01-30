package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static be.vlaanderen.vip.magda.client.diensten.TestBase.TEST_INSZ;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GeefHistoriekPersoonRequestTest {

    @Nested
    class Builder {

        private GeefHistoriekPersoonRequest.Builder builder;

        @BeforeEach
        void setup() {
            builder = GeefHistoriekPersoonRequest.builder()
                    .insz(TEST_INSZ)
                    .bron(PersonSource.RR);
        }

        @Test
        void buildsRequestWithOpDatum() {

            LocalDate localDate = LocalDate.of(2024, 1, 1);

            builder.insz(TEST_INSZ);
            builder.opDatum(localDate);
            var request = builder.build();

            assertNotNull(request);
            assertEquals(INSZNumber.of(TEST_INSZ), request.getInsz());
            assertNull(request.getVanDatum());
            assertEquals(LocalDate.of(2024, 1, 1), request.getOpDatum());
            assertEquals(PersonSource.RR, request.getBron());
        }

        @Test
        void buildsRequestWithVanDatum() {

            LocalDate localDate = LocalDate.of(2024, 1, 1);

            builder.insz(TEST_INSZ);
            builder.vanDatum(localDate);
            var request = builder.build();

            assertNotNull(request);
            assertEquals(INSZNumber.of(TEST_INSZ), request.getInsz());
            assertEquals(LocalDate.of(2024, 1, 1), request.getVanDatum());
            assertNull(request.getOpDatum());
            assertEquals(PersonSource.RR, request.getBron());
        }

        @Test
        void whenInszIsNull_throwsException() {
            assertThrows(IllegalStateException.class, () -> builder.build());
        }

        @Test
        void whenOpDatumAndVanDatumIsNottNull_throwsException() {

            LocalDate localDate = LocalDate.of(2024, 1, 1);

            builder.insz(TEST_INSZ)
                    .opDatum(localDate)
                    .vanDatum(localDate);

            assertThrows(IllegalStateException.class, () -> builder.build());
        }


        @Test
        void bronIsNotOptional() {
            builder.insz(TEST_INSZ)
                    .bron(null);
            assertThrows(IllegalStateException.class, () -> builder.build());
        }
    }

    @Nested
    class ToMagdaDocument {
        private static final UUID REQUEST_ID = UUID.fromString("64fb1939-0ca7-432b-b7f4-3b53f7fc3789");

        private MagdaRegistrationInfo magdaRegistrationInfo;
        private GeefHistoriekPersoonRequest.Builder builder;

        @BeforeEach
        void setup() {
            magdaRegistrationInfo = MagdaRegistrationInfo.builder()
                    .identification("identification")
                    .hoedanigheidscode("hoedanigheidscode")
                    .build();

            builder = GeefHistoriekPersoonRequest.builder()
                    .insz(TEST_INSZ)
                    .opDatum(LocalDate.of(2024, 1, 1))
                    .vanDatum(LocalDate.of(2024, 12, 31))
                    .bron(PersonSource.RR);
        }

        @Test
        void fillsInValuesOpDatum() {
            var document = builder.vanDatum(null).build()
                    .toMagdaDocument(REQUEST_ID, magdaRegistrationInfo);

            assertNotNull(document);
            assertEquals(TEST_INSZ, document.getValue("//Vraag/Inhoud/Criteria/INSZ"));
            assertEquals("RR", document.getValue("//Vraag/Inhoud//Bron"));
            assertEquals("2024-01-01", document.getValue("//Vraag/Inhoud/Criteria/Datum"));
            assertThat(document.getValue("//Vragen/Vraag/Inhoud/Criteria/Datum/@Op"), is(equalTo("1")));
        }

        @Test
        void fillsInValuesVanDatum() {
            var document = builder.opDatum(null).build()
                    .toMagdaDocument(REQUEST_ID, magdaRegistrationInfo);

            assertNotNull(document);
            assertEquals(TEST_INSZ, document.getValue("//Vraag/Inhoud/Criteria/INSZ"));
            assertEquals("RR", document.getValue("//Vraag/Inhoud//Bron"));
            assertEquals("2024-12-31", document.getValue("//Vraag/Inhoud/Criteria/Datum"));
            assertThat(document.getValue("//Vragen/Vraag/Inhoud/Criteria/Datum/@Op"), is(equalTo("0")));
        }
    }
}