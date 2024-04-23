package be.vlaanderen.vip.magda.client.domain.socialstatute;

import java.util.List;

public interface SocialStatutes {

    List<SocialStatute> socialStatutes();

    record SocialStatute(String name, String code) {

    }

}
