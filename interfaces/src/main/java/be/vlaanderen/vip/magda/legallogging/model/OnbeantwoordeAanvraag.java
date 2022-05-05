package be.vlaanderen.vip.magda.legallogging.model;

import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheid;

import java.util.ArrayList;
import java.util.UUID;

/**
 * MAGDA heeft geen respons gestuurd, communicatie fout of timeout.
 * <p>
 * Voegt de volgende velden toe aan {@link GelogdeAanvraag}:
 * <ul>
 * <li>geen</li>
 * </ul>
 */
public class OnbeantwoordeAanvraag extends GelogdeAanvraag {
    public OnbeantwoordeAanvraag(String insz, String overWie, UUID transactieID, UUID localTransactieID, String dienst, String dienstVersie, MagdaHoedanigheid registratie) {
        super(insz, new ArrayList<>(), transactieID, localTransactieID, dienst, dienstVersie, registratie);
    }
}
