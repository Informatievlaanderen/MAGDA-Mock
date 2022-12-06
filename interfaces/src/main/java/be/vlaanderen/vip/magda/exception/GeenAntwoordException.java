package be.vlaanderen.vip.magda.exception;

import be.vlaanderen.vip.magda.client.Aanvraag;
import lombok.Getter;

import java.util.UUID;

/**
 * De server heeft geen antwoord geleverd.
 */
@Getter
public class GeenAntwoordException extends BronException {
    private static final long serialVersionUID = 4914331924177455934L;
    
    private final UUID transactieID;
    private final UUID localTransactieID;
    private String dienst;

    public GeenAntwoordException(Aanvraag aanvraag, String bericht) {
        super(bericht);
        this.transactieID = aanvraag.getCorrelationId();
        this.localTransactieID = aanvraag.getRequestId();
        this.dienst = aanvraag.magdaService().getNaam();
    }

    public GeenAntwoordException(String dienst, UUID transactieID, UUID localTransactieID, String bericht) {
        super(bericht);
        this.transactieID = transactieID;
        this.localTransactieID = localTransactieID;
        this.dienst = dienst;
    }

    public GeenAntwoordException(String dienst, UUID transactieID, UUID localTransactieID, String bericht, Throwable oorzaak) {
        super(bericht, oorzaak);
        this.transactieID = transactieID;
        this.localTransactieID = localTransactieID;
        this.dienst = dienst;
    }
}
