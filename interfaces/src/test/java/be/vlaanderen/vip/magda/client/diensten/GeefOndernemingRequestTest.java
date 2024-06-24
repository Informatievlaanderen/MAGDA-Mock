package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.diensten.GeefOndernemingRequest.Builder;
import be.vlaanderen.vip.magda.client.diensten.subject.KBONumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

class GeefOndernemingRequestTest {

    private final String kboNumber = "0242069537";

    @Nested
    class GeefOndernemingRequestBuilder {



        @Test
        void buildsRequest() {
            var startDate = LocalDate.of(2023, 1,18);
            var endDate = LocalDate.of(2024, 1,18);

            var request = GeefOndernemingRequest.builder()
                    .kboNumber(KBONumber.of(kboNumber))
                    .includeBasicData(true)
                    .includeJuridicalSituations(true)
                    .includeEstablishments(true)
                    .includeEstablishmentsDetails(true)
                    .includeDescriptions(true)
                    .startDate(startDate)
                    .endDate(endDate)
                    .build();

            assertEquals(KBONumber.of(kboNumber), request.getKboNumber());
            assertEquals(true, request.getIncludeBasicData());
            assertEquals(true, request.getIncludeJuridicalSituations());
            assertEquals(true, request.getIncludeEstablishments());
            assertEquals(true, request.getIncludeEstablishmentsDetails());
            assertEquals(true, request.getIncludeDescriptions());
            assertEquals(startDate, request.getStartDate());
            assertEquals(endDate, request.getEndDate());
        }

        @Test
        void throwsException_whenKboNumberIsNull() {
            var startDate = LocalDate.of(2023, 1,18);
            var endDate = LocalDate.of(2024, 1,18);

            var builder = GeefOndernemingRequest.builder()
                    .includeBasicData(true)
                    .includeJuridicalSituations(true)
                    .includeEstablishments(true)
                    .includeEstablishmentsDetails(true)
                    .includeDescriptions(true)
                    .startDate(startDate)
                    .endDate(endDate);
            assertThrows(IllegalStateException.class, builder::build);
        }

        @Test
        void fieldsOptional() {

            var request = GeefOndernemingRequest.builder()
                    .kboNumber(KBONumber.of(kboNumber))
                    .build();

            assertNull(request.getIncludeBasicData());
            assertNull(request.getIncludeJuridicalSituations());
            assertNull(request.getIncludeEstablishments());
            assertNull(request.getIncludeEstablishmentsDetails());
            assertNull(request.getIncludeDescriptions());
            assertNull(request.getStartDate());
            assertNull(request.getEndDate());
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

            builder = GeefOndernemingRequest.builder()
                    .kboNumber(KBONumber.of(kboNumber));
        }

        @Test
        void setsFields() {
            var startDate = LocalDate.of(2023, 1,18);
            var endDate = LocalDate.of(2024, 1,18);

            var request = builder
                    .includeBasicData(true)
                    .includeJuridicalSituations(true)
                    .includeEstablishments(true)
                    .includeEstablishmentsDetails(true)
                    .includeDescriptions(true)
                    .startDate(startDate)
                    .endDate(endDate)
                    .build()
                    .toMagdaDocument(REQUEST_ID, info);

            assertThat(request.getValue("//Criteria/Ondernemingsnummer"), is(equalTo(kboNumber)));
            assertThat(request.getValue("//Criteria/Basisgegevens"), is(equalTo("1")));
            assertThat(request.getValue("//Criteria/Rechtstoestanden"), is(equalTo("1")));
            assertThat(request.getValue("//Criteria/Vestigingen/Aanduiding"), is(equalTo("1")));
            assertThat(request.getValue("//Criteria/Vestigingen/Details"), is(equalTo("1")));
            assertThat(request.getValue("//Criteria/Omschrijvingen/Aanduiding"), is(equalTo("1")));
            assertThat(request.getValue("//Criteria/Datums/Periode/Begin"), is(equalTo("2023-01-18")));
            assertThat(request.getValue("//Criteria/Datums/Periode/Einde"), is(equalTo("2024-01-18")));
        }

        @Test
        void doesNotSetFieldsIfNotSpecified(){

            var request = builder
                    .build()
                    .toMagdaDocument(REQUEST_ID, info);
            assertNull(request.getValue("//Criteria/Basisgegevens"));
            assertNull(request.getValue("//Criteria/Rechtstoestanden"));
            assertNull(request.getValue("//Criteria/Vestigingen"));
            assertNull(request.getValue("//Criteria/Omschrijvingen"));
            assertNull(request.getValue("//Criteria/Datums"));
        }
    }
}
