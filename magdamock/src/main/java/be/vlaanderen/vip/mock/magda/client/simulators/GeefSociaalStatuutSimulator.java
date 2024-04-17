package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder;

import java.util.Arrays;
import java.util.List;

public class GeefSociaalStatuutSimulator extends StaticResponseSimulator {
    public GeefSociaalStatuutSimulator(ResourceFinder finder, String type, List<String> keys) {
        super(finder, type, keys);
    }

    public GeefSociaalStatuutSimulator(ResourceFinder finder, String type, String... keys) {
        this(finder, type, Arrays.asList(keys));
    }

    protected void patchResponse(MagdaDocument request, MagdaDocument response) {
        super.patchResponse(request, response);

        response.setValue("//INSZ", request.getValue("//INSZ"));

        var socialStatuteNamesRequest = request.getValues("//SociaalStatuut/Naam");
        var socialStatuteNamesResponse = response.getValues("//SociaalStatuut/Naam");


        socialStatuteNamesResponse.stream().filter(x -> !socialStatuteNamesRequest.contains(x)).forEach(x -> {
            //No injection, because we are removing redundant social statutes from hardcoded response xml.
            response.removeNode("//SociaalStatuut[Naam[text()='" + x + "']]");
        });

        socialStatuteNamesRequest.stream().filter(x -> !socialStatuteNamesResponse.contains(x)).forEach(x -> {
            writeNotAppliedSocialStatute(response, x);
        });
    }

    private void writeNotAppliedSocialStatute(MagdaDocument response, String socialStatuteName) {
        /*
            <SociaalStatuut>
                <Naam>SOCIAL_STATUTE_NAME</Naam>
                <Resultaat>
                    <Code>0</Code>
                    <Omschrijving>Niet van toepassing</Omschrijving>
                </Resultaat>
            </SociaalStatuut>
         */
        var doc = response.getXml();
        var sociaalStatuutNode = doc.createElement("SociaalStatuut");
        //Include name
        var nameNode = doc.createElement("Naam");
        nameNode.appendChild(doc.createTextNode(socialStatuteName));
        sociaalStatuutNode.appendChild(nameNode);
        //Include result
        var resultNode = doc.createElement("Resultaat");
        var resultCodeNode = doc.createElement("Code");
        resultCodeNode.appendChild(doc.createTextNode("0"));
        resultNode.appendChild(resultCodeNode);
        var resultDescriptionNode = doc.createElement("Omschrijving");
        resultDescriptionNode.appendChild(doc.createTextNode("Niet van toepassing"));
        resultNode.appendChild(resultDescriptionNode);
        sociaalStatuutNode.appendChild(resultNode);
        //Add to collection
        response.xpath("//SocialeStatuten").item(0).appendChild(sociaalStatuutNode);
    }
}
