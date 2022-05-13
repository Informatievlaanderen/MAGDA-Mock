package be.vlaanderen.vip.mock.magdaservice.aanvraag;

import be.vlaanderen.vip.magda.client.Aanvraag;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentificatie;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class GeefBewijsAanvraag extends Aanvraag {

    public GeefBewijsAanvraag(String insz) {
        super(insz);
    }

    @Override
    public MagdaServiceIdentificatie magdaService() {
        return new MagdaServiceIdentificatie("GeefBewijs", "02.00.0000");
    }

}