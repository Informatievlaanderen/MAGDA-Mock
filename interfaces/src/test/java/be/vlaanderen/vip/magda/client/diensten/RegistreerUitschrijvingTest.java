package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
class RegistreerUitschrijvingTest extends TestBase {
    private static final UUID REQUEST_ID = UUID.fromString("64fb1939-0ca7-432b-b7f4-3b53f7fc3789");

    @Test
    void fillsInRequestRegistreerUitschrijving() {
        var start = LocalDate.of(2020, 8, 15);
        var end = LocalDate.of(2021, 9, 20);
        
        var request = RegistreerUitschrijvingRequest.builder()
                .insz("67021546719")
                .startDate(start)
                .endDate(end)
                .build();

        var mockedMagdaRegistrationInfo = MagdaRegistrationInfo.builder()
                .identification(TEST_SERVICE_URI)
                .build();

        var requestDocument = request.toMagdaDocument(REQUEST_ID, mockedMagdaRegistrationInfo);

        log.debug("Request:  {}", requestDocument.toString());

        assertAll(
                () -> assertThatTechnicalFieldsInRequestMatchRequest(requestDocument, request, REQUEST_ID, mockedMagdaRegistrationInfo),
                () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Uitschrijving/Identificatie", TEST_SERVICE_URI),
                () -> assertThatXmlHasNoFieldForPath(requestDocument, "//Vragen/Vraag/Inhoud/Uitschrijving/Hoedanigheid"),
                () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Uitschrijving/INSZ", "67021546719"),
                () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Uitschrijving/Periode/Begin", "2020-08-15"),
                () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Uitschrijving/Periode/Einde", "2021-09-20"));
    }

    @Test
    void dateFieldsOptional() {
        var request = RegistreerUitschrijvingRequest.builder()
                .insz("67021546719")
                .build();

        var mockedMagdaRegistrationInfo = MagdaRegistrationInfo.builder()
                .identification(TEST_SERVICE_URI)
                .build();

        var requestDocument = request.toMagdaDocument(REQUEST_ID, mockedMagdaRegistrationInfo);

        log.debug("Request:  {}", requestDocument);

        assertAll(
                () -> assertThatXmlHasNoFieldForPath(requestDocument, "//Vragen/Vraag/Inhoud/Uitschrijving/Periode/Begin"),
                () -> assertThatXmlHasNoFieldForPath(requestDocument, "//Vragen/Vraag/Inhoud/Uitschrijving/Periode/Einde"));
    }

    @Nested
    class Deprecated {

        @Test
        void fillsInRequestRegistreerUitschrijving() {
            var start = LocalDate.of(2020, 8, 15);
            var end = LocalDate.of(2021, 9, 20);

            var request = RegistreerUitschrijvingRequest.builder()
                    .insz("67021546719")
                    .startDate(start)
                    .endDate(end)
                    .build();

            var mockedMagdaRegistrationInfo = MagdaRegistrationInfo.builder()
                    .identification(TEST_SERVICE_URI)
                    .hoedanigheidscode(TEST_SERVICE_HOEDANIGHEID)
                    .build();

            var requestDocument = request.toMagdaDocument(REQUEST_ID, mockedMagdaRegistrationInfo);

            log.debug("Request:  {}", requestDocument.toString());

            assertAll(
                    () -> assertThatTechnicalFieldsIncludingHoedanigheidInRequestMatchRequest(requestDocument, request, REQUEST_ID, mockedMagdaRegistrationInfo),
                    () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Uitschrijving/Identificatie", TEST_SERVICE_URI),
                    () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Uitschrijving/Hoedanigheid", TEST_SERVICE_HOEDANIGHEID),
                    () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Uitschrijving/INSZ", "67021546719"),
                    () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Uitschrijving/Periode/Begin", "2020-08-15"),
                    () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Uitschrijving/Periode/Einde", "2021-09-20"));
        }
    }
}
