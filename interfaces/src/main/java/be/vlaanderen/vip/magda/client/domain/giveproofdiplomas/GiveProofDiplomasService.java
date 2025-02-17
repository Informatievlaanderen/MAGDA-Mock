package be.vlaanderen.vip.magda.client.domain.giveproofdiplomas;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefBewijsRequest;

import java.util.Optional;
import java.util.UUID;

/**
 * A service for interfacing with MAGDA's "LED.GeefBewijs" services for retrieving information on a citizen's diploma proofs.
 */
public interface GiveProofDiplomasService {

    /**
     * Retrieves diploma proof information, if any, from a GeefBewijsRequest.
     *
     * @see ProofDiplomas
     * @see GeefBewijsRequest
     */
    Optional<ProofDiplomas> getProofDiplomas(GeefBewijsRequest request) throws MagdaClientException;

    /**
     * Retrieves diploma proof information, if any, from a GeefBewijsRequest, with a provided request ID.
     *
     * @see ProofDiplomas
     * @see GeefBewijsRequest
     */
    Optional<ProofDiplomas> getProofDiplomas(GeefBewijsRequest request, UUID requestId) throws MagdaClientException;
}
