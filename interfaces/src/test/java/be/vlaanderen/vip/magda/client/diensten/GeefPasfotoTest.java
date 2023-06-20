package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
class GeefPasfotoTest extends TestBase {
    private static final String INSZ = "67021546719";

    @Test
    void fillsInRequestGeefPasfoto0200() {
        var aanvraag = new GeefPasfotoAanvraag(INSZ);

        var mockedMagdaRegistrationInfo = MagdaRegistrationInfo.builder()
                .identification(TEST_SERVICE_URI)
                .build();

        var requestDocument = aanvraag.toMagdaDocument(mockedMagdaRegistrationInfo);

        log.debug("Request:  {}", requestDocument.toString());

        assertAll(
                () -> assertThatTechnicalFieldsInRequestMatchAanvraag(requestDocument, aanvraag, mockedMagdaRegistrationInfo)
        );
    }

    @Nested
    class Deprecated {

        @Test
        void fillsInRequestGeefPasfoto0200() {
            var aanvraag = new GeefPasfotoAanvraag(INSZ);

            var mockedMagdaRegistrationInfo = MagdaRegistrationInfo.builder()
                    .identification(TEST_SERVICE_URI)
                    .hoedanigheidscode(TEST_SERVICE_HOEDANIGHEID)
                    .build();

            var requestDocument = aanvraag.toMagdaDocument(mockedMagdaRegistrationInfo);

            log.debug("Request:  {}", requestDocument.toString());

            assertAll(
                    () -> assertThatTechnicalFieldsIncludingHoedanigheidInRequestMatchAanvraag(requestDocument, aanvraag, mockedMagdaRegistrationInfo)
            );
        }
    }
}
