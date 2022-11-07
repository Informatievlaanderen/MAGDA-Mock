package be.vlaanderen.vip.magda.client.connection;

import be.vlaanderen.vip.magda.client.Aanvraag;
import be.vlaanderen.vip.magda.exception.MagdaSendFailed;
import org.w3c.dom.Document;

public interface MagdaConnection {
    @Deprecated(since = "20221027",forRemoval = true)
    Document sendDocument(Aanvraag aanvraag, Document xml) throws MagdaSendFailed;

    Document sendDocument(Document xml) throws MagdaSendFailed;
}
