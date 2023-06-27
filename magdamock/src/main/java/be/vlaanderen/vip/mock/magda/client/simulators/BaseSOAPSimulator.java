package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.mock.magda.client.exceptions.MagdaMockException;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public abstract class BaseSOAPSimulator implements SOAPSimulator {
    private final ResourceFinder finder;

    protected BaseSOAPSimulator(ResourceFinder finder) {
        this.finder = finder;
    }

    protected static void patchResponse(MagdaDocument request, MagdaDocument response) {
        response.setValue("//Referte", request.getValue("//Afzender/Referte"));
        response.setValue("//Ontvanger/Identificatie", request.getValue("//Afzender/Identificatie"));
        response.setValue("//Ontvanger/Hoedanigheid", request.getValue("//Afzender/Hoedanigheid"));
        
        Optional.ofNullable(request.getValue("//Afzender/Gebruiker"))
                .ifPresentOrElse(user -> response.setValue("//Ontvanger/Gebruiker", user), 
                                 () -> response.removeNode("//Ontvanger/Gebruiker"));

        var dayFormat = new SimpleDateFormat("yyyy-MM-dd");
        var today = dayFormat.format(new Date());
        response.setValue("//Context/Bericht/Tijdstip/Datum", today);

        var timeFormat = new SimpleDateFormat("HH:mm:ss.S");
        var time = timeFormat.format(new Date());
        response.setValue("//Context/Bericht/Tijdstip/Tijd", time);

        // Identificeert antwoord als komend van Magda Mock
        response.setValue("//Afzender/Referte", UUID.randomUUID().toString());
        response.setValue("//Afzender/Identificatie", "kb.vlaanderen.be/aiv/magda-mock-server");
        response.setValue("//Afzender/Naam", "Magda Mock Server");
    }

    protected MagdaDocument loadSimulatorResource(String type, String testResource) {
        try (var resource = finder.loadSimulatorResource(type, testResource)) {
            if (Objects.nonNull(resource)) {
                return MagdaDocument.fromStream(resource);
            }
        } catch (Exception e) {
            log.error("Error loading resource {}: ", testResource, e);
        }
        return null;
    }

    protected MagdaDocument wrapInEnvelope(MagdaDocument bodyDocument) {
        var soap = """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" >
                <soapenv:Header/>
                    <soapenv:Body>
                    %s
                    </soapenv:Body>
                </soapenv:Envelope>""".formatted(bodyDocument);

        return MagdaDocument.fromString(soap);
    }

    protected MagdaDocument makeFaultDocument(String faultCode, String faultString) {
        var faultDocument = Objects.requireNonNull(MagdaDocument.fromResource(getClass(), "/magda_simulator/generic_fault.xml"));
        faultDocument.setValue("//soapenv:Fault/faultcode", faultCode);
        faultDocument.setValue("//soapenv:Fault/faultstring", faultString);

        return wrapInEnvelope(faultDocument);
    }

    protected void validatePathElement(String value) {
        if(value.contains("/") || "..".equals(value) || ".".equals(value)) {
            throw new MagdaMockException("Invalid path element: %s".formatted(value));
        }
    }
}
