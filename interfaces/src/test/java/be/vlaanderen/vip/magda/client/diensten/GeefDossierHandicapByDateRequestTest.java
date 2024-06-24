package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.diensten.GeefDossierHandicapByDateRequest.Builder;
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

class GeefDossierHandicapByDateRequestTest {

    @Nested
    class GeefDossierHandicapByDateRequestBuilder {

        @Test
        void buildsRequest() {
            var referenceDate = LocalDate.now();
            var sources = Set.of(
                    GeefDossierHandicapByDateRequest.HandicapAuthenticSourceType.DGPH,
                    GeefDossierHandicapByDateRequest.HandicapAuthenticSourceType.VSB
            );
            var parts = Set.of(
                    GeefDossierHandicapByDateRequest.HandicapFilePartType.RIGHTS,
                    GeefDossierHandicapByDateRequest.HandicapFilePartType.SOCIAL_CARDS
            );

            var request = GeefDossierHandicapByDateRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .referenceDate(referenceDate)
                    .sources(sources)
                    .parts(parts)
                    .build();

            assertEquals(INSZNumber.of(TestBase.TEST_INSZ), request.getInsz());
            assertEquals(referenceDate, request.getReferenceDate());
            assertEquals(sources, request.getSources());
            assertEquals(parts, request.getParts());
        }

        @Test
        void throwsException_whenInszNull() {
            var referenceDate = LocalDate.now();

            var builder = GeefDossierHandicapByDateRequest.builder()
                    .referenceDate(referenceDate);

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void throwsException_whenReferenceDateNull() {
            var builder = GeefDossierHandicapByDateRequest.builder()
                    .insz(TestBase.TEST_INSZ);

            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void sourcesIsOptional() {
            var referenceDate = LocalDate.now();

            var request = GeefDossierHandicapByDateRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .referenceDate(referenceDate)
                    .build();

            assertNull(request.getSources());
        }

        @Test
        void partsIsOptional() {
            var referenceDate = LocalDate.now();

            var request = GeefDossierHandicapByDateRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .referenceDate(referenceDate)
                    .build();

            assertNull(request.getParts());
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

            builder = GeefDossierHandicapByDateRequest.builder()
                    .insz(TestBase.TEST_INSZ)
                    .referenceDate(LocalDate.now());
        }

        @Test
        void setsFields() {
            var sources = Set.of(
                    GeefDossierHandicapByDateRequest.HandicapAuthenticSourceType.DGPH,
                    GeefDossierHandicapByDateRequest.HandicapAuthenticSourceType.VSB
            );
            var parts = Set.of(
                    GeefDossierHandicapByDateRequest.HandicapFilePartType.RIGHTS,
                    GeefDossierHandicapByDateRequest.HandicapFilePartType.SOCIAL_CARDS
            );


            var request = builder
                    .insz(TestBase.TEST_INSZ)
                    .referenceDate(LocalDate.of(2022, 01, 22))
                    .sources(sources)
                    .parts(parts)
                    .build()
                    .toMagdaDocument(REQUEST_ID, info);

            assertThat(request.getValue("//ConsultFilesByDateCriteria/ssin"), is(equalTo(TestBase.TEST_INSZ)));
            assertThat(request.getValue("//ConsultFilesByDateCriteria/referenceDate"), is(equalTo("2022-01-22")));
            assertThat(request.getValue("//ConsultFilesByDateCriteria/handicapAuthenticSources/DGPH"), is(equalTo("true")));
            assertThat(request.getValue("//ConsultFilesByDateCriteria/handicapAuthenticSources/VSB"), is(equalTo("true")));
            assertThat(request.getValue("//ConsultFilesByDateCriteria/handicapAuthenticSources/IrisCare"), is(equalTo("false")));
            assertThat(request.getValue("//ConsultFilesByDateCriteria/handicapAuthenticSources/NicCin"), is(equalTo("false")));
            assertThat(request.getValue("//ConsultFilesByDateCriteria/handicapAuthenticSources/AVIQ"), is(equalTo("false")));
            assertThat(request.getValue("//ConsultFilesByDateCriteria/handicapAuthenticSources/DSL"), is(equalTo("false")));
            assertThat(request.getValue("//ConsultFilesByDateCriteria/parts/evolutionOfRequest"), is(equalTo("false")));
            assertThat(request.getValue("//ConsultFilesByDateCriteria/parts/handicapRecognitions"), is(equalTo("false")));
            assertThat(request.getValue("//ConsultFilesByDateCriteria/parts/rights"), is(equalTo("true")));
            assertThat(request.getValue("//ConsultFilesByDateCriteria/parts/socialCards"), is(equalTo("true")));
        }

        @Test
        void doesNotSetSourcesOrPartsIfNotSpecified(){

            var request = builder
                    .insz(TestBase.TEST_INSZ)
                    .referenceDate(LocalDate.of(2022, 01, 22))
                    .build()
                    .toMagdaDocument(REQUEST_ID, info);

            assertThat(request.getValue("//ConsultFilesByDateCriteria/handicapAuthenticSources/DGPH"), is(equalTo("false")));
            assertThat(request.getValue("//ConsultFilesByDateCriteria/handicapAuthenticSources/VSB"), is(equalTo("false")));
            assertThat(request.getValue("//ConsultFilesByDateCriteria/handicapAuthenticSources/IrisCare"), is(equalTo("false")));
            assertThat(request.getValue("//ConsultFilesByDateCriteria/handicapAuthenticSources/NicCin"), is(equalTo("false")));
            assertThat(request.getValue("//ConsultFilesByDateCriteria/handicapAuthenticSources/AVIQ"), is(equalTo("false")));
            assertThat(request.getValue("//ConsultFilesByDateCriteria/handicapAuthenticSources/DSL"), is(equalTo("false")));
            assertThat(request.getValue("//ConsultFilesByDateCriteria/parts/evolutionOfRequest"), is(equalTo("false")));
            assertThat(request.getValue("//ConsultFilesByDateCriteria/parts/handicapRecognitions"), is(equalTo("false")));
            assertThat(request.getValue("//ConsultFilesByDateCriteria/parts/rights"), is(equalTo("false")));
            assertThat(request.getValue("//ConsultFilesByDateCriteria/parts/socialCards"), is(equalTo("false")));
        }

        @Test
        void doesRemoveConsultFilesByPeriodCriteriaElement(){

            var request = builder
                    .insz(TestBase.TEST_INSZ)
                    .referenceDate(LocalDate.of(2022, 01, 22))
                    .build()
                    .toMagdaDocument(REQUEST_ID, info);

            assertNull(request.getValue("//ConsultFilesByPeriodCriteria"));
        }
    }
}
