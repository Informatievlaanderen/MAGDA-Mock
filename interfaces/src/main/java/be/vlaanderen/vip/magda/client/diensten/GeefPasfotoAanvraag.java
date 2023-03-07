package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.Aanvraag;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentificatie;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Getter
@ToString
public class GeefPasfotoAanvraag extends Aanvraag {

    public GeefPasfotoAanvraag(String insz) {
        super(insz);
    }

    @Builder
    public GeefPasfotoAanvraag(String insz, String overWie, String registratie) {
        super(insz, StringUtils.defaultString(overWie, insz));
        setRegistratie(StringUtils.defaultString(registratie, "default"));
    }

    @Override
    public MagdaServiceIdentificatie magdaService() {
        return new MagdaServiceIdentificatie("GeefPasfoto", "02.00.0000");
    }

    @Override
    public void fillIn(MagdaDocument request, MagdaRegistrationInfo magdaRegistrationInfo) {
        super.fillIn(request, magdaRegistrationInfo);
    }

}