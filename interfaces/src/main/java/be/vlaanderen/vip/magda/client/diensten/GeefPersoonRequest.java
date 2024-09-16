package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

import static be.vlaanderen.vip.magda.client.diensten.PersonSource.RR;

/**
 * A request to a "GeefPersoon" MAGDA service, which provides personal information.
 * Adds the following fields to the {@link PersonMagdaRequest}:
 * <ul>
 * <li>source: the source to be consulted (optional; default is RR)</li>
 * </ul>
 *
 * @see <a href="file:resources/templates/GeefPersoon/02.02.0000/template.xml">XML template for this request type</a>
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class GeefPersoonRequest extends PersonMagdaRequest {

    public static class Builder extends PersonMagdaRequest.Builder<Builder> {

        @Getter(AccessLevel.PROTECTED)
        private PersonSource source;

        public GeefPersoonRequest.Builder source(PersonSource source) {
            this.source = source;
            return this;
        }

        public GeefPersoonRequest build() {
            if(getInsz() == null) { throw new IllegalStateException("INSZ number must be given"); }

            return new GeefPersoonRequest(
                    getInsz(),
                    getRegistration(),
                    getSource()
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Nullable
    private final PersonSource source;

    private GeefPersoonRequest(
            @NotNull INSZNumber insz,
            @NotNull String registratie,
            @Nullable PersonSource source) {
        super(insz, registratie);
        this.source = source;
    }

    @Override
    public MagdaServiceIdentification magdaServiceIdentification() {
        return new MagdaServiceIdentification("GeefPersoon", "02.02.0000");
    }

    @Override
    protected void fillIn(MagdaDocument request, UUID requestId, MagdaRegistrationInfo magdaRegistrationInfo) {
        fillInCommonFields(request, requestId, magdaRegistrationInfo);

        var sourceValue = source != null ? source : RR;
        request.setValue("//Inhoud/Bron", sourceValue.getValue());

        if (sourceValue == RR) { request.removeNode("//Inhoud/Criteria/OpvragingenKSZ"); }
    }
}