package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class GeefHistoriekGezinssamenstellingRequestTest {

    @Nested
    class GeefHistoriekGezinssamenstellingRequestBuilder {

        @Test
        void buildsRequestWhenFromDateIsGiven() {
            var date = LocalDate.now();

            var request = GeefHistoriekGezinssamenstellingRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .fromDate(date)
                    .build();

            assertEquals(INSZNumber.of(TestBase.TEST_INSZ), request.getInsz());
            assertEquals(date, request.getFromDate());
        }

        @Test
        void buildsRequestWhenOnDateIsGiven() {
            var date = LocalDate.now();

            var request = GeefHistoriekGezinssamenstellingRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .onDate(date)
                    .build();

            assertEquals(INSZNumber.of(TestBase.TEST_INSZ), request.getInsz());
            assertEquals(date, request.getOnDate());
        }


        @Test
        void throwsException_whenInszIsNull() {

            var builder = GeefHistoriekGezinssamenstellingRequest.builder()
                    .fromDate(LocalDate.now());

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void throwsException_whenFromDateAndOnDateIsNull() {
            var builder = GeefHistoriekGezinssamenstellingRequest.builder()
                    .insz(TestBase.TEST_INSZ);

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void throwsException_whenFromDateAndOnDateIsNotNull() {
            var builder = GeefHistoriekGezinssamenstellingRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .fromDate(LocalDate.now())
                    .onDate(LocalDate.now());

            assertThrows(IllegalStateException.class, builder::build);
        }
    }

    @Nested
    class ToMagdaDocument {
        private MagdaRegistrationInfo info;
        private GeefHistoriekGezinssamenstellingRequest.Builder builder;

        @BeforeEach
        void setup() {

            info = MagdaRegistrationInfo.builder()
                    .identification("identification")
                    .hoedanigheidscode("identification")
                    .build();

            builder = GeefHistoriekGezinssamenstellingRequest.builder()
                    .insz(TestBase.TEST_INSZ);
        }

        @Test
        void setsFieldsWithFromDateSpecified() {
            var request = builder
                    .fromDate(LocalDate.of(2023, 01, 22))
                    .build()
                    .toMagdaDocument(info);

            assertThat(request.getValue("//Vragen/Vraag/Inhoud/Criteria/Datum"), is(equalTo("2023-01-22")));
            assertThat(request.getValue("//Vragen/Vraag/Inhoud/Criteria/Datum/@Op"), is(equalTo("1")));
        }

        @Test
        void setsFieldsWithOnDateSpecified() {
            var request = builder
                    .onDate(LocalDate.of(2023, 01, 22))
                    .build()
                    .toMagdaDocument(info);

            assertThat(request.getValue("//Vragen/Vraag/Inhoud/Criteria/Datum"), is(equalTo("2023-01-22")));
            assertThat(request.getValue("//Vragen/Vraag/Inhoud/Criteria/Datum/@Op"), is(equalTo("0")));
        }
    }
}
