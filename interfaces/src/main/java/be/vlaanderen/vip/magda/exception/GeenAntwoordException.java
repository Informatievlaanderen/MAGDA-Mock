package be.vlaanderen.vip.magda.exception;

import be.vlaanderen.vip.magda.client.Aanvraag;
import lombok.Getter;

import java.io.Serial;
import java.util.UUID;

/**
 * De server heeft geen antwoord geleverd.
 */
@Getter
public class GeenAntwoordException extends BronException {
    @Serial
    private static final long serialVersionUID = 4914331924177455934L;
    
    private final UUID transactieID;
    private final UUID localTransactieID;
    private final String dienst;

    public GeenAntwoordException(String bericht, Throwable oorzaak, Aanvraag aanvraag) {
        super(bericht, oorzaak);
        this.transactieID = aanvraag.getCorrelationId();
        this.localTransactieID = aanvraag.getRequestId();
        this.dienst = aanvraag.magdaServiceIdentification().getNaam();
    }
}
