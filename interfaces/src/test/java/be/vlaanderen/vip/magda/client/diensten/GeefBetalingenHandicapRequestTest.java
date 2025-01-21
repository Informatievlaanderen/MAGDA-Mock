package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.diensten.GeefBetalingenHandicapRequest.Builder;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

class GeefBetalingenHandicapRequestTest {

    @Nested
    class GeefBetalingenHandicapRequestBuilder {

        @Test
        void buildsRequest() {
            var startDate = LocalDate.now().minusYears(1);
            var endDate = LocalDate.now();
            var sources = Set.of(
                    GeefBetalingenHandicapRequest.HandicapAuthenticSourceType.DGPH,
                    GeefBetalingenHandicapRequest.HandicapAuthenticSourceType.VSB
            );

            var request = GeefBetalingenHandicapRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .startDate(startDate)
                    .endDate(endDate)
                    .sources(sources)
                    .build();

            assertEquals(INSZNumber.of(TestBase.TEST_INSZ), request.getInsz());
            assertEquals(startDate, request.getStartDate());
            assertEquals(endDate, request.getEndDate());
            assertEquals(sources, request.getSources());
        }

        @Test
        void throwsException_whenInszNull() {
            var startDate = LocalDate.now().minusYears(1);
            var endDate = LocalDate.now();

            var builder = GeefBetalingenHandicapRequest.builder()
                    .startDate(startDate)
                    .endDate(endDate);

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void throwsException_whenStartDateNull() {
            var endDate = LocalDate.now();
            var builder = GeefBetalingenHandicapRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .endDate(endDate);

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void throwsException_whenEndDateNull() {
            var startDate = LocalDate.now().minusYears(1);
            var builder = GeefBetalingenHandicapRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .startDate(startDate);

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void sourcesIsOptional() {
            var startDate = LocalDate.now().minusYears(1);
            var endDate = LocalDate.now();

            var request = GeefBetalingenHandicapRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .startDate(startDate)
                    .endDate(endDate)
                    .build();

            assertNull(request.getSources());
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

            builder = GeefBetalingenHandicapRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .startDate(LocalDate.now().minusYears(1))
                    .endDate(LocalDate.now());
        }

        @Test
        void setsFields() {
            var sources = Set.of(
                    GeefBetalingenHandicapRequest.HandicapAuthenticSourceType.DGPH,
                    GeefBetalingenHandicapRequest.HandicapAuthenticSourceType.VSB
            );

            var request = builder
                    .insz(TestBase.TEST_INSZ)
                    .startDate(LocalDate.of(2022, 01, 22))
                    .endDate(LocalDate.of(2023, 05, 16))
                    .sources(sources)
                    .build()
                    .toMagdaDocument(REQUEST_ID, info);

            assertThat(request.getValue("//ConsultPaymentsCriteria/ssin"), is(equalTo(TestBase.TEST_INSZ)));
            assertThat(request.getValue("//ConsultPaymentsCriteria/period/beginDate"), is(equalTo("2022-01-22")));
            assertThat(request.getValue("//ConsultPaymentsCriteria/period/endDate"), is(equalTo("2023-05-16")));
            assertThat(request.getValue("//ConsultPaymentsCriteria/handicapAuthenticSources/DGPH"), is(equalTo("true")));
            assertThat(request.getValue("//ConsultPaymentsCriteria/handicapAuthenticSources/VSB"), is(equalTo("true")));
            assertThat(request.getValue("//ConsultPaymentsCriteria/handicapAuthenticSources/IrisCare"), is(equalTo("false")));
            assertThat(request.getValue("//ConsultPaymentsCriteria/handicapAuthenticSources/NicCin"), is(equalTo("false")));
        }

        @Test
        void doesNotSetSourcesIfNotSpecified(){

            var request = builder
                    .insz(TestBase.TEST_INSZ)
                    .startDate(LocalDate.of(2022, 01, 22))
                    .endDate(LocalDate.of(2023, 05, 16))
                    .build()
                    .toMagdaDocument(REQUEST_ID, info);

            assertThat(request.getValue("//ConsultPaymentsCriteria/handicapAuthenticSources/DGPH"), is(equalTo("false")));
            assertThat(request.getValue("//ConsultPaymentsCriteria/handicapAuthenticSources/VSB"), is(equalTo("false")));
            assertThat(request.getValue("//ConsultPaymentsCriteria/handicapAuthenticSources/IrisCare"), is(equalTo("false")));
            assertThat(request.getValue("//ConsultPaymentsCriteria/handicapAuthenticSources/NicCin"), is(equalTo("false")));
        }
    }
}
