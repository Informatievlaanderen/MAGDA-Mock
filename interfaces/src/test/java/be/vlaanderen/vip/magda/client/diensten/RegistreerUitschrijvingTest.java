package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.Aanvraag;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheid;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class RegistreerUitschrijvingTest extends TestBase {

    @Test
    void fillsInRequestRegistreerUitschrijving() {
        var aanvraag = new RegistreerUitschrijvingAanvraag("67021546719", LocalDate.now(), LocalDate.now().plusDays(5));

        MagdaDocument request = MagdaDocument.fromTemplate(aanvraag);

        MagdaHoedanigheid mockedMagdaHoedanigheid = new MagdaHoedanigheid(TEST_SERVICE_NAAM, TEST_SERVICE_URI, TEST_SERVICE_HOEDANIGHEID);

        aanvraag.fillIn(request, mockedMagdaHoedanigheid);

        log.debug("Request:  {}", request.toString());

        assertThatTechnicalFieldsInRequestMatchAanvraag(request, aanvraag,mockedMagdaHoedanigheid);
        assertThatXmlFieldIsEqualTo(request, "//Vragen/Vraag/Inhoud/Uitschrijving/Identificatie", TEST_SERVICE_URI);
        assertThatXmlFieldIsEqualTo(request, "//Vragen/Vraag/Inhoud/Uitschrijving/Hoedanigheid", TEST_SERVICE_HOEDANIGHEID);
    }

}
