package be.vlaanderen.vip.magda.legallogging.model;

import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheid;

import java.util.UUID;

/**
 * Niet gemachtigd om deze aanvraag te doen
 * <p>
 * Voegt de volgende velden toe aan {@link GelogdeAanvraag}:
 * <ul>
 * <li>geen</li>
 * </ul>
 */
public class NietGeautoriseerdeAanvraag extends GelogdeAanvraag {
    public NietGeautoriseerdeAanvraag(String insz, UUID transactieID, String dienst, String dienstVersie, MagdaHoedanigheid registratie) {
        super(insz, null, transactieID, null, dienst, dienstVersie, registratie);
    }

    public NietGeautoriseerdeAanvraag(String insz, UUID transactieID, UUID localTransactieID, String dienst, String dienstVersie, MagdaHoedanigheid registratie) {
        super(insz, null, transactieID, localTransactieID, dienst, dienstVersie, registratie);
    }
}
