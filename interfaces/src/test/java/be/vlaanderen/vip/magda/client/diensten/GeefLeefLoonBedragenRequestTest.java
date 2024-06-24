package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

class GeefLeefLoonBedragenRequestTest {

    @Nested
    class GeefLeefLoonBedragenRequestBuilder {

        @Test
        void buildsRequestWhenCurrentYearIsGiven() {
            var year = Year.of(2023);
            var request = GeefLeefLoonBedragenRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .currentYear(year)
                    .numberOfYearsAgo(2)
                    .build();

            assertEquals(INSZNumber.of(TestBase.TEST_INSZ), request.getInsz());
            assertEquals(year, request.getCurrentYear());
            assertEquals(2, request.getNumberOfYearsAgo());
        }

        @Test
        void buildsRequestWhenReferenceYearIsGiven() {
            var year = Year.of(2023);
            var request = GeefLeefLoonBedragenRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .referenceYear(year)
                    .numberOfYearsAgo(2)
                    .build();

            assertEquals(INSZNumber.of(TestBase.TEST_INSZ), request.getInsz());
            assertEquals(year, request.getReferenceYear());
            assertEquals(2, request.getNumberOfYearsAgo());
        }

        @Test
        void numberOfYearsAgoIsOptional() {

            var request = GeefLeefLoonBedragenRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .currentYear(Year.of(2023))
                    .build();

            assertNull(request.getNumberOfYearsAgo());
        }

        @Test
        void throwsException_whenInszIsNull() {

            var year = Year.of(2023);
            var builder = GeefLeefLoonBedragenRequest.builder()
                    .currentYear(year)
                    .numberOfYearsAgo(2);

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void throwsException_whenCurrentYearAndReferenceYearIsNull() {
            var builder = GeefLeefLoonBedragenRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .numberOfYearsAgo(2);

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void throwsException_whenCurrentYearAndReferenceYearIsNotNull() {

            var year = Year.of(2023);
            var builder = GeefLeefLoonBedragenRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .currentYear(year)
                    .referenceYear(year)
                    .numberOfYearsAgo(2);

            assertThrows(IllegalStateException.class, builder::build);
        }
    }

    @Nested
    class ToMagdaDocument {
        private static final UUID REQUEST_ID = UUID.fromString("64fb1939-0ca7-432b-b7f4-3b53f7fc3789");

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
        void setsFieldsWithCurrentYearSpecified() {
            var year = Year.of(2023);
            var request = builder
                    .currentYear(year)
                    .numberOfYearsAgo(2)
                    .build()
                    .toMagdaDocument(REQUEST_ID, info);

            assertThat(request.getValue("//Criteria/Jaar"), is(equalTo(year.toString())));
            assertThat(request.getValue("//Criteria/Jaar/@Huidig"), is(equalTo("1")));
            assertThat(request.getValue("//Criteria/AantalJaarTerug"), is(equalTo("2")));
        }

        @Test
        void setsFieldsWithReferenceYearSpecified() {
            var year = Year.of(2023);
            var request = builder
                    .referenceYear(year)
                    .numberOfYearsAgo(2)
                    .build()
                    .toMagdaDocument(REQUEST_ID, info);

            assertThat(request.getValue("//Criteria/Jaar"), is(equalTo(year.toString())));
            assertThat(request.getValue("//Criteria/Jaar/@Huidig"), is(equalTo("0")));
            assertThat(request.getValue("//Criteria/AantalJaarTerug"), is(equalTo("2")));
        }

        @Test
        void doesNotSetNumberOfYearsAgoIfNotSpecified() {
            var year = Year.of(2023);
            var request = builder
                    .currentYear(year)
                    .build()
                    .toMagdaDocument(REQUEST_ID, info);

            assertThat(request.getValue("//Criteria/AantalJaarTerug"), is(nullValue()));
        }
    }
}
