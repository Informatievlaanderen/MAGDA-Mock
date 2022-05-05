package be.vlaanderen.vip.magda.legallogging.model;

import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheid;
import lombok.Getter;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

/**
 * MAGDA heeft een respons gestuurd.
 * Voegt de volgende velden toe aan {@link GelogdeAanvraag}:
 * <ul>
 * <li>duratie tijd voor respons, in nanoseconden</li>
 * </ul>
 */
@Getter
public class GeslaagdeAanvraag extends GelogdeAanvraag {
    private Duration duratie;

    public GeslaagdeAanvraag(String insz,
                             List<String> overWie,
                             UUID transactieID,
                             UUID localTransactieID,
                             Duration duratie,
                             String dienst,
                             String dienstVersie,
                             MagdaHoedanigheid registratie) {
        super(insz, overWie, transactieID, localTransactieID, dienst, dienstVersie, registratie);
        this.duratie = duratie;
    }
}
