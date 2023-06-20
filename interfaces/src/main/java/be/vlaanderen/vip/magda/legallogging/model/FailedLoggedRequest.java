package be.vlaanderen.vip.magda.legallogging.model;

import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import lombok.Getter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * MAGDA heeft een respons gestuurd, maar deze bevat {@link Uitzondering}.
 * Voegt de volgende velden toe aan {@link LoggedRequest}:
 * <ul>
 * <li>duratie tijd voor respons, met nanoseconden precisie</li>
 * <li>Lijst van {@link Uitzondering} die MAGDA antwoordde</li>
 * </ul>
 */
@Getter
public class FailedLoggedRequest extends LoggedRequest {
    private final List<Uitzondering> uitzonderingen;
    private final Duration duratie;

    public FailedLoggedRequest(String insz,
                               UUID transactieID,
                               UUID localTransactieID,
                               Duration duratie,
                               List<Uitzondering> uitzonderingen,
                               String dienst,
                               String dienstVersie,
                               MagdaRegistrationInfo registratie) {
        super(insz, new ArrayList<>(), transactieID, localTransactieID, dienst, dienstVersie, registratie);
        this.uitzonderingen = uitzonderingen;
        this.duratie = duratie;
    }
}
