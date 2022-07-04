package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.Aanvraag;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentificatie;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheid;
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

    @Override
    public void fillIn(MagdaDocument request, MagdaHoedanigheid magdaHoedanigheid) {
        super.fillIn(request, magdaHoedanigheid);
    }

}