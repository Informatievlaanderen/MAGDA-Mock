package be.vlaanderen.vip.magda.legallogging.model;

import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import lombok.Getter;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

/**
 * MAGDA heeft een respons gestuurd.
 * Voegt de volgende velden toe aan {@link LoggedRequest}:
 * <ul>
 * <li>duratie tijd voor respons, in nanoseconden</li>
 * </ul>
 */
@Getter
public class SucceededLoggedRequest extends LoggedRequest {
    private final Duration duratie;

    public SucceededLoggedRequest(String insz,
                                  List<String> overWie,
                                  UUID transactieID,
                                  UUID localTransactieID,
                                  Duration duratie,
                                  String dienst,
                                  String dienstVersie,
                                  MagdaRegistrationInfo registratie) {
        super(insz, overWie, transactieID, localTransactieID, dienst, dienstVersie, registratie);
        this.duratie = duratie;
    }
}
