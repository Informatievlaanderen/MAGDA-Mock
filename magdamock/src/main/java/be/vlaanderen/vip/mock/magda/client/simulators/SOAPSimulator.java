package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import org.w3c.dom.Document;

public interface SOAPSimulator {
    MagdaDocument send(MagdaDocument xml) ;
}
