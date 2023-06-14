package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.Aanvraag;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentificatie;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Getter
@ToString
public class GeefPersoonAanvraag extends Aanvraag {

    public GeefPersoonAanvraag(String insz) {
        super(insz);
    }

    @Builder
    public GeefPersoonAanvraag(String insz, String overWie, String registratie) {
        super(insz, StringUtils.defaultString(overWie, insz));
        setRegistratie(StringUtils.defaultString(registratie, "default"));
    }

    @Override
    public MagdaServiceIdentificatie magdaService() {
        return new MagdaServiceIdentificatie("GeefPersoon", "02.02.0000");
    }

}