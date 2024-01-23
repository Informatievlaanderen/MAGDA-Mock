package be.vlaanderen.vip.mock.magda.client.domain.socialstatute;

import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import be.vlaanderen.vip.magda.client.domain.socialstatute.MagdaResponseSocialStatute;
import be.vlaanderen.vip.magda.client.domain.socialstatute.SocialStatute;
import be.vlaanderen.vip.mock.magda.client.domain.MagdaMock;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

class MagdaResponseSocialStatuteIntegrationTest {

    @Test
    void socialStatuteCode() {
        var result = statute("62691699717", "LIVING_WAGES_PCSA_HELP");

        MatcherAssert.assertThat(result.socialStatuteCode(), is(equalTo(1)));
    }
    
    private SocialStatute statute(String insz, String socialStatute) {
        var response = MagdaMock.getInstance()
                                .getSocialStatute(insz, socialStatute);
        return new MagdaResponseSocialStatute(new MagdaResponseWrapper(response));
    }
    
}
