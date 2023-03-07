package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.Aanvraag;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;

import static org.assertj.core.api.Assertions.assertThat;

public class TestBase {

    public static final String TEST_SERVICE_NAAM = "Magda Mock";
    public static final String TEST_SERVICE_URI = "be.vlaanderen/magda/mock.service";
    public static final String TEST_SERVICE_HOEDANIGHEID = "123";

    public static final String VRAAG_REFERTE = "//Vragen/Vraag/Referte";
    public static final String AFZENDER_REFERTE = "//Bericht/Afzender/Referte";
    public static final String AFZENDER_IDENTIFICATIE = "//Bericht/Afzender/Identificatie";
    public static final String AFZENDER_HOEDANIGHEID = "//Bericht/Afzender/Hoedanigheid";

    protected void assertThatXmlFieldIsEqualTo(MagdaDocument doc, String xmlPath, String expected) {
        String value = doc.getValue(xmlPath);
        assertThat(value).isNotNull();
        assertThat(value).isEqualTo(expected);
    }

    protected void assertThatXmlHasNoFieldForPath(MagdaDocument doc, String xmlPath) {
        var nodes = doc.xpath(xmlPath);
        assertThat(nodes.getLength()).isEqualTo(0);
    }

    protected void assertThatTechnicalFieldsInRequestMatchAanvraag(MagdaDocument doc, Aanvraag aanvraag, MagdaRegistrationInfo hoedanigheid) {
        assertThatXmlFieldIsEqualTo(doc, AFZENDER_REFERTE, aanvraag.getRequestId().toString());
        assertThatXmlFieldIsEqualTo(doc, AFZENDER_IDENTIFICATIE, hoedanigheid.getIdentification());
        assertThatXmlHasNoFieldForPath(doc, AFZENDER_HOEDANIGHEID);
        assertThatXmlFieldIsEqualTo(doc, VRAAG_REFERTE, aanvraag.getRequestId().toString());
    }

    protected void assertThatTechnicalFieldsIncludingHoedanigheidInRequestMatchAanvraag(MagdaDocument doc, Aanvraag aanvraag, MagdaRegistrationInfo hoedanigheid) {
        assertThatXmlFieldIsEqualTo(doc, AFZENDER_REFERTE, aanvraag.getRequestId().toString());
        assertThatXmlFieldIsEqualTo(doc, AFZENDER_IDENTIFICATIE, hoedanigheid.getIdentification());
        assertThatXmlFieldIsEqualTo(doc, AFZENDER_HOEDANIGHEID, hoedanigheid.getHoedanigheidscode());
        assertThatXmlFieldIsEqualTo(doc, VRAAG_REFERTE, aanvraag.getRequestId().toString());
    }
}