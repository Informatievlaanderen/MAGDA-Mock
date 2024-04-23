package be.vlaanderen.vip.magda.client.domain.socialstatute;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaResponse;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;


public class MagdaResponseSocialStatutesTest {

    @Test
    public void magdaResponseSocialStatutesTest() throws Exception {

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(MagdaResponseSocialStatutesTest.class.getResourceAsStream("/socialstatuterespons.xml"));

        MagdaResponse magdaResponse = new MagdaResponse(null, null, null, null, null, new MagdaDocument(document), true, null);
        MagdaResponseWrapper response = new MagdaResponseWrapper(magdaResponse);
        MagdaResponseSocialStatutes magdaResponseSocialStatutes = new MagdaResponseSocialStatutes(response);

        List<SocialStatutes.SocialStatute> socialStatutes = magdaResponseSocialStatutes.socialStatutes();
        assertThat(socialStatutes, equalTo(List.of(new SocialStatutes.SocialStatute("INCREASED_COMPENSATION", "1"))));
    }

}
