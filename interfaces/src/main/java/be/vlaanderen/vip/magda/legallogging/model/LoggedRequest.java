package be.vlaanderen.vip.magda.legallogging.model;

import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/**
 * Base class for all request logs.
 * <p>
 * Contains:
 * <ul>
 * <li>INSZ number of the person that performed the request</li>
 * <li>Unique transaction ID of the request</li>
 * </ul>
 */
@Getter
@AllArgsConstructor
public class LoggedRequest { // XXX remove dutch from attributes
    private final String insz;
    private final List<String> overWie;
    private final UUID transactieID;
    private final UUID localTransactieID;
    private final String dienst;
    private final String dienstVersie;
    private final MagdaRegistrationInfo registratie;
}
