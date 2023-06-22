package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaRequest;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import lombok.Getter;
import lombok.ToString;

/**
 * A request to a "GeefPersoon" MAGDA service, which provides personal information.
 * Adds the following fields to the {@link MagdaRequest}:
 * <ul>
 * <li>none</li>
 * </ul>
 *
 * @see <a href="file:resources/templates/GeefPersoon/02.02.0000/template.xml">XML template for this request type</a>
 */
@Getter
@ToString
public class GeefPersoonRequest extends MagdaRequest {

    public static class Builder<SELF extends Builder<SELF>> extends MagdaRequest.Builder<SELF> {

        public GeefPersoonRequest build() {
            return new GeefPersoonRequest(
                    getRequestingPartyInsz(),
                    getSubjectInsz(),
                    getRegistratie()
            );
        }
    }

    public static Builder<? extends Builder<?>> builder() {
        return new Builder();
    }

    private GeefPersoonRequest(String requestingPartyInsz, String subjectInsz, String registratie) {
        super(requestingPartyInsz, subjectInsz, registratie);
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