package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.Aanvraag;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentificatie;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheid;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Getter
@ToString
public class GeefAanslagbiljetPersonenbelastingAanvraag extends Aanvraag {

    public GeefAanslagbiljetPersonenbelastingAanvraag(String insz) {
        super(insz);
    }

    @Builder
    public GeefAanslagbiljetPersonenbelastingAanvraag(String insz, String overWie, String registratie) {
        super(insz, StringUtils.defaultString(overWie, insz));
        setRegistratie(StringUtils.defaultString(registratie, "default"));
    }

    @Override
    public MagdaServiceIdentificatie magdaService() {
        return new MagdaServiceIdentificatie("GeefAanslagbiljetPersonenbelasting", "02.00.0000");
    }

    @Override
    public void fillIn(MagdaDocument request, MagdaHoedanigheid magdaHoedanigheid) {
        super.fillIn(request, magdaHoedanigheid);
    }

}