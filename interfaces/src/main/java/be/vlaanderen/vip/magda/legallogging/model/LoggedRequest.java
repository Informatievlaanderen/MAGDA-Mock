package be.vlaanderen.vip.magda.legallogging.model;

import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

/**
 * Basis klasse voor alle gelogde aanvragen.
 * <p>
 * Bevat:
 * <ul>
 * <li>INSZ nummer van de persoon die de aanvraag deed</li>
 * <li>Uniek transactie ID van de aanvraag</li>
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
