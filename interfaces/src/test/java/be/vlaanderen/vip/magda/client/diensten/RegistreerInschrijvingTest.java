package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class RegistreerInschrijvingTest extends TestBase {

    private static final UUID REQUEST_ID = UUID.fromString("64fb1939-0ca7-432b-b7f4-3b53f7fc3789");
    private static final String VRAAG_INHOUD_INSCHRIJVING_IDENTIFICATIE = "//Vragen/Vraag/Inhoud/Inschrijving/Identificatie";
    private static final String VRAAG_INHOUD_INSCHRIJVING_HOEDANIGHEID = "//Vragen/Vraag/Inhoud/Inschrijving/Hoedanigheid";
    private static final String INSZ = "67021546719";

    @Test
    void fillsInRequestRegistreerInschrijving0200() {
        var start = LocalDate.of(2020, 8, 15);
        var end = LocalDate.of(2021, 9, 20);

        var request = RegistreerInschrijvingRequest.builder()
                .insz(INSZ)
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
            () -> assertThatXmlFieldIsEqualTo(requestDocument, VRAAG_INHOUD_INSCHRIJVING_IDENTIFICATIE, mockedMagdaRegistrationInfo.getIdentification()),
            () -> assertThatXmlHasNoFieldForPath(requestDocument, VRAAG_INHOUD_INSCHRIJVING_HOEDANIGHEID),
            () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Inschrijving/INSZ", INSZ),
            () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Inschrijving/Periode/Begin", "2020-08-15"),
            () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Inschrijving/Periode/Einde", "2021-09-20"));
    }

    @Test
    void fillsInRequestRegistreerInschrijving0201() {
        var request = RegistreerInschrijving0201Request.builder()
                .insz(INSZ)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(5))
                .build();

        var mockedMagdaRegistrationInfo = MagdaRegistrationInfo.builder()
                .identification(TEST_SERVICE_URI)
                .build();

        var requestDocument = request.toMagdaDocument(REQUEST_ID, mockedMagdaRegistrationInfo);

        log.debug("Request:  {}", requestDocument.toString());

        assertThatTechnicalFieldsInRequestMatchRequest(requestDocument, request, REQUEST_ID, mockedMagdaRegistrationInfo);
        assertThatXmlFieldIsEqualTo(requestDocument, VRAAG_INHOUD_INSCHRIJVING_IDENTIFICATIE, mockedMagdaRegistrationInfo.getIdentification());
        assertThatXmlHasNoFieldForPath(requestDocument, VRAAG_INHOUD_INSCHRIJVING_HOEDANIGHEID);
    }

    @Nested
    class Deprecated {

        @Test
        void fillsInRequestRegistreerInschrijving0200() {
            var start = LocalDate.of(2020, 8, 15);
            var end = LocalDate.of(2021, 9, 20);

            var request = RegistreerInschrijvingRequest.builder()
                    .insz(INSZ)
                    .startDate(start)
                    .endDate(end)
                    .build();

            var mockedMagdaRegistrationInfo = MagdaRegistrationInfo.builder()
                    .identification(TEST_SERVICE_URI)
                    .hoedanigheidscode(TEST_SERVICE_HOEDANIGHEID)
                    .build();

            var requestDocument = request.toMagdaDocument(REQUEST_ID, mockedMagdaRegistrationInfo);

            log.debug("Request:  {}", requestDocument.toString());

            var hoedanigheidscode = mockedMagdaRegistrationInfo.getHoedanigheidscode();
            assertTrue(hoedanigheidscode.isPresent());
            assertAll(
                    () -> assertThatTechnicalFieldsIncludingHoedanigheidInRequestMatchRequest(requestDocument, request, REQUEST_ID, mockedMagdaRegistrationInfo),
                    () -> assertThatXmlFieldIsEqualTo(requestDocument, VRAAG_INHOUD_INSCHRIJVING_IDENTIFICATIE, mockedMagdaRegistrationInfo.getIdentification()),
                    () -> assertThatXmlFieldIsEqualTo(requestDocument, VRAAG_INHOUD_INSCHRIJVING_HOEDANIGHEID, hoedanigheidscode.get()),
                    () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Inschrijving/INSZ", INSZ),
                    () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Inschrijving/Periode/Begin", "2020-08-15"),
                    () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Inschrijving/Periode/Einde", "2021-09-20"));
        }

        @Test
        void fillsInRequestRegistreerInschrijving0201() {
            var request = RegistreerInschrijving0201Request.builder()
                    .insz(INSZ)
                    .startDate(LocalDate.now())
                    .endDate(LocalDate.now().plusDays(5))
                    .build();

            var mockedMagdaRegistrationInfo = MagdaRegistrationInfo.builder()
                    .identification(TEST_SERVICE_URI)
                    .hoedanigheidscode(TEST_SERVICE_HOEDANIGHEID)
                    .build();

            var requestDocument = request.toMagdaDocument(REQUEST_ID, mockedMagdaRegistrationInfo);

            log.debug("Request:  {}", requestDocument.toString());

            assertThatTechnicalFieldsIncludingHoedanigheidInRequestMatchRequest(requestDocument, request, REQUEST_ID, mockedMagdaRegistrationInfo);
            assertThatXmlFieldIsEqualTo(requestDocument, VRAAG_INHOUD_INSCHRIJVING_IDENTIFICATIE, mockedMagdaRegistrationInfo.getIdentification());
            var hoedanigheidscode = mockedMagdaRegistrationInfo.getHoedanigheidscode();
            assertTrue(hoedanigheidscode.isPresent());
            assertThatXmlFieldIsEqualTo(requestDocument, VRAAG_INHOUD_INSCHRIJVING_HOEDANIGHEID, hoedanigheidscode.get());
        }
    }
}
