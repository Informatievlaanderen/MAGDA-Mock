package be.vlaanderen.vip.magda.client.connection;

import be.vlaanderen.vip.magda.exception.MagdaConnectionException;
import org.w3c.dom.Document;

public interface MagdaConnection {

    Document sendDocument(Document xml) throws MagdaConnectionException;
}
