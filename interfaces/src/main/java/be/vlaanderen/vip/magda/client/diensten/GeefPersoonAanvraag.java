package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.Aanvraag;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
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
    public MagdaServiceIdentification magdaServiceIdentification() {
        return new MagdaServiceIdentification("GeefPersoon", "02.02.0000");
    }

    @Override
    protected void fillIn(MagdaDocument request, MagdaRegistrationInfo magdaRegistrationInfo) {
        fillInCommonFields(request, magdaRegistrationInfo);
    }
}