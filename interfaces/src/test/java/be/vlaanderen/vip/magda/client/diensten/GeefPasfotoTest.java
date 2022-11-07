package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheid;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
public class GeefPasfotoTest extends TestBase {
    private static final String INSZ = "67021546719";

    @Test
    void fillsInRequestGeefPasfoto0200() {
        var aanvraag = new GeefPasfotoAanvraag(INSZ);

        MagdaDocument request = MagdaDocument.fromTemplate(aanvraag);

        MagdaHoedanigheid mockedMagdaHoedanigheid = new MagdaHoedanigheid(TEST_SERVICE_NAAM, TEST_SERVICE_URI, TEST_SERVICE_HOEDANIGHEID);

        aanvraag.fillIn(request, mockedMagdaHoedanigheid);

        log.debug("Request:  {}", request.toString());

        assertAll(
                () -> assertThatTechnicalFieldsInRequestMatchAanvraag(request, aanvraag, mockedMagdaHoedanigheid)
        );
    }


}
