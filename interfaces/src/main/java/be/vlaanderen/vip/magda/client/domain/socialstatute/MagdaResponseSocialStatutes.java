package be.vlaanderen.vip.magda.client.domain.socialstatute;

import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import be.vlaanderen.vip.magda.client.xml.node.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public record MagdaResponseSocialStatutes(MagdaResponseWrapper response) implements SocialStatutes {

    private Stream<Node> names() {
        return response.getNodes("//SociaalStatuut/SocialeStatuten/SociaalStatuut/Naam");
    }

    private Stream<Node> codes() {
        return response.getNodes("//SociaalStatuut/SocialeStatuten/SociaalStatuut/Resultaat/Code");
    }

    @Override
    public List<SocialStatute> socialStatutes() {

        List<String> names = names().map(Node::getValue).map(name -> name.orElse(null)).toList();
        List<String> codes = codes().map(Node::getValue).map(code -> code.orElse(null)).toList();

        List<SocialStatute> socialStatutes = new ArrayList<>();

        for (int i = 0; i < names.size(); i++) {
            socialStatutes.add(new SocialStatute(names.get(i), codes.get(i)));
        }

        return socialStatutes.stream().filter(socialStatute -> socialStatute.code().equals("1")).toList();
    }
}
