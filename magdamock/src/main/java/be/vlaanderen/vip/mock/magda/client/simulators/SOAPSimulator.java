package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Slf4j
public abstract class SOAPSimulator {
    public abstract MagdaDocument send(MagdaDocument xml);

    protected static void PatchResponse(MagdaRequest params, MagdaDocument response) {
        response.setValue("//Referte", params.getReferte());
        response.setValue("//Ontvanger/Identificatie", params.getIdentificatie());
        response.setValue("//Ontvanger/Hoedanigheid", params.getHoedanigheid());
        response.setValue("//Ontvanger/Gebruiker", params.getGebruiker());

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
        try (InputStream resource = this.getClass().getResourceAsStream("/magda_simulator/" + type + "/" + testResource)) {
            if (Objects.nonNull(resource)) {
                return MagdaDocument.fromStream(resource);
            }
        } catch (Exception e) {
            log.error("Fout bij het laden van resource {}: ", testResource, e);
        }
        return null;
    }
}
