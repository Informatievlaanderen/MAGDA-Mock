package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
class GeefAanslagbiljetPersonenBelastingTest extends TestBase {

    @Test
    void requestFillsInStandardRequestParameters() {

        var insz = RandomStringUtils.randomNumeric(11);
        var request = GeefAanslagbiljetPersonenbelastingRequest.builder()
                .subjectInsz(insz)
                .build();

        var mockedMagdaRegistrationInfo = MagdaRegistrationInfo.builder()
                .identification(TEST_SERVICE_URI)
                .build();

        var requestDocument = request.toMagdaDocument(mockedMagdaRegistrationInfo);

        log.debug("Request:  {}", requestDocument);

        assertAll(
                () -> assertThatTechnicalFieldsInRequestMatchRequest(requestDocument, request, mockedMagdaRegistrationInfo),
                () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Criteria/INSZ", insz),

                // TODO: Should these values be passed as inputs in MagdaRequest instead of hardcoded in the request template?
                () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Criteria/Inkomensjaar", "2011"),
                () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Criteria/IPCALCodes/IPCALCode[1]", "7555"),
                () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Criteria/IPCALCodes/IPCALCode[2]", "7557"),
                () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Bron", "FODFIN"),
                () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Taal", "nl")
        );
    }

    @Nested
    class Deprecated {

        @Test
        void requestFillsInStandardRequestParameters() {

            var insz = RandomStringUtils.randomNumeric(11) ;
            var request = GeefAanslagbiljetPersonenbelastingRequest.builder()
                    .subjectInsz(insz)
                    .build();

            var mockedMagdaRegistrationInfo = MagdaRegistrationInfo.builder()
                    .identification(TEST_SERVICE_URI)
                    .hoedanigheidscode(TEST_SERVICE_HOEDANIGHEID)
                    .build();

            var requestDocument = request.toMagdaDocument(mockedMagdaRegistrationInfo);

            log.debug("Request:  {}", requestDocument);

            assertAll(
                    () -> assertThatTechnicalFieldsIncludingHoedanigheidInRequestMatchRequest(requestDocument, request, mockedMagdaRegistrationInfo),
                    () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Criteria/INSZ", insz),

                    // TODO: Should these values be passed as inputs in MagdaRequest instead of hardcoded in the request template?
                    () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Criteria/Inkomensjaar", "2011"),
                    () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Criteria/IPCALCodes/IPCALCode[1]", "7555"),
                    () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Criteria/IPCALCodes/IPCALCode[2]", "7557"),
                    () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Bron", "FODFIN"),
                    () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Taal", "nl")
            );
        }
    }
}