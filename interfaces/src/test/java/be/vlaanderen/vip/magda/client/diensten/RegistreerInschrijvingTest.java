package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
public class RegistreerInschrijvingTest extends TestBase {

    private static final String VRAAG_INHOUD_INSCHRIJVING_IDENTIFICATIE = "//Vragen/Vraag/Inhoud/Inschrijving/Identificatie";
    private static final String VRAAG_INHOUD_INSCHRIJVING_HOEDANIGHEID = "//Vragen/Vraag/Inhoud/Inschrijving/Hoedanigheid";
    private static final String INSZ = "67021546719";

    @Test
    void fillsInRequestRegistreerInschrijving0200() {
        var start = LocalDate.of(2020, 8, 15);
        var end = LocalDate.of(2021, 9, 20);
        
        var aanvraag = new RegistreerInschrijvingAanvraag(INSZ, start, end);

        MagdaDocument request = MagdaDocument.fromTemplate(aanvraag);

        var mockedMagdaHoedanigheid = MagdaRegistrationInfo.builder()
                .identification(TEST_SERVICE_URI)
                .build();

        aanvraag.fillIn(request, mockedMagdaHoedanigheid);

        log.debug("Request:  {}", request.toString());

        assertAll(
            () -> assertThatTechnicalFieldsInRequestMatchAanvraag(request, aanvraag, mockedMagdaHoedanigheid),
            () -> assertThatXmlFieldIsEqualTo(request, VRAAG_INHOUD_INSCHRIJVING_IDENTIFICATIE, mockedMagdaHoedanigheid.getIdentification()),
            () -> assertThatXmlHasNoFieldForPath(request, VRAAG_INHOUD_INSCHRIJVING_HOEDANIGHEID),
            () -> assertThatXmlFieldIsEqualTo(request, "//Vragen/Vraag/Inhoud/Inschrijving/INSZ", INSZ),
            () -> assertThatXmlFieldIsEqualTo(request, "//Vragen/Vraag/Inhoud/Inschrijving/Periode/Begin", "2020-08-15"),
            () -> assertThatXmlFieldIsEqualTo(request, "//Vragen/Vraag/Inhoud/Inschrijving/Periode/Einde", "2021-09-20"));
    }

    @Test
    void fillsInRequestRegistreerInschrijving0201() {
        var aanvraag = new RegistreerInschrijving0201Aanvraag(TypeInschrijving.PERSOON, INSZ, LocalDate.now(), LocalDate.now().plusDays(5));

        MagdaDocument request = MagdaDocument.fromTemplate(aanvraag);

        MagdaRegistrationInfo mockedMagdaRegistrationInfo = MagdaRegistrationInfo.builder()
                .identification(TEST_SERVICE_URI)
                .build();

        aanvraag.fillIn(request, mockedMagdaRegistrationInfo);

        log.debug("Request:  {}", request.toString());

        assertThatTechnicalFieldsInRequestMatchAanvraag(request, aanvraag, mockedMagdaRegistrationInfo);
        assertThatXmlFieldIsEqualTo(request, VRAAG_INHOUD_INSCHRIJVING_IDENTIFICATIE, mockedMagdaRegistrationInfo.getIdentification());
        assertThatXmlHasNoFieldForPath(request, VRAAG_INHOUD_INSCHRIJVING_HOEDANIGHEID);
    }

    @Nested
    class Deprecated {

        @Test
        void fillsInRequestRegistreerInschrijving0200() {
            var start = LocalDate.of(2020, 8, 15);
            var end = LocalDate.of(2021, 9, 20);

            var aanvraag = new RegistreerInschrijvingAanvraag(INSZ, start, end);

            MagdaDocument request = MagdaDocument.fromTemplate(aanvraag);

            var mockedMagdaHoedanigheid = MagdaRegistrationInfo.builder()
                    .identification(TEST_SERVICE_URI)
                    .hoedanigheidscode(TEST_SERVICE_HOEDANIGHEID)
                    .build();

            aanvraag.fillIn(request, mockedMagdaHoedanigheid);

            log.debug("Request:  {}", request.toString());

            assertAll(
                    () -> assertThatTechnicalFieldsIncludingHoedanigheidInRequestMatchAanvraag(request, aanvraag, mockedMagdaHoedanigheid),
                    () -> assertThatXmlFieldIsEqualTo(request, VRAAG_INHOUD_INSCHRIJVING_IDENTIFICATIE, mockedMagdaHoedanigheid.getIdentification()),
                    () -> assertThatXmlFieldIsEqualTo(request, VRAAG_INHOUD_INSCHRIJVING_HOEDANIGHEID, mockedMagdaHoedanigheid.getHoedanigheidscode()),
                    () -> assertThatXmlFieldIsEqualTo(request, "//Vragen/Vraag/Inhoud/Inschrijving/INSZ", INSZ),
                    () -> assertThatXmlFieldIsEqualTo(request, "//Vragen/Vraag/Inhoud/Inschrijving/Periode/Begin", "2020-08-15"),
                    () -> assertThatXmlFieldIsEqualTo(request, "//Vragen/Vraag/Inhoud/Inschrijving/Periode/Einde", "2021-09-20"));
        }

        @Test
        void fillsInRequestRegistreerInschrijving0201() {
            var aanvraag = new RegistreerInschrijving0201Aanvraag(TypeInschrijving.PERSOON, INSZ, LocalDate.now(), LocalDate.now().plusDays(5));

            MagdaDocument request = MagdaDocument.fromTemplate(aanvraag);

            MagdaRegistrationInfo mockedMagdaRegistrationInfo = MagdaRegistrationInfo.builder()
                    .identification(TEST_SERVICE_URI)
                    .hoedanigheidscode(TEST_SERVICE_HOEDANIGHEID)
                    .build();

            aanvraag.fillIn(request, mockedMagdaRegistrationInfo);

            log.debug("Request:  {}", request.toString());

            assertThatTechnicalFieldsIncludingHoedanigheidInRequestMatchAanvraag(request, aanvraag, mockedMagdaRegistrationInfo);
            assertThatXmlFieldIsEqualTo(request, VRAAG_INHOUD_INSCHRIJVING_IDENTIFICATIE, mockedMagdaRegistrationInfo.getIdentification());
            assertThatXmlFieldIsEqualTo(request, VRAAG_INHOUD_INSCHRIJVING_HOEDANIGHEID, mockedMagdaRegistrationInfo.getHoedanigheidscode());
        }
    }
}
