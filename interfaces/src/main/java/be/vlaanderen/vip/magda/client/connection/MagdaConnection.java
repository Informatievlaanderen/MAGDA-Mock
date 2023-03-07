package be.vlaanderen.vip.magda.client.connection;

import be.vlaanderen.vip.magda.exception.MagdaSendFailed;
import org.w3c.dom.Document;

public interface MagdaConnection {

    Document sendDocument(Document xml) throws MagdaSendFailed;
}
