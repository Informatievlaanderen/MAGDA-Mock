package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
class GeefAanslagbiljetPersonenbelastingTest extends TestBase {

    @Test
    void requestFillsInStandardRequestParameters_withIpcalCodes() {

        var insz = RandomStringUtils.randomNumeric(11);
        var request = GeefAanslagbiljetPersonenbelastingRequest.builder2()
                .insz(insz)
                .incomeYear(Year.of(2021))
                .ipcalCodes(List.of("7001", "7002", "7003"))
                .build();

        var mockedMagdaRegistrationInfo = MagdaRegistrationInfo.builder()
                .identification(TEST_SERVICE_URI)
                .build();

        var requestDocument = request.toMagdaDocument(mockedMagdaRegistrationInfo);

        log.debug("Request:  {}", requestDocument);

        assertAll(
                () -> assertThatTechnicalFieldsInRequestMatchRequest(requestDocument, request, mockedMagdaRegistrationInfo),
                () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Criteria/INSZ", insz),
                () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Criteria/Inkomensjaar", "2021"),
                () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Criteria/IPCALCodes/IPCALCode[1]", "7001"),
                () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Criteria/IPCALCodes/IPCALCode[2]", "7002"),
                () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Criteria/IPCALCodes/IPCALCode[3]", "7003"),

                // TODO: Should these values be passed as inputs in MagdaRequest instead of hardcoded in the request template?
                () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Bron", "FODFIN"),
                () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Taal", "nl")
        );
    }

    @Test
    void requestFillsInStandardRequestParameters_withoutIpcalCodes() {

        var insz = RandomStringUtils.randomNumeric(11);
        var request = GeefAanslagbiljetPersonenbelastingRequest.builder2()
                .insz(insz)
                .incomeYear(Year.of(2021))
                .build();

        var mockedMagdaRegistrationInfo = MagdaRegistrationInfo.builder()
                .identification(TEST_SERVICE_URI)
                .build();

        var requestDocument = request.toMagdaDocument(mockedMagdaRegistrationInfo);

        log.debug("Request:  {}", requestDocument);

        assertAll(
                () -> assertThatTechnicalFieldsInRequestMatchRequest(requestDocument, request, mockedMagdaRegistrationInfo),
                () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Criteria/INSZ", insz),
                () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Criteria/Inkomensjaar", "2021"),
                () -> assertThatXmlHasNoFieldForPath(requestDocument, "//Vragen/Vraag/Inhoud/Criteria/IPCALCodes"),

                // TODO: Should these values be passed as inputs in MagdaRequest instead of hardcoded in the request template?
                () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Bron", "FODFIN"),
                () -> assertThatXmlFieldIsEqualTo(requestDocument, "//Vragen/Vraag/Inhoud/Taal", "nl")
        );
    }

    @Nested
    class Deprecated {

        @Test
        void requestFillsInStandardRequestParameters1() {

            var insz = RandomStringUtils.randomNumeric(11) ;
            var request = GeefAanslagbiljetPersonenbelastingRequest.builder()
                    .insz(insz)
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

        @Test
        void requestFillsInStandardRequestParameters2() {

            var insz = RandomStringUtils.randomNumeric(11);
            var request = GeefAanslagbiljetPersonenbelastingRequest.builder()
                    .insz(insz)
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
    }
}