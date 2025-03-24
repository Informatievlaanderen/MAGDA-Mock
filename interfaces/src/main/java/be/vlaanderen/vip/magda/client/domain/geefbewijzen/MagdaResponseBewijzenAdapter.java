package be.vlaanderen.vip.magda.client.domain.geefbewijzen;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;

import java.util.Optional;

public interface MagdaResponseBewijzenAdapter {

    Optional<Bewijzen> adapt(MagdaResponseWrapper wrapper) throws MagdaClientException;
}