package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.ToString;

/**
 * A request to a "GeefPasfoto" MAGDA service, which provides passport photos.
 * Adds the following fields to the {@link PersonMagdaRequest}:
 * <ul>
 * <li>none</li>
 * </ul>
 *
 * @see <a href="file:resources/templates/GeefPasfoto/02.00.0000/template.xml">XML template for this request type</a>
 */
@Getter
@ToString
public class GeefPasfotoRequest extends PersonMagdaRequest {

    public static class Builder extends PersonMagdaRequest.Builder<Builder> {

        public GeefPasfotoRequest build() {
            if(getInsz() == null) { throw new IllegalStateException("INSZ number must be given"); }

            return new GeefPasfotoRequest(
                    getInsz(),
                    getRegistration()
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private GeefPasfotoRequest(
            @NotNull INSZNumber insz,
            @NotNull String registratie) {
        super(insz, registratie);
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