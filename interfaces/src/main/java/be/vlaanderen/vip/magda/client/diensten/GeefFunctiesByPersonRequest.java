package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.Registration;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.diensten.subject.KBONumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Set;
import java.util.UUID;

/**
 * A request to a "GeefFuncties" MAGDA service allows you to consult the work relations for an INSZ.
 * Adds the following fields to the {@link PersonMagdaRequest}:
 * <ul>
 * <li>kboNumber: optional, the company number for the specific company where the job functions of the job holder are requested.</li>
 * <li>functionTypes: optional, the codes of specified job functions; only these functions will be searched if provided.</li>
 * </ul>
 *
 * @see <a href="file:resources/templates/GeefFuncties/02.00.0000/template.xml">XML template for this request type</a>
 * @see <a href="https://vlaamseoverheid.atlassian.net/wiki/spaces/MG/pages/500041057/Onderneming.GeefFuncties-02.00">More information on this request type</a>
 * @see <a href="https://economie.fgov.be/nl/themas/ondernemingen/kruispuntbank-van/diensten-voor-administraties/codetabellen">More information on the available job function codes</a>
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class GeefFunctiesByPersonRequest extends PersonMagdaRequest{

    public static class Builder extends PersonMagdaRequest.Builder<Builder> {
        @Getter(AccessLevel.PROTECTED)
        private KBONumber kboNumber;
        @Getter(AccessLevel.PROTECTED)
        private Set<String> functionTypes;

        public Builder kboNumber(KBONumber kboNumber) {
            this.kboNumber = kboNumber;
            return this;
        }

        public Builder functionTypes(Set<String> functionTypes) {
            this.functionTypes = functionTypes;
            return this;
        }

        public GeefFunctiesByPersonRequest build() {
            if(getInsz() == null) { throw new IllegalStateException("INSZ number must be given"); }
            return new GeefFunctiesByPersonRequest(
                    getInsz(),
                    getRegistration(),
                    getKboNumber(),
                    getFunctionTypes()
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Nullable
    private final KBONumber kboNumber;
    @Nullable
    private final Set<String> functionTypes;

    public GeefFunctiesByPersonRequest(
            @NotNull INSZNumber insz,
            @NotNull Registration registration,
            @Nullable KBONumber kboNumber,
            @Nullable Set<String> functionTypes) {
        super(insz, registration);
        this.kboNumber = kboNumber;
        this.functionTypes = functionTypes;
    }

    @Override
    public MagdaServiceIdentification magdaServiceIdentification() {
        return new MagdaServiceIdentification("GeefFuncties", "02.00.0000");
    }

    @Override
    protected void fillIn(MagdaDocument request, UUID requestId, MagdaRegistrationInfo magdaRegistrationInfo) {
        fillInCommonFields(request, requestId, magdaRegistrationInfo);
        request.removeNode("//Criteria/Persoon/Ondernemingsnummer");
        if(getKboNumber() != null)
        {
            request.createTextNode("//Criteria/Persoon", "Ondernemingsnummer", getKboNumber().getValue());
        }
        request.removeNode("//Criteria/AardFuncties");
        if(getFunctionTypes() != null && getFunctionTypes().size() > 0)
        {
            request.createNode("//Criteria", "AardFuncties");
            getFunctionTypes().forEach(x -> {
                request.createTextNode("//Criteria/AardFuncties", "AardFunctie", x);
            });
        }
    }
}
