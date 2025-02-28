package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.Registration;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Year;
import java.util.UUID;

/**
 * A request to a "GeefBetalingenHandicap" MAGDA service allows you to consult the periods and amounts paid out in the context of the living wage for an INSZ.
 * Adds the following fields to the {@link PersonMagdaRequest}:
 * <ul>
 * <li>currentYear: Search by current year. Year in 'YYYY' format. This date cannot be in the future.</li>
 * <li>referenceYear: Search by reference year. Year in 'YYYY' format. This date cannot be in the future.</li>
 * <li>numberOfYearsAgo: total number of years back in time. Values: 2 or 3. (Optional)</li>
 * </ul>
 * The service can be consulted by providing 'currentYear' or 'referenceYear', at least one field must contain a value, both fields are not allowed.
 * @see <a href="file:resources/templates/GeefLeefloonbedragen/02.00.0000/template.xml">XML template for this request type</a>
 * @see <a href="https://vlaamseoverheid.atlassian.net/wiki/spaces/MG/pages/1207599973/SocZek.GeefLeefLoonBedragen-02.00">More information on this request type</a>
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class GeefLeefLoonBedragenRequest extends PersonMagdaRequest {

    public static class Builder extends PersonMagdaRequest.Builder<Builder> {

        @Getter(AccessLevel.PROTECTED)
        private Year currentYear;
        @Getter(AccessLevel.PROTECTED)
        private Year referenceYear;
        @Getter(AccessLevel.PROTECTED)
        private Integer numberOfYearsAgo;

        public Builder currentYear(Year currentYear) {
            this.currentYear = currentYear;
            return this;
        }

        public Builder referenceYear(Year referenceYear) {
            this.referenceYear = referenceYear;
            return this;
        }

        public Builder numberOfYearsAgo(Integer numberOfYearsAgo) {
            this.numberOfYearsAgo = numberOfYearsAgo;
            return this;
        }

        public GeefLeefLoonBedragenRequest build() {
            if(getInsz() == null) { throw new IllegalStateException("INSZ number must be given"); }
            if(getCurrentYear() == null && getReferenceYear() == null) { throw new IllegalStateException("Current year or reference year must be given"); }
            if(getCurrentYear() != null && getReferenceYear() != null) { throw new IllegalStateException("Current year and reference year cannot be given at the same time."); }

            return new GeefLeefLoonBedragenRequest(
                    getInsz(),
                    getRegistration(),
                    getCurrentYear(),
                    getReferenceYear(),
                    getNumberOfYearsAgo()
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Nullable
    private final Year currentYear;

    @Nullable
    private final Year referenceYear;

    @Nullable
    private final Integer numberOfYearsAgo;

    public GeefLeefLoonBedragenRequest(
            @NotNull INSZNumber insz,
            @NotNull Registration registration,
            @Nullable Year currentYear,
            @Nullable Year referenceYear,
            @Nullable Integer numberOfYearsAgo) {
        super(insz, registration);
        this.currentYear = currentYear;
        this.referenceYear = referenceYear;
        this.numberOfYearsAgo = numberOfYearsAgo;
    }

    @Override
    public MagdaServiceIdentification magdaServiceIdentification() {
        return new MagdaServiceIdentification("GeefLeefloonbedragen", "02.00.0000");
    }

    @Override
    protected void fillIn(MagdaDocument request, UUID requestId, MagdaRegistrationInfo magdaRegistrationInfo) {
        fillInCommonFields(request, requestId, magdaRegistrationInfo);
        request.setValue("//Criteria/Jaar", getCurrentYear() != null ? getCurrentYear().toString() : getReferenceYear().toString());
        request.setValue("//Criteria/Jaar/@Huidig", getCurrentYear() == null ? "0" : "1" );
        request.removeNode("//Criteria/AantalJaarTerug");
        if(getNumberOfYearsAgo() != null)
        {
            request.createTextNode("//Criteria", "AantalJaarTerug", getNumberOfYearsAgo().toString());
        }
    }
}
