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

class GeefLoopbaanARZARequestTest {

    @Nested
    class GeefLoopbaanARZARequestBuilder {

        @Test
        void buildsRequest() {
            var startDate = LocalDate.of(2023, 1,1);
            var endDate = LocalDate.of(2023,12,31);
            var request = GeefLoopbaanARZARequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .startDate(startDate)
                    .endDate(endDate)
                    .build();
            assertEquals(INSZNumber.of(TestBase.TEST_INSZ), request.getInsz());
            assertEquals(startDate, request.getStartDate());
            assertEquals(endDate, request.getEndDate());
        }

        @Test
        void throwsException_whenInszNull() {
            var startDate = LocalDate.of(2023, 1,1);
            var endDate = LocalDate.of(2023,12,31);
            var builder = GeefLoopbaanARZARequest.builder()
                    .startDate(startDate)
                    .endDate(endDate);
            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void throwsException_whenStartDateNull() {
            var endDate = LocalDate.of(2023,12,31);
            var builder = GeefLoopbaanARZARequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .endDate(endDate);
            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void endDateIsOptional() {
            var startDate = LocalDate.of(2023, 1,1);
            var request = GeefLoopbaanARZARequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .startDate(startDate)
                    .build();
            assertNull(request.getEndDate());
        }
    }

    @Nested
    class ToMagdaDocument {
        private MagdaRegistrationInfo info;
        private GeefLoopbaanARZARequest.Builder builder;

        @BeforeEach
        void setup() {
            info = MagdaRegistrationInfo.builder()
                    .identification("identification")
                    .hoedanigheidscode("identification")
                    .build();
            builder = GeefLoopbaanARZARequest.builder()
                    .insz(TestBase.TEST_INSZ);
        }

        @Test
        void setsFields() {

            var request = builder
                    .insz(TestBase.TEST_INSZ)
                    .startDate(LocalDate.of(2023, 1,1))
                    .endDate(LocalDate.of(2023,12,31))
                    .build()
                    .toMagdaDocument(info);
            assertThat(request.getValue("//Criteria/INSZ"), is(equalTo(TestBase.TEST_INSZ)));
            assertThat(request.getValue("//Criteria/Periode/Begin"), is(equalTo("2023-01-01")));
            assertThat(request.getValue("//Criteria/Periode/Einde"), is(equalTo("2023-12-31")));
        }

        @Test
        void doesNotSetEndDateIfNotSpecified() {
            var request = builder
                    .insz(TestBase.TEST_INSZ)
                    .startDate(LocalDate.of(2023, 1,1))
                    .build()
                    .toMagdaDocument(info);

            assertThat(request.getValue("//Criteria/Periode/Einde"), is(nullValue()));
        }
    }

}