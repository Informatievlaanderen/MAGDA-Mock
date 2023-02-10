package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheid;
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

        var mockedMagdaHoedanigheid = MagdaHoedanigheid.builder()
                .naam(TEST_SERVICE_NAAM)
                .uri(TEST_SERVICE_URI)
                .build();

        aanvraag.fillIn(request, mockedMagdaHoedanigheid);

        log.debug("Request:  {}", request.toString());

        assertAll(
            () -> assertThatTechnicalFieldsInRequestMatchAanvraag(request, aanvraag, mockedMagdaHoedanigheid),
            () -> assertThatXmlFieldIsEqualTo(request, VRAAG_INHOUD_INSCHRIJVING_IDENTIFICATIE, mockedMagdaHoedanigheid.getUri()),
            () -> assertThatXmlHasNoFieldForPath(request, VRAAG_INHOUD_INSCHRIJVING_HOEDANIGHEID),
            () -> assertThatXmlFieldIsEqualTo(request, "//Vragen/Vraag/Inhoud/Inschrijving/INSZ", INSZ),
            () -> assertThatXmlFieldIsEqualTo(request, "//Vragen/Vraag/Inhoud/Inschrijving/Periode/Begin", "2020-08-15"),
            () -> assertThatXmlFieldIsEqualTo(request, "//Vragen/Vraag/Inhoud/Inschrijving/Periode/Einde", "2021-09-20"));
    }

    @Test
    void fillsInRequestRegistreerInschrijving0201() {
        var aanvraag = new RegistreerInschrijving0201Aanvraag(TypeInschrijving.PERSOON, INSZ, LocalDate.now(), LocalDate.now().plusDays(5));

        MagdaDocument request = MagdaDocument.fromTemplate(aanvraag);

        MagdaHoedanigheid mockedMagdaHoedanigheid = MagdaHoedanigheid.builder()
                .naam(TEST_SERVICE_NAAM)
                .uri(TEST_SERVICE_URI)
                .build();

        aanvraag.fillIn(request, mockedMagdaHoedanigheid);

        log.debug("Request:  {}", request.toString());

        assertThatTechnicalFieldsInRequestMatchAanvraag(request, aanvraag, mockedMagdaHoedanigheid);
        assertThatXmlFieldIsEqualTo(request, VRAAG_INHOUD_INSCHRIJVING_IDENTIFICATIE, mockedMagdaHoedanigheid.getUri());
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

            var mockedMagdaHoedanigheid = MagdaHoedanigheid.builder()
                    .naam(TEST_SERVICE_NAAM)
                    .uri(TEST_SERVICE_URI)
                    .hoedanigheid(TEST_SERVICE_HOEDANIGHEID)
                    .build();

            aanvraag.fillIn(request, mockedMagdaHoedanigheid);

            log.debug("Request:  {}", request.toString());

            assertAll(
                    () -> assertThatTechnicalFieldsIncludingHoedanigheidInRequestMatchAanvraag(request, aanvraag, mockedMagdaHoedanigheid),
                    () -> assertThatXmlFieldIsEqualTo(request, VRAAG_INHOUD_INSCHRIJVING_IDENTIFICATIE, mockedMagdaHoedanigheid.getUri()),
                    () -> assertThatXmlFieldIsEqualTo(request, VRAAG_INHOUD_INSCHRIJVING_HOEDANIGHEID, mockedMagdaHoedanigheid.getHoedanigheid()),
                    () -> assertThatXmlFieldIsEqualTo(request, "//Vragen/Vraag/Inhoud/Inschrijving/INSZ", INSZ),
                    () -> assertThatXmlFieldIsEqualTo(request, "//Vragen/Vraag/Inhoud/Inschrijving/Periode/Begin", "2020-08-15"),
                    () -> assertThatXmlFieldIsEqualTo(request, "//Vragen/Vraag/Inhoud/Inschrijving/Periode/Einde", "2021-09-20"));
        }

        @Test
        void fillsInRequestRegistreerInschrijving0201() {
            var aanvraag = new RegistreerInschrijving0201Aanvraag(TypeInschrijving.PERSOON, INSZ, LocalDate.now(), LocalDate.now().plusDays(5));

            MagdaDocument request = MagdaDocument.fromTemplate(aanvraag);

            MagdaHoedanigheid mockedMagdaHoedanigheid = MagdaHoedanigheid.builder()
                    .naam(TEST_SERVICE_NAAM)
                    .uri(TEST_SERVICE_URI)
                    .hoedanigheid(TEST_SERVICE_HOEDANIGHEID)
                    .build();

            aanvraag.fillIn(request, mockedMagdaHoedanigheid);

            log.debug("Request:  {}", request.toString());

            assertThatTechnicalFieldsIncludingHoedanigheidInRequestMatchAanvraag(request, aanvraag, mockedMagdaHoedanigheid);
            assertThatXmlFieldIsEqualTo(request, VRAAG_INHOUD_INSCHRIJVING_IDENTIFICATIE, mockedMagdaHoedanigheid.getUri());
            assertThatXmlFieldIsEqualTo(request, VRAAG_INHOUD_INSCHRIJVING_HOEDANIGHEID, mockedMagdaHoedanigheid.getHoedanigheid());
        }
    }
}
