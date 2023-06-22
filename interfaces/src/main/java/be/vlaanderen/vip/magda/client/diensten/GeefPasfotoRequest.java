package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaRequest;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import lombok.Getter;
import lombok.ToString;

/**
 * A request to a "GeefPasfoto" MAGDA service, which provides passport photos.
 * Adds the following fields to the {@link MagdaRequest}:
 * <ul>
 * <li>none</li>
 * </ul>
 */
@Getter
@ToString
public class GeefPasfotoRequest extends MagdaRequest {

    public static class Builder<SELF extends Builder<SELF>> extends MagdaRequest.Builder<SELF> {

        public GeefPasfotoRequest build() {
            return new GeefPasfotoRequest(
                    getInsz(),
                    getAboutWhom(),
                    getRegistratie()
            );
        }
    }

    public static Builder<? extends Builder<?>> builder() {
        return new Builder();
    }

    private GeefPasfotoRequest(String insz, String aboutWhom, String registratie) {
        super(insz, aboutWhom, registratie);
    }

    @Override
    public MagdaServiceIdentification magdaServiceIdentification() {
        return new MagdaServiceIdentification("GeefPasfoto", "02.00.0000");
    }

    @Override
    protected void fillIn(MagdaDocument request, MagdaRegistrationInfo magdaRegistrationInfo) {
        fillInCommonFields(request, magdaRegistrationInfo);
    }
}