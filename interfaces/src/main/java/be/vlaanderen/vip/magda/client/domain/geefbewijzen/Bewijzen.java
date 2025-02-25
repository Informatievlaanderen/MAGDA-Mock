package be.vlaanderen.vip.magda.client.domain.geefbewijzen;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaResponse;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;

import java.util.List;
import java.util.Optional;

/**
 * Information on a citizen's diploma proofs.
 */
public interface Bewijzen {

    static Optional<Bewijzen> ofMagdaDocument(MagdaDocument magdaDocument) throws MagdaClientException {
        return MagdaResponseBewijzenAdapterJaxbImpl.getInstance().adapt(new MagdaResponseWrapper(MagdaResponse.builder()
                .document(magdaDocument)
                .build()));
    }

    List<Bewijs> bewijzen();
}