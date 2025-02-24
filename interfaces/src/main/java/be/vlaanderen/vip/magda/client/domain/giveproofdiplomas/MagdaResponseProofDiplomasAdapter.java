package be.vlaanderen.vip.magda.client.domain.giveproofdiplomas;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;

import java.util.Optional;

public interface MagdaResponseProofDiplomasAdapter {

    Optional<ProofDiplomas> adapt(MagdaResponseWrapper wrapper) throws MagdaClientException;
}