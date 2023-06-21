package be.vlaanderen.vip.magda.legallogging.model;

import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import lombok.Getter;

import java.util.Collections;
import java.util.UUID;

@Getter
public class MagdaLoggedRequest extends LoggedRequest {

    public MagdaLoggedRequest(String insz, String overWie, UUID transactieID, UUID localTransactieID, String dienst, String dienstVersie, MagdaRegistrationInfo registratie) {
        super(insz, Collections.singletonList(overWie), transactieID, localTransactieID, dienst, dienstVersie, registratie);
    }
}
