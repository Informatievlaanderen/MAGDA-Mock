package be.vlaanderen.vip.magda.legallogging.model;

import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;

import java.util.UUID;

/**
 * Not authorized to perform this request
 * <p>
 * Adds the following fields to {@link LoggedRequest}:
 * <ul>
 * <li>none</li>
 * </ul>
 */
public class UnauthorizedLoggedRequest extends LoggedRequest {
    public UnauthorizedLoggedRequest(String insz, UUID transactieID, String dienst, String dienstVersie, MagdaRegistrationInfo registratie) {
        super(insz, null, transactieID, null, dienst, dienstVersie, registratie);
    }

    public UnauthorizedLoggedRequest(String insz, UUID transactieID, UUID localTransactieID, String dienst, String dienstVersie, MagdaRegistrationInfo registratie) {
        super(insz, null, transactieID, localTransactieID, dienst, dienstVersie, registratie);
    }
}
