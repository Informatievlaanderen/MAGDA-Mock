package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
public class StaticResponseSimulator implements SOAPSimulator {
    private final List<String> keys;

    public StaticResponseSimulator(List<String> keys) {
        this.keys = keys;
    }

    public StaticResponseSimulator(String... keys) {
        this.keys = Arrays.asList(keys);
    }

    @Override
    public MagdaDocument send(MagdaDocument request) {
        var params = new MagdaRequest(request, keys);

        var dienst = params.getServiceNaam();
        var versie = params.getServiceVersie();


        var response = loadResource(dienst, versie, params.getKeys());
        if (response == null) {
            response = loadResource(dienst, versie, replaceLastKey(params.getKeys(), "notfound"));
        }
        if (response == null) {
            response = loadResource(dienst, versie, replaceLastKey(params.getKeys(), "success"));
        }

        if (response != null) {
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

            return response;
        } else {
            log.warn("Geen mock data gevonden voor request naar {} {}", dienst, versie);
            return null;
        }
    }

    private List<String> replaceLastKey(List<String> original, String replaceFinal) {
        var result = new ArrayList<String>();
        for (var i = 0; i < (original.size() - 1); i++) {
            result.add(original.get(i));
        }
        result.add(replaceFinal);
        return result;
    }

    // TODO: gemeenschappelijke responses
    // Als er bvb 3 keys zijn A, B, C
    // 1. Kijk of A/B/C.xml bestaat
    // 2. Kijk of A/C.xml bestaat
    // 3. Kijk of C.xml bestaat
    private MagdaDocument loadResource(String dienst, String versie, List<String> keys) {
        String testResource = "/magda_simulator/" + dienst + "/" + versie + "/";
        if (keys.size() > 1) {
            for (var i = 0; i < keys.size() - 1; i++) {
                testResource += keys.get(i) + "/";
            }
        }
        testResource += keys.get(keys.size() - 1) + ".xml";
        try (InputStream resource = this.getClass().getResourceAsStream(testResource)) {
            if (Objects.nonNull(resource)) {
                return MagdaDocument.fromStream(resource);
            }
        } catch (Exception e) {
            log.error("Fout bij het laden van resource {}: ", testResource, e);
        }
        return null;
    }


}
