package be.vlaanderen.vip.magda.exception;

import be.vlaanderen.vip.magda.client.MagdaRequest;
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

    public GeenAntwoordException(String bericht, Throwable oorzaak, MagdaRequest magdaRequest) {
        super(bericht, oorzaak);
        this.transactieID = magdaRequest.getCorrelationId();
        this.localTransactieID = magdaRequest.getRequestId();
        this.dienst = magdaRequest.magdaServiceIdentification().getName();
    }
}
