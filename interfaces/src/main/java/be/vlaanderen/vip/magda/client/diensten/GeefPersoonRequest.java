package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaRequest;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

/**
 * A request to a "GeefPersoon" MAGDA service, which provides personal information.
 * Adds the following fields to the {@link MagdaRequest}:
 * <ul>
 * <li>insz: the INSZ number of the party about which the information is requested</li>
 * </ul>
 *
 * @see <a href="file:resources/templates/GeefPersoon/02.02.0000/template.xml">XML template for this request type</a>
 */
@Getter
@ToString
public class GeefPersoonRequest extends PersonMagdaRequest {

    public static class Builder<SELF extends Builder<SELF>> extends PersonMagdaRequest.Builder<SELF> {

        public GeefPersoonRequest build() {
            if(getInsz() == null) { throw new IllegalStateException("INSZ number must be given"); }

            return new GeefPersoonRequest(
                    getInsz(),
                    getRegistration()
            );
        }
    }

    public static Builder<? extends Builder<?>> builder() {
        return new Builder();
    }

    private GeefPersoonRequest(
            @NotNull INSZNumber insz,
            @NotNull String registratie) {
        super(insz, registratie);
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