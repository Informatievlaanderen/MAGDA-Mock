package be.vlaanderen.vip.magda.client.domain.socialstatute;

import be.vlaanderen.vip.magda.MalformedMagdaResponseException;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import be.vlaanderen.vip.magda.client.xml.node.Node;

public record MagdaResponseSocialStatute(MagdaResponseWrapper response) implements SocialStatute {

    @Override
    public Integer socialStatuteCode() {
        return response.getNodes("//Persoon/SocialeStatuten")
                       .findFirst()
                       .flatMap(n -> n.get("//Resultaat/Code"))
                       .flatMap(Node::getValue)
                       .map(Integer::parseUnsignedInt)
                       .orElseThrow(() -> new MalformedMagdaResponseException("Magda response document misses an expected social stature code node"));
    }

}
