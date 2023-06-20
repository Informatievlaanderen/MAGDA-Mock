package be.vlaanderen.vip.magda.legallogging.model;

import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;

import java.util.Collections;
import java.util.UUID;

/**
 * MAGDA heeft geen respons gestuurd, communicatie fout of timeout.
 * <p>
 * Voegt de volgende velden toe aan {@link LoggedRequest}:
 * <ul>
 * <li>geen</li>
 * </ul>
 */
public class UnansweredLoggedRequest extends LoggedRequest {
    public UnansweredLoggedRequest(String insz, String overWie, UUID transactieID, UUID localTransactieID, String dienst, String dienstVersie, MagdaRegistrationInfo registratie) {
        super(insz, Collections.singletonList(overWie), transactieID, localTransactieID, dienst, dienstVersie, registratie);
    }
}
