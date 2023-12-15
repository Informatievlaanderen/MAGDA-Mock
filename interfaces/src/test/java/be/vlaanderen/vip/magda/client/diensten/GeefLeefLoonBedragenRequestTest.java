package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Year;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class GeefLeefLoonBedragenRequestTest {

    @Nested
    class GeefLeefLoonBedragenRequestBuilder {

        @Test
        void buildsRequest() {
            var year = Year.of(2023);
            var request = GeefLeefLoonBedragenRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .year(year)
                    .currentYear(true)
                    .numberOfYearsAgo(2)
                    .build();

            assertEquals(INSZNumber.of(TestBase.TEST_INSZ), request.getInsz());
            assertEquals(year, request.getYear());
            assertEquals(true, request.getCurrentYear());
            assertEquals(2, request.getNumberOfYearsAgo());
        }

        @Test
        void numberOfYearsAgoIsOptional() {

            var request = GeefLeefLoonBedragenRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .year(Year.of(2023))
                    .currentYear(true)
                    .build();

            assertNull(request.getNumberOfYearsAgo());
        }

        @Test
        void throwsException_whenInszNull() {

            var year = Year.of(2023);
            var builder = GeefLeefLoonBedragenRequest.builder()
                    .year(year)
                    .currentYear(true)
                    .numberOfYearsAgo(2);

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void throwsException_whenYearNull() {
            var builder = GeefLeefLoonBedragenRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .currentYear(true)
                    .numberOfYearsAgo(2);

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void throwsException_whenCurrentYearNull() {

            var year = Year.of(2023);
            var builder = GeefLeefLoonBedragenRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .year(year)
                    .numberOfYearsAgo(2);

            assertThrows(IllegalStateException.class, builder::build);
        }
    }

    @Nested
    class ToMagdaDocument {
        private MagdaRegistrationInfo info;
        private GeefLeefLoonBedragenRequest.Builder builder;

        @BeforeEach
        void setup() {

            info = MagdaRegistrationInfo.builder()
                    .identification("identification")
                    .hoedanigheidscode("identification")
                    .build();

            builder = GeefLeefLoonBedragenRequest.builder()
                    .insz(TestBase.TEST_INSZ);
        }

        @Test
        void setsFields() {
            var year = Year.of(2023);
            var request = builder
                    .year(year)
                    .currentYear(true)
                    .numberOfYearsAgo(2)
                    .build()
                    .toMagdaDocument(info);

            assertThat(request.getValue("//Criteria/Jaar"), is(equalTo(year.toString())));
            assertThat(request.getValue("//Criteria/Jaar/@Huidig"), is(equalTo("1")));
            assertThat(request.getValue("//Criteria/AantalJaarTerug"), is(equalTo("2")));
        }

        @Test
        void doesNotSetNumberOfYearsAgoIfNotSpecified() {
            var year = Year.of(2023);
            var request = builder
                    .year(year)
                    .currentYear(true)
                    .build()
                    .toMagdaDocument(info);

            assertThat(request.getValue("//Criteria/AantalJaarTerug"), is(nullValue()));
        }
    }
}
