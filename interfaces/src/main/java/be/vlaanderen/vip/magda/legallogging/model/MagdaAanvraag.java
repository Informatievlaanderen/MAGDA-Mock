package be.vlaanderen.vip.magda.legallogging.model;

import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheid;
import lombok.Getter;

import java.util.Collections;
import java.util.UUID;

@Getter
public class MagdaAanvraag extends GelogdeAanvraag {

    public MagdaAanvraag(String insz, String overWie, UUID transactieID, UUID localTransactieID, String dienst, String dienstVersie, MagdaHoedanigheid registratie) {
        super(insz, Collections.singletonList(overWie), transactieID, localTransactieID, dienst, dienstVersie, registratie);
    }
}
