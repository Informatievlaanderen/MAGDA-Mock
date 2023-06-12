package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
class GeefAanslagbiljetPersonenBelastingTest extends TestBase {

    @Test
    void aanvraagFillsInStandardRequestParameters() {

        var insz = RandomStringUtils.randomNumeric(11);
        var aanvraag = new GeefAanslagbiljetPersonenbelastingAanvraag(insz);

        var request = MagdaDocument.fromTemplate(aanvraag);

        var mockedMagdaRegistrationInfo = MagdaRegistrationInfo.builder()
                .identification(TEST_SERVICE_URI)
                .build();

        aanvraag.fillIn(request, mockedMagdaRegistrationInfo);

        log.debug("Request:  {}", request);

        assertAll(
                () -> assertThatTechnicalFieldsInRequestMatchAanvraag(request, aanvraag, mockedMagdaRegistrationInfo),
                () -> assertThatXmlFieldIsEqualTo(request, "//Vragen/Vraag/Inhoud/Criteria/INSZ", insz),

                // TODO: Should these values be passed as inputs in Aanvraag instead of hardcoded in the request template?
                () -> assertThatXmlFieldIsEqualTo(request, "//Vragen/Vraag/Inhoud/Criteria/Inkomensjaar", "2011"),
                () -> assertThatXmlFieldIsEqualTo(request, "//Vragen/Vraag/Inhoud/Criteria/IPCALCodes/IPCALCode[1]", "7555"),
                () -> assertThatXmlFieldIsEqualTo(request, "//Vragen/Vraag/Inhoud/Criteria/IPCALCodes/IPCALCode[2]", "7557"),
                () -> assertThatXmlFieldIsEqualTo(request, "//Vragen/Vraag/Inhoud/Bron", "FODFIN"),
                () -> assertThatXmlFieldIsEqualTo(request, "//Vragen/Vraag/Inhoud/Taal", "nl")
        );
    }

    @Nested
    class Deprecated {

        @Test
        void aanvraagFillsInStandardRequestParameters() {

            var insz = RandomStringUtils.randomNumeric(11) ;
            var aanvraag = new GeefAanslagbiljetPersonenbelastingAanvraag(insz);

            var request = MagdaDocument.fromTemplate(aanvraag);

            var mockedMagdaRegistrationInfo = MagdaRegistrationInfo.builder()
                    .identification(TEST_SERVICE_URI)
                    .hoedanigheidscode(TEST_SERVICE_HOEDANIGHEID)
                    .build();

            aanvraag.fillIn(request, mockedMagdaRegistrationInfo);

            log.debug("Request:  {}", request);

            assertAll(
                    () -> assertThatTechnicalFieldsIncludingHoedanigheidInRequestMatchAanvraag(request, aanvraag, mockedMagdaRegistrationInfo),
                    () -> assertThatXmlFieldIsEqualTo(request, "//Vragen/Vraag/Inhoud/Criteria/INSZ", insz),

                    // TODO: Should these values be passed as inputs in Aanvraag instead of hardcoded in the request template?
                    () -> assertThatXmlFieldIsEqualTo(request, "//Vragen/Vraag/Inhoud/Criteria/Inkomensjaar", "2011"),
                    () -> assertThatXmlFieldIsEqualTo(request, "//Vragen/Vraag/Inhoud/Criteria/IPCALCodes/IPCALCode[1]", "7555"),
                    () -> assertThatXmlFieldIsEqualTo(request, "//Vragen/Vraag/Inhoud/Criteria/IPCALCodes/IPCALCode[2]", "7557"),
                    () -> assertThatXmlFieldIsEqualTo(request, "//Vragen/Vraag/Inhoud/Bron", "FODFIN"),
                    () -> assertThatXmlFieldIsEqualTo(request, "//Vragen/Vraag/Inhoud/Taal", "nl")
            );
        }
    }
}
