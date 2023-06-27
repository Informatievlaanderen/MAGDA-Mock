package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.mock.magda.client.exceptions.MagdaMockException;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class RandomPasfotoSimulator extends BaseSOAPSimulator {

    // FIXME number of files shouldn't be hardcoded, as the user of magdamock may mount an alternate set of xml files
    private enum GenderCategory {
        MALE("mannen", 6),
        FEMALE("vrouwen", 4);

        @Getter private final String directoryName;
        @Getter private final int numberOfFiles;

        GenderCategory(String directoryName, int numberOfFiles) {
            this.directoryName = directoryName;
            this.numberOfFiles = numberOfFiles;
        }
    }

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
        var versie = request.getValue("//Verzoek/Context/Versie");

        var insz = request.getValue(keys.get(0));

        var responseBody = loadSimulatorResource(type, exactPasFotoresourcePath(serviceName, versie, insz));
        if (responseBody == null) {
            responseBody = loadSimulatorResource(type, randomPasFotoResourcePath(serviceName, versie, insz));
        }
        if (responseBody == null) {
            responseBody = loadSimulatorResource(type, exactPasFotoresourcePath(serviceName, versie, "notfound"));
        }
        if (responseBody == null) {
            responseBody = loadSimulatorResource(type, exactPasFotoresourcePath(serviceName, versie, "succes"));
        }

        if (responseBody != null) {
            patchResponse(request, responseBody);

            // Patch response gebaseerd op request input
            responseBody.setValue("//Antwoorden/Antwoord/Inhoud/Pasfoto/INSZ", insz);

            return wrapInEnvelope(responseBody);
        } else {
            throw new MagdaMockException("No mock data found for request to %s %s".formatted(serviceName, versie));
        }
    }

    private String randomPasFotoResourcePath(String serviceName, String versie, String insz) {
        var genderCategory = isMaleINSZ(insz) ? GenderCategory.MALE : GenderCategory.FEMALE;

        return String.join("/",
                serviceName,
                versie,
                genderCategory.directoryName,
                String.valueOf(insz.hashCode() % genderCategory.numberOfFiles))
                + ".xml";
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

    private String exactPasFotoresourcePath(String serviceName, String versie, String insz) {
        return String.join("/", serviceName, versie, insz) + ".xml";
    }
}