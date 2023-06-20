package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class RegistreerInschrijvingTest extends TestBase {

    private static final String VRAAG_INHOUD_INSCHRIJVING_IDENTIFICATIE = "//Vragen/Vraag/Inhoud/Inschrijving/Identificatie";
    private static final String VRAAG_INHOUD_INSCHRIJVING_HOEDANIGHEID = "//Vragen/Vraag/Inhoud/Inschrijving/Hoedanigheid";
    private static final String INSZ = "67021546719";

    @Test
    void fillsInRequestRegistreerInschrijving0200() {
        var start = LocalDate.of(2020, 8, 15);
        var end = LocalDate.of(2021, 9, 20);
        
        var aanvraag = new RegistreerInschrijvingAanvraag(INSZ, start, end);

        var mockedMagdaRegistrationInfo = MagdaRegistrationInfo.builder()
                .identification(TEST_SERVICE_URI)
                .build();

        var requestDocument = aanvraag.toMagdaDocument(mockedMagdaRegistrationInfo);

        log.debug("Request:  {}", requestDocument.toString());

        assertAll(
            () -> assertThatTechnicalFieldsInRequestMatchAanvraag(requestDocument, aanvraag, mockedMagdaRegistrationInfo),
            () -> assertThatXmlFieldIsEqualTo(requestDocument, VRAAG_INHOUD_INSCHRIJVING_IDENTIFICATIE, mockedMagdaRegistrationInfo.getIdentification()),
            () -> assertThatXmlHasNoFieldForPath(requestDocument, VRAAG_INHOUD_INSCHRIJVING_HOEDANIGHEID),
            () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Inschrijving/INSZ", INSZ),
            () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Inschrijving/Periode/Begin", "2020-08-15"),
            () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Inschrijving/Periode/Einde", "2021-09-20"));
    }

    @Test
    void fillsInRequestRegistreerInschrijving0201() {
        var aanvraag = new RegistreerInschrijving0201Aanvraag(TypeInschrijving.PERSOON, INSZ, LocalDate.now(), LocalDate.now().plusDays(5));

        var mockedMagdaRegistrationInfo = MagdaRegistrationInfo.builder()
                .identification(TEST_SERVICE_URI)
                .build();

        var requestDocument = aanvraag.toMagdaDocument(mockedMagdaRegistrationInfo);

        log.debug("Request:  {}", requestDocument.toString());

        assertThatTechnicalFieldsInRequestMatchAanvraag(requestDocument, aanvraag, mockedMagdaRegistrationInfo);
        assertThatXmlFieldIsEqualTo(requestDocument, VRAAG_INHOUD_INSCHRIJVING_IDENTIFICATIE, mockedMagdaRegistrationInfo.getIdentification());
        assertThatXmlHasNoFieldForPath(requestDocument, VRAAG_INHOUD_INSCHRIJVING_HOEDANIGHEID);
    }

    @Nested
    class Deprecated {

        @Test
        void fillsInRequestRegistreerInschrijving0200() {
            var start = LocalDate.of(2020, 8, 15);
            var end = LocalDate.of(2021, 9, 20);

            var aanvraag = new RegistreerInschrijvingAanvraag(INSZ, start, end);

            var mockedMagdaRegistrationInfo = MagdaRegistrationInfo.builder()
                    .identification(TEST_SERVICE_URI)
                    .hoedanigheidscode(TEST_SERVICE_HOEDANIGHEID)
                    .build();

            var requestDocument = aanvraag.toMagdaDocument(mockedMagdaRegistrationInfo);

            log.debug("Request:  {}", requestDocument.toString());

            var hoedanigheidscode = mockedMagdaRegistrationInfo.getHoedanigheidscode();
            assertTrue(hoedanigheidscode.isPresent());
            assertAll(
                    () -> assertThatTechnicalFieldsIncludingHoedanigheidInRequestMatchAanvraag(requestDocument, aanvraag, mockedMagdaRegistrationInfo),
                    () -> assertThatXmlFieldIsEqualTo(requestDocument, VRAAG_INHOUD_INSCHRIJVING_IDENTIFICATIE, mockedMagdaRegistrationInfo.getIdentification()),
                    () -> assertThatXmlFieldIsEqualTo(requestDocument, VRAAG_INHOUD_INSCHRIJVING_HOEDANIGHEID, hoedanigheidscode.get()),
                    () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Inschrijving/INSZ", INSZ),
                    () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Inschrijving/Periode/Begin", "2020-08-15"),
                    () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Inschrijving/Periode/Einde", "2021-09-20"));
        }

        @Test
        void fillsInRequestRegistreerInschrijving0201() {
            var aanvraag = new RegistreerInschrijving0201Aanvraag(TypeInschrijving.PERSOON, INSZ, LocalDate.now(), LocalDate.now().plusDays(5));

            var mockedMagdaRegistrationInfo = MagdaRegistrationInfo.builder()
                    .identification(TEST_SERVICE_URI)
                    .hoedanigheidscode(TEST_SERVICE_HOEDANIGHEID)
                    .build();

            var requestDocument = aanvraag.toMagdaDocument(mockedMagdaRegistrationInfo);

            log.debug("Request:  {}", requestDocument.toString());

            assertThatTechnicalFieldsIncludingHoedanigheidInRequestMatchAanvraag(requestDocument, aanvraag, mockedMagdaRegistrationInfo);
            assertThatXmlFieldIsEqualTo(requestDocument, VRAAG_INHOUD_INSCHRIJVING_IDENTIFICATIE, mockedMagdaRegistrationInfo.getIdentification());
            var hoedanigheidscode = mockedMagdaRegistrationInfo.getHoedanigheidscode();
            assertTrue(hoedanigheidscode.isPresent());
            assertThatXmlFieldIsEqualTo(requestDocument, VRAAG_INHOUD_INSCHRIJVING_HOEDANIGHEID, hoedanigheidscode.get());
        }
    }
}
