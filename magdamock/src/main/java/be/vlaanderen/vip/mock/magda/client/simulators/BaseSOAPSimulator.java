package be.vlaanderen.vip.mock.magda.client.simulators;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.mock.magda.inventory.ResourceFinder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseSOAPSimulator implements SOAPSimulator {
    private ResourceFinder finder;

    protected BaseSOAPSimulator(ResourceFinder finder) {
        this.finder = finder;
    }

    protected static void patchResponse(MagdaRequest params, MagdaDocument response) {
        response.setValue("//Referte", params.getReferte());
        response.setValue("//Ontvanger/Identificatie", params.getIdentificatie());
        response.setValue("//Ontvanger/Hoedanigheid", params.getHoedanigheid());
        
        Optional.ofNullable(params.getGebruiker())
                .ifPresentOrElse(user -> response.setValue("//Ontvanger/Gebruiker", user), 
                                 () -> response.removeNode("//Ontvanger/Gebruiker"));

        SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");
        String today = dayFormat.format(new Date());
        response.setValue("//Context/Bericht/Tijdstip/Datum", today);

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss.S");
        String time = timeFormat.format(new Date());
        response.setValue("//Context/Bericht/Tijdstip/Tijd", time);

        // Identificeert antwoord als komend van Magda Mock
        response.setValue("//Afzender/Referte", UUID.randomUUID().toString());
        response.setValue("//Afzender/Identificatie", "kb.vlaanderen.be/aiv/magda-mock-server");
        response.setValue("//Afzender/Naam", "Magda Mock Server");
    }

    protected MagdaDocument loadSimulatorResource(String type, String testResource) {
        try (InputStream resource = finder.loadSimulatorResource(type, testResource)) {
            if (Objects.nonNull(resource)) {
                return MagdaDocument.fromStream(resource);
            }
        } catch (Exception e) {
            log.error("Fout bij het laden van resource {}: ", testResource, e);
        }
        return null;
    }

    protected MagdaDocument wrapInEnvelope(MagdaDocument bodyDocument) {
        String soap = """
                <soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" >
                <soapenv:Header/>
                    <soapenv:Body>
                    %s
                    </soapenv:Body>
                </soapenv:Envelope>""".formatted(bodyDocument);

        return MagdaDocument.fromString(soap);
    }

    protected MagdaDocument makeFaultDocument(String faultCode, String faultString) {
        var faultDocument = MagdaDocument.fromResource(getClass(), "/magda_simulator/generic_fault.xml");
        faultDocument.setValue("//soapenv:Fault/faultcode", faultCode);
        faultDocument.setValue("//soapenv:Fault/faultstring", faultString);

        return wrapInEnvelope(faultDocument);
    }
}
