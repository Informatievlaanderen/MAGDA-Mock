package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.exception.MagdaSendFailed;

public interface ISOAPSimulator {
    MagdaDocument send(MagdaDocument request) throws MagdaSendFailed;
}
