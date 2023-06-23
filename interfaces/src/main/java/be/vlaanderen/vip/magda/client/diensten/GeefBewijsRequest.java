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

        public GeefBewijsRequest build() {
            return new GeefBewijsRequest(
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

    private GeefBewijsRequest(
            @NotNull INSZNumber insz,
            @NotNull String registratie) {
        super(registratie);
        this.insz = insz;
    }

    @Override
    public MagdaServiceIdentification magdaServiceIdentification() {
        return new MagdaServiceIdentification("GeefBewijs", "02.00.0000");
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