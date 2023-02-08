package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheid;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
public class GeefPasfotoTest extends TestBase {
    private static final String INSZ = "67021546719";

    @Test
    void fillsInRequestGeefPasfoto0200() {
        var aanvraag = new GeefPasfotoAanvraag(INSZ);

        MagdaDocument request = MagdaDocument.fromTemplate(aanvraag);

        MagdaHoedanigheid mockedMagdaHoedanigheid = MagdaHoedanigheid.builder()
                .naam(TEST_SERVICE_NAAM)
                .uri(TEST_SERVICE_URI)
                .build();

        aanvraag.fillIn(request, mockedMagdaHoedanigheid);

        log.debug("Request:  {}", request.toString());

        assertAll(
                () -> assertThatTechnicalFieldsInRequestMatchAanvraag(request, aanvraag, mockedMagdaHoedanigheid)
        );
    }

    @Nested
    class Deprecated {

        @Test
        void fillsInRequestGeefPasfoto0200() {
            var aanvraag = new GeefPasfotoAanvraag(INSZ);

            MagdaDocument request = MagdaDocument.fromTemplate(aanvraag);

            MagdaHoedanigheid mockedMagdaHoedanigheid = MagdaHoedanigheid.builder()
                    .naam(TEST_SERVICE_NAAM)
                    .uri(TEST_SERVICE_URI)
                    .hoedanigheid(TEST_SERVICE_HOEDANIGHEID)
                    .build();

            aanvraag.fillIn(request, mockedMagdaHoedanigheid);

            log.debug("Request:  {}", request.toString());

            assertAll(
                    () -> assertThatTechnicalFieldsIncludingHoedanigheidInRequestMatchAanvraag(request, aanvraag, mockedMagdaHoedanigheid)
            );
        }
    }
}
