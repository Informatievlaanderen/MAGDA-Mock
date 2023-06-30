package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

/**
 * A request to a "GeefBewijs" MAGDA service, which provides licenses.
 * Adds the following fields to the {@link PersonMagdaRequest}:
 * <ul>
 * <li>none</li>
 * </ul>
 *
 * @see <a href="file:resources/templates/GeefBewijs/02.00.0000/template.xml">XML template for this request type</a>
 */
@Getter
@ToString
public class GeefBewijsRequest extends PersonMagdaRequest {

    public static class Builder<SELF extends Builder<SELF>> extends PersonMagdaRequest.Builder<SELF> {

        public GeefBewijsRequest build() {
            if(getInsz() == null) { throw new IllegalStateException("INSZ number must be given"); }

            return new GeefBewijsRequest(
                    getInsz(),
                    getRegistration()
            );
        }
    }

    public static Builder<? extends Builder<?>> builder() {
        return new Builder();
    }

    private GeefBewijsRequest(
            @NotNull INSZNumber insz,
            @NotNull String registratie) {
        super(insz, registratie);
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