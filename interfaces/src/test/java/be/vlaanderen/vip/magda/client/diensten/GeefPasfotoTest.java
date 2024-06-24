package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
class GeefPasfotoTest extends TestBase {
    private static final UUID REQUEST_ID = UUID.fromString("64fb1939-0ca7-432b-b7f4-3b53f7fc3789");
    private static final String INSZ = "67021546719";

    @Test
    void fillsInRequestGeefPasfoto0200() {
        var request = GeefPasfotoRequest.builder()
                .insz(INSZ)
                .build();

        var mockedMagdaRegistrationInfo = MagdaRegistrationInfo.builder()
                .identification(TEST_SERVICE_URI)
                .build();

        var requestDocument = request.toMagdaDocument(REQUEST_ID, mockedMagdaRegistrationInfo);

        log.debug("Request:  {}", requestDocument.toString());

        assertAll(
                () -> assertThatTechnicalFieldsInRequestMatchRequest(requestDocument, request, REQUEST_ID, mockedMagdaRegistrationInfo)
        );
    }

    @Nested
    class Deprecated {

        @Test
        void fillsInRequestGeefPasfoto0200() {
            var request = GeefPasfotoRequest.builder()
                    .insz(INSZ)
                    .build();

            var mockedMagdaRegistrationInfo = MagdaRegistrationInfo.builder()
                    .identification(TEST_SERVICE_URI)
                    .hoedanigheidscode(TEST_SERVICE_HOEDANIGHEID)
                    .build();

            var requestDocument = request.toMagdaDocument(REQUEST_ID, mockedMagdaRegistrationInfo);

            log.debug("Request:  {}", requestDocument.toString());

            assertAll(
                    () -> assertThatTechnicalFieldsIncludingHoedanigheidInRequestMatchRequest(requestDocument, request, REQUEST_ID, mockedMagdaRegistrationInfo)
            );
        }
    }
}
