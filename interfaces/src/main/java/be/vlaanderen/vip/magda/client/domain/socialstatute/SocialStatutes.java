package be.vlaanderen.vip.magda.client.domain.socialstatute;

import java.util.List;

/**
 * Information of socialStatutes of a citizen
 */
public interface SocialStatutes {

    String insz();

    /**
     *
     * @see SocialStatute
     */
    List<SocialStatute> socialStatutes();

}
