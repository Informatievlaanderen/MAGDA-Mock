package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
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

        MagdaRegistrationInfo mockedMagdaRegistrationInfo = MagdaRegistrationInfo.builder()
                .name(TEST_SERVICE_NAAM)
                .identification(TEST_SERVICE_URI)
                .build();

        aanvraag.fillIn(request, mockedMagdaRegistrationInfo);

        log.debug("Request:  {}", request.toString());

        assertAll(
                () -> assertThatTechnicalFieldsInRequestMatchAanvraag(request, aanvraag, mockedMagdaRegistrationInfo)
        );
    }

    @Nested
    class Deprecated {

        @Test
        void fillsInRequestGeefPasfoto0200() {
            var aanvraag = new GeefPasfotoAanvraag(INSZ);

            MagdaDocument request = MagdaDocument.fromTemplate(aanvraag);

            MagdaRegistrationInfo mockedMagdaRegistrationInfo = MagdaRegistrationInfo.builder()
                    .name(TEST_SERVICE_NAAM)
                    .identification(TEST_SERVICE_URI)
                    .hoedanigheidscode(TEST_SERVICE_HOEDANIGHEID)
                    .build();

            aanvraag.fillIn(request, mockedMagdaRegistrationInfo);

            log.debug("Request:  {}", request.toString());

            assertAll(
                    () -> assertThatTechnicalFieldsIncludingHoedanigheidInRequestMatchAanvraag(request, aanvraag, mockedMagdaRegistrationInfo)
            );
        }
    }
}
