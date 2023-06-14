package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
class RegistreerUitschrijvingTest extends TestBase {

    @Test
    void fillsInRequestRegistreerUitschrijving() {
        var start = LocalDate.of(2020, 8, 15);
        var end = LocalDate.of(2021, 9, 20);
        
        var aanvraag = new RegistreerUitschrijvingAanvraag("67021546719", start, end);

        var request = MagdaDocument.fromTemplate(aanvraag);

        var mockedMagdaRegistrationInfo = MagdaRegistrationInfo.builder()
                .identification(TEST_SERVICE_URI)
                .build();

        aanvraag.fillIn(request, mockedMagdaRegistrationInfo);

        log.debug("Request:  {}", request.toString());

        assertAll(
                () -> assertThatTechnicalFieldsInRequestMatchAanvraag(request, aanvraag, mockedMagdaRegistrationInfo),
                () -> assertThatXmlFieldIsEqualTo(request, "//Vragen/Vraag/Inhoud/Uitschrijving/Identificatie", TEST_SERVICE_URI),
                () -> assertThatXmlHasNoFieldForPath(request, "//Vragen/Vraag/Inhoud/Uitschrijving/Hoedanigheid"),
                () -> assertThatXmlFieldIsEqualTo(request, "//Vragen/Vraag/Inhoud/Uitschrijving/INSZ", "67021546719"),
                () -> assertThatXmlFieldIsEqualTo(request, "//Vragen/Vraag/Inhoud/Uitschrijving/Periode/Begin", "2020-08-15"),
                () -> assertThatXmlFieldIsEqualTo(request, "//Vragen/Vraag/Inhoud/Uitschrijving/Periode/Einde", "2021-09-20"));
    }

    @Test
    void dateFieldsOptional() {
        var aanvraag = new RegistreerUitschrijvingAanvraag("67021546719", null, null);
        
        var request = MagdaDocument.fromTemplate(aanvraag);

        var mockedMagdaHoedanigheid = MagdaRegistrationInfo.builder()
                .identification(TEST_SERVICE_URI)
                .build();

        aanvraag.fillIn(request, mockedMagdaHoedanigheid);

        log.debug("Request:  {}", request);

        assertAll(
                () -> assertThatXmlHasNoFieldForPath(request, "//Vragen/Vraag/Inhoud/Uitschrijving/Periode/Begin"),
                () -> assertThatXmlHasNoFieldForPath(request, "//Vragen/Vraag/Inhoud/Uitschrijving/Periode/Einde"));
    }

    @Nested
    class Deprecated {

        @Test
        void fillsInRequestRegistreerUitschrijving() {
            var start = LocalDate.of(2020, 8, 15);
            var end = LocalDate.of(2021, 9, 20);

            var aanvraag = new RegistreerUitschrijvingAanvraag("67021546719", start, end);

            var request = MagdaDocument.fromTemplate(aanvraag);

            var mockedMagdaRegistrationInfo = MagdaRegistrationInfo.builder()
                    .identification(TEST_SERVICE_URI)
                    .hoedanigheidscode(TEST_SERVICE_HOEDANIGHEID)
                    .build();

            aanvraag.fillIn(request, mockedMagdaRegistrationInfo);

            log.debug("Request:  {}", request.toString());

            assertAll(
                    () -> assertThatTechnicalFieldsIncludingHoedanigheidInRequestMatchAanvraag(request, aanvraag, mockedMagdaRegistrationInfo),
                    () -> assertThatXmlFieldIsEqualTo(request, "//Vragen/Vraag/Inhoud/Uitschrijving/Identificatie", TEST_SERVICE_URI),
                    () -> assertThatXmlFieldIsEqualTo(request, "//Vragen/Vraag/Inhoud/Uitschrijving/Hoedanigheid", TEST_SERVICE_HOEDANIGHEID),
                    () -> assertThatXmlFieldIsEqualTo(request, "//Vragen/Vraag/Inhoud/Uitschrijving/INSZ", "67021546719"),
                    () -> assertThatXmlFieldIsEqualTo(request, "//Vragen/Vraag/Inhoud/Uitschrijving/Periode/Begin", "2020-08-15"),
                    () -> assertThatXmlFieldIsEqualTo(request, "//Vragen/Vraag/Inhoud/Uitschrijving/Periode/Einde", "2021-09-20"));
        }
    }
}
