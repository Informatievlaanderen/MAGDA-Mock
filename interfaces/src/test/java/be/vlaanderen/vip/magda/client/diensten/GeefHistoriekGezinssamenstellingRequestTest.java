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
        void buildsRequest() {
            var date = LocalDate.now();

            var request = GeefHistoriekGezinssamenstellingRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .date(date)
                    .dateOp(true)
                    .build();

            assertEquals(INSZNumber.of(TestBase.TEST_INSZ), request.getInsz());
            assertEquals(date, request.getDate());
            assertEquals(true, request.getDateOp());
        }

        @Test
        void dateOpIsOptional() {

            var request = GeefHistoriekGezinssamenstellingRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .date(LocalDate.now())
                    .build();

            assertNull(request.getDateOp());
        }

        @Test
        void throwsException_whenInszNull() {

            var builder = GeefHistoriekGezinssamenstellingRequest.builder()
                    .date(LocalDate.now());

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void throwsException_whenDateNull() {
            var builder = GeefHistoriekGezinssamenstellingRequest.builder()
                    .insz(TestBase.TEST_INSZ);

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
                    .insz(TestBase.TEST_INSZ)
                    .date(LocalDate.now().minusYears(1));
        }

        @Test
        void setsFields() {
            var request = builder
                    .date(LocalDate.of(2023, 01, 22))
                    .dateOp(true)
                    .build()
                    .toMagdaDocument(info);

            assertThat(request.getValue("//Vragen/Vraag/Inhoud/Criteria/Datum"), is(equalTo("2023-01-22")));
            assertThat(request.getValue("//Vragen/Vraag/Inhoud/Criteria/Datum/@Op"), is(equalTo("1")));
        }

        @Test
        void doesNotSetDateOpIfNotSpecified() {
            var request = builder
                    .build()
                    .toMagdaDocument(info);

            assertThat(request.getValue("//Vragen/Vraag/Inhoud/Criteria/Datum/@Op"), is(nullValue()));
        }
    }
}
