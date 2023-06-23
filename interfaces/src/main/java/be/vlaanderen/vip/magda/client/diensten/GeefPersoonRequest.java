package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaRequest;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.diensten.subject.SubjectIdentificationNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
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

    public static class Builder<SELF extends Builder<SELF>> extends MagdaRequest.Builder<SELF> { // XXX test

        @Getter(AccessLevel.PROTECTED)
        private INSZNumber insz;

        @SuppressWarnings("unchecked")
        public SELF insz(INSZNumber insz) {
            this.insz = insz;
            return (SELF) this;
        }

        public SELF insz(String insz) {
            return insz(INSZNumber.of(insz));
        }

        public GeefPersoonRequest build() {
            return new GeefPersoonRequest(
                    getInsz(),
                    getRegistration()
            );
        }
    }

    public static Builder<? extends Builder<?>> builder() {
        return new Builder();
    }

    @NotNull
    private final INSZNumber insz;

    private GeefPersoonRequest(
            @NotNull INSZNumber insz,
            @NotNull String registratie) {
        super(registratie);
        this.insz = insz;
    }

    @Override
    public MagdaServiceIdentification magdaServiceIdentification() {
        return new MagdaServiceIdentification("GeefPersoon", "02.02.0000");
    }

    @Override
    public SubjectIdentificationNumber getSubject() {
        return insz;
    }

    @Override
    protected void fillIn(MagdaDocument request, MagdaRegistrationInfo magdaRegistrationInfo) {
        fillInCommonFields(request, magdaRegistrationInfo);

        request.setValue("//INSZ", getInsz().getValue());
    }
}