package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaRequest;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class GeefAanslagbiljetPersonenbelastingRequest extends MagdaRequest {

    public static class Builder<SELF extends Builder<SELF>> extends MagdaRequest.Builder<SELF> {

        public GeefAanslagbiljetPersonenbelastingRequest build() {
            return new GeefAanslagbiljetPersonenbelastingRequest(
                    getInsz(),
                    getOverWie(),
                    getRegistratie()
            );
        }
    }

    public static Builder<? extends Builder<?>> builder() {
        return new Builder();
    }

    private GeefAanslagbiljetPersonenbelastingRequest(String insz, String overWie, String registratie) {
        super(insz, overWie, registratie);
    }

    @Override
    public MagdaServiceIdentification magdaServiceIdentification() {
        return new MagdaServiceIdentification("GeefAanslagbiljetPersonenbelasting", "02.00.0000");
    }

    @Override
    protected void fillIn(MagdaDocument request, MagdaRegistrationInfo magdaRegistrationInfo) {
        fillInCommonFields(request, magdaRegistrationInfo);
    }
}