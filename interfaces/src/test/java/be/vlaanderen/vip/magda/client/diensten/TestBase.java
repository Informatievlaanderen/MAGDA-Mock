package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaRequest;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestBase {

    public static final String TEST_INSZ = "67722499797";
    public static final String TEST_KBO_NUMBER = "0456123791";
    public static final String TEST_SERVICE_URI = "be.vlaanderen/magda/mock.service";
    public static final String TEST_SERVICE_HOEDANIGHEID = "123";

    public static final String VRAAG_REFERTE = "//Vragen/Vraag/Referte";
    public static final String AFZENDER_REFERTE = "//Bericht/Afzender/Referte";
    public static final String AFZENDER_IDENTIFICATIE = "//Bericht/Afzender/Identificatie";
    public static final String AFZENDER_HOEDANIGHEID = "//Bericht/Afzender/Hoedanigheid";

    protected void assertThatXmlFieldIsEqualTo(MagdaDocument doc, String xmlPath, String expected) {
        var value = doc.getValue(xmlPath);
        assertThat(value).isNotNull();
        assertThat(value).isEqualTo(expected);
    }

    protected void assertThatXmlHasNoFieldForPath(MagdaDocument doc, String xmlPath) {
        var nodes = doc.xpath(xmlPath);
        assertThat(nodes.getLength()).isZero();
    }

    protected void assertThatTechnicalFieldsInRequestMatchRequest(MagdaDocument doc, MagdaRequest magdaRequest, UUID requestId, MagdaRegistrationInfo hoedanigheid) {
        assertThatXmlFieldIsEqualTo(doc, AFZENDER_REFERTE, requestId.toString());
        assertThatXmlFieldIsEqualTo(doc, AFZENDER_IDENTIFICATIE, hoedanigheid.getIdentification());
        assertThatXmlHasNoFieldForPath(doc, AFZENDER_HOEDANIGHEID);
        assertThatXmlFieldIsEqualTo(doc, VRAAG_REFERTE, requestId.toString());
    }

    protected void assertThatTechnicalFieldsIncludingHoedanigheidInRequestMatchRequest(MagdaDocument doc, MagdaRequest magdaRequest, UUID requestId, MagdaRegistrationInfo hoedanigheid) {
        assertThatXmlFieldIsEqualTo(doc, AFZENDER_REFERTE, requestId.toString());
        assertThatXmlFieldIsEqualTo(doc, AFZENDER_IDENTIFICATIE, hoedanigheid.getIdentification());
        var hoedanigheidscode = hoedanigheid.getHoedanigheidscode();
        assertTrue(hoedanigheidscode.isPresent());
        assertThatXmlFieldIsEqualTo(doc, AFZENDER_HOEDANIGHEID, hoedanigheidscode.get());
        assertThatXmlFieldIsEqualTo(doc, VRAAG_REFERTE, requestId.toString());
    }
}