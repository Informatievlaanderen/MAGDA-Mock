package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaRequest;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import lombok.Getter;
import lombok.ToString;

/**
 * A request to a "GeefBewijs" MAGDA service, which provides licenses.
 * Adds the following fields to the {@link MagdaRequest}:
 * <ul>
 * <li>none</li>
 * </ul>
 *
 * @see <a href="file:resources/templates/GeefBewijs/02.00.0000/template.xml">XML template for this request type</a>
 */
@Getter
@ToString
public class GeefBewijsRequest extends MagdaRequest {

    public static class Builder<SELF extends Builder<SELF>> extends MagdaRequest.Builder<SELF> {

        public GeefBewijsRequest build() {
            return new GeefBewijsRequest(
                    getRequestingPartyInsz(),
                    getSubjectInsz(),
                    getRegistratie()
            );
        }
    }

    public static Builder<? extends Builder<?>> builder() {
        return new Builder();
    }

    private GeefBewijsRequest(String requestingPartyInsz, String subjectInsz, String registratie) {
        super(requestingPartyInsz, subjectInsz, registratie);
    }

    @Override
    public MagdaServiceIdentification magdaServiceIdentification() {
        return new MagdaServiceIdentification("GeefBewijs", "02.00.0000");
    }

    @Override
    protected void fillIn(MagdaDocument request, MagdaRegistrationInfo magdaRegistrationInfo) {
        fillInCommonFields(request, magdaRegistrationInfo);
    }
}