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
 * A request to a "GeefAanslagBiljet" MAGDA service, which provides tax bills.
 * Adds the following fields to the {@link MagdaRequest}:
 * <ul>
 * <li>insz: the INSZ number of the party about which the information is requested</li>
 * </ul>
 *
 * @see <a href="file:resources/templates/GeefAanslagbiljetPersonenbelasting/02.00.0000/template.xml">XML template for this request type</a>
 */
@Getter
@ToString
public class GeefAanslagbiljetPersonenbelastingRequest extends PersonMagdaRequest {

    public static class Builder<SELF extends Builder<SELF>> extends PersonMagdaRequest.Builder<SELF> {

        public GeefAanslagbiljetPersonenbelastingRequest build() {
            if(getInsz() == null) { throw new IllegalStateException("INSZ number must be given"); }

            return new GeefAanslagbiljetPersonenbelastingRequest(
                    getInsz(),
                    getRegistration()
            );
        }
    }

    public static Builder<? extends Builder<?>> builder() {
        return new Builder();
    }

    private GeefAanslagbiljetPersonenbelastingRequest(
            @NotNull INSZNumber insz,
            @NotNull String registratie) {
        super(insz, registratie);
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