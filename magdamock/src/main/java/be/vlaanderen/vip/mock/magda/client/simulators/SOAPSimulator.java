package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.mock.magda.client.exceptions.MagdaMockException;

public interface SOAPSimulator {
    MagdaDocument send(MagdaDocument request) throws MagdaMockException;
}
