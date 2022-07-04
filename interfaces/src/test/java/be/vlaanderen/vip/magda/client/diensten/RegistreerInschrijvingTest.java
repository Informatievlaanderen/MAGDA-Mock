package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheid;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

@Slf4j
public class RegistreerInschrijvingTest extends TestBase {

    private static final String VRAAG_INHOUD_INSCHRIJVING_IDENTIFICATIE = "//Vragen/Vraag/Inhoud/Inschrijving/Identificatie";
    private static final String VRAAG_INHOUD_INSCHRIJVING_HOEDANIGHEID = "//Vragen/Vraag/Inhoud/Inschrijving/Hoedanigheid";
    private static final String INSZ = "67021546719";

    @Test
    void fillsInRequestRegistreerInschrijving0200() {
        var aanvraag = new RegistreerInschrijvingAanvraag(INSZ, LocalDate.now(), LocalDate.now().plusDays(5));

        MagdaDocument request = MagdaDocument.fromTemplate(aanvraag);

        MagdaHoedanigheid mockedMagdaHoedanigheid = new MagdaHoedanigheid(TEST_SERVICE_NAAM, TEST_SERVICE_URI, TEST_SERVICE_HOEDANIGHEID);

        aanvraag.fillIn(request, mockedMagdaHoedanigheid);

        log.debug("Request:  {}", request.toString());

        assertThatTechnicalFieldsInRequestMatchAanvraag(request, aanvraag, mockedMagdaHoedanigheid);
        assertThatXmlFieldIsEqualTo(request, VRAAG_INHOUD_INSCHRIJVING_IDENTIFICATIE, mockedMagdaHoedanigheid.getUri());
        assertThatXmlFieldIsEqualTo(request, VRAAG_INHOUD_INSCHRIJVING_HOEDANIGHEID, mockedMagdaHoedanigheid.getHoedanigheid());
    }

    @Test
    void fillsInRequestRegistreerInschrijving0201() {
        var aanvraag = new RegistreerInschrijving0201Aanvraag(TypeInschrijving.PERSOON, INSZ, LocalDate.now(), LocalDate.now().plusDays(5));

        MagdaDocument request = MagdaDocument.fromTemplate(aanvraag);

        MagdaHoedanigheid mockedMagdaHoedanigheid = new MagdaHoedanigheid(TEST_SERVICE_NAAM, TEST_SERVICE_URI, TEST_SERVICE_HOEDANIGHEID);

        aanvraag.fillIn(request, mockedMagdaHoedanigheid);

        log.debug("Request:  {}", request.toString());

        assertThatTechnicalFieldsInRequestMatchAanvraag(request, aanvraag, mockedMagdaHoedanigheid);
        assertThatXmlFieldIsEqualTo(request, VRAAG_INHOUD_INSCHRIJVING_IDENTIFICATIE, mockedMagdaHoedanigheid.getUri());
        assertThatXmlFieldIsEqualTo(request, VRAAG_INHOUD_INSCHRIJVING_HOEDANIGHEID, mockedMagdaHoedanigheid.getHoedanigheid());
    }

}
