package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.mock.magda.client.exceptions.MagdaMockException;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RandomPasfotoSimulator extends BaseSOAPSimulator {

    private final String type;
    private final List<String> keys;

    public RandomPasfotoSimulator(ResourceFinder finder, String type, List<String> keys) {
        super(finder);
        this.type = type;
        this.keys = keys;
    }

    public RandomPasfotoSimulator(ResourceFinder finder, String type, String... keys) {
        this(finder, type, Arrays.asList(keys));
    }

    @Override
    public MagdaDocument send(MagdaDocument request) throws MagdaMockException {
        var serviceName = request.getValue("//Verzoek/Context/Naam");
        var serviceVersion = request.getValue("//Verzoek/Context/Versie");
        var insz = request.getValue(keys.get(0));

        validatePathElement(serviceName);
        validatePathElement(serviceVersion);
        validatePathElement(insz);

        var responseBody = loadSimulatorResource(type, exactPasFotoresourcePath(serviceName, serviceVersion, insz));
        if (responseBody == null) {
            responseBody = loadSimulatorResource(type, randomPasFotoResourcePath(serviceName, serviceVersion, insz));
        }
        if (responseBody == null) {
            responseBody = loadSimulatorResource(type, exactPasFotoresourcePath(serviceName, serviceVersion, "notfound"));
        }
        if (responseBody == null) {
            responseBody = loadSimulatorResource(type, exactPasFotoresourcePath(serviceName, serviceVersion, "succes"));
        }

        if (responseBody != null) {
            patchResponse(request, responseBody);

            // Patch response gebaseerd op request input
            responseBody.setValue("//Antwoorden/Antwoord/Inhoud/Pasfoto/INSZ", insz);

            return wrapInEnvelope(responseBody);
        } else {
            throw new MagdaMockException("No mock data found for request to %s %s".formatted(serviceName, serviceVersion));
        }
    }

    private String randomPasFotoResourcePath(String serviceName, String serviceVersion, String insz) {
        var genderDirectory = isMaleINSZ(insz) ? "mannen" : "vrouwen";
        var path = String.join("/", serviceName, serviceVersion, genderDirectory);
        var caseFiles = getFinder().listCaseFiles(type, path);

        return String.join("/", path, caseFiles.get(Math.abs(insz.hashCode()) % caseFiles.size()).name());
    }

    /**
     * Determines if an INSZ number refers to a male.
     * If not male, then the INSZ number refers to a female (because nonbinary people don't exist, apparently)
     *
     * @see <a href="https://www.ibz.rrn.fgov.be/nl/rijksregister/faq/meer-technische-informatie-it-autogeneratie-wijzigingen/">Normative source on information encoded in INSZ numbers</a>
     */
    public boolean isMaleINSZ(String insz) {
        return Optional.of(insz)
                .filter(StringUtils::isNumeric)
                .map(i -> Integer.parseInt(i.substring(6, 9)) % 2 == 1)
                .orElse(false);
    }

    private String exactPasFotoresourcePath(String serviceName, String serviceVersion, String insz) {
        return String.join("/", serviceName, serviceVersion, insz) + ".xml";
    }
}