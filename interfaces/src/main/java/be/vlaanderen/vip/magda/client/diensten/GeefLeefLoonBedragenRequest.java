package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;

import java.time.Year;

/**
 * A request to a "GeefBetalingenHandicap" MAGDA service allows you to consult the periods and amounts paid out in the context of the living wage for an INSZ.
 * Adds the following fields to the {@link PersonMagdaRequest}:
 * <ul>
 * <li>year: Year in 'YYYY' format. This date cannot be in the future.</li>
 * <li>currentYear: false when specified year is the reference year for the consultation; true when specified year is the current year for the consultation</li>
 * <li>numberOfYearsAgo: total number of years back in time. Values: 2 or 3. (Optional)</li>
 * </ul>
 *
 * @see <a href="file:resources/templates/GeefLeefLoonBedragen/02.00.0000/template.xml">XML template for this request type</a>
 * @see <a href="https://vlaamseoverheid.atlassian.net/wiki/spaces/MG/pages/1207599973/SocZek.GeefLeefLoonBedragen-02.00">More information on this request type</a>
 */
@Getter
@ToString
public class GeefLeefLoonBedragenRequest extends PersonMagdaRequest {

    public static class Builder extends PersonMagdaRequest.Builder<Builder> {

        @Getter(AccessLevel.PROTECTED)
        private Year year;
        @Getter(AccessLevel.PROTECTED)
        private Boolean currentYear;
        @Getter(AccessLevel.PROTECTED)
        private Integer numberOfYearsAgo;

        public Builder year(Year year) {
            this.year = year;
            return this;
        }

        public Builder currentYear(Boolean currentYear) {
            this.currentYear = currentYear;
            return this;
        }

        public Builder numberOfYearsAgo(Integer numberOfYearsAgo) {
            this.numberOfYearsAgo = numberOfYearsAgo;
            return this;
        }

        public GeefLeefLoonBedragenRequest build() {
            if(getInsz() == null) { throw new IllegalStateException("INSZ number must be given"); }
            if(year == null) { throw new IllegalStateException("Year must be given"); }
            if(currentYear == null) { throw new IllegalStateException("CurrentYear must be given"); }

            return new GeefLeefLoonBedragenRequest(
                    getInsz(),
                    getRegistration(),
                    getYear(),
                    getCurrentYear(),
                    getNumberOfYearsAgo()
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @NotNull
    private final Year year;

    @NotNull
    private final Boolean currentYear;

    @Nullable
    private final Integer numberOfYearsAgo;

    public GeefLeefLoonBedragenRequest(
            @NotNull INSZNumber insz,
            @NotNull String registration,
            @NotNull Year year,
            @NotNull Boolean currentYear,
            @Nullable Integer numberOfYearsAgo) {
        super(insz, registration);
        this.year = year;
        this.currentYear = currentYear;
        this.numberOfYearsAgo = numberOfYearsAgo;
    }

    @Override
    public MagdaServiceIdentification magdaServiceIdentification() {
        return new MagdaServiceIdentification("GeefLeefLoonBedragen", "02.00.0000");
    }

    @Override
    protected void fillIn(MagdaDocument request, MagdaRegistrationInfo magdaRegistrationInfo) {
        fillInCommonFields(request, magdaRegistrationInfo);
        request.setValue("//Criteria/Jaar", getYear().toString());
        request.setValue("//Criteria/Jaar/@Huidig", getCurrentYear() ? "1" : "0" );
        request.removeNode("//Criteria/AantalJaarTerug");
        if(getNumberOfYearsAgo() != null)
        {
            request.createTextNode("//Criteria", "AantalJaarTerug", getNumberOfYearsAgo().toString());
        }
    }
}
