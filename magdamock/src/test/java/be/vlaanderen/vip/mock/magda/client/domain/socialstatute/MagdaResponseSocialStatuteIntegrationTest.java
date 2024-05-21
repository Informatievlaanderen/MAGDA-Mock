package be.vlaanderen.vip.mock.magda.client.domain.socialstatute;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import be.vlaanderen.vip.magda.client.domain.socialstatute.MagdaResponseSocialStatutesAdapterJaxbImpl;
import be.vlaanderen.vip.magda.client.domain.socialstatute.SocialStatutes;
import be.vlaanderen.vip.mock.magda.client.domain.MagdaMock;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class MagdaResponseSocialStatuteIntegrationTest {

    @Test
    void socialStatuteRequest() throws MagdaClientException {
        var result = statute("62691699717", "LIVING_WAGES_PCSA_HELP");

        MatcherAssert.assertThat(result.insz(), is(equalTo("62691699717")));
        MatcherAssert.assertThat(result.socialStatutes().get(0).name(), is("LIVING_WAGES_PCSA_HELP"));
    }

    @Test
    void socialStatutesRequest() throws MagdaClientException {
        var result = statutes("62691699717", List.of("LIVING_WAGES_PCSA_HELP", "TEST"));

        MatcherAssert.assertThat(result.insz(), is(equalTo("62691699717")));
        MatcherAssert.assertThat(result.socialStatutes().get(0).name(), is("LIVING_WAGES_PCSA_HELP"));
        MatcherAssert.assertThat(result.socialStatutes().get(1).name(), is("TEST"));
    }

    private SocialStatutes statute(String insz, String socialStatute) throws MagdaClientException {
        var response = MagdaMock.getInstance()
                                .getSocialStatute(insz, socialStatute);

        MagdaResponseSocialStatutesAdapterJaxbImpl magdaResponseSocialStatutesAdapterJaxb = new MagdaResponseSocialStatutesAdapterJaxbImpl();
        return magdaResponseSocialStatutesAdapterJaxb.adapt(new MagdaResponseWrapper(response));
    }

    private SocialStatutes statutes(String insz, List<String> socialStatute) throws MagdaClientException {
        var response = MagdaMock.getInstance()
                .getSocialStatute(insz, socialStatute);

        MagdaResponseSocialStatutesAdapterJaxbImpl magdaResponseSocialStatutesAdapterJaxb = new MagdaResponseSocialStatutesAdapterJaxbImpl();
        return magdaResponseSocialStatutesAdapterJaxb.adapt(new MagdaResponseWrapper(response));
    }
    
}
