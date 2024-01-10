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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
/**
 * A request to a "GeefLoopbaanARZA" MAGDA service, which provides information regarding self-employment for an INSZ.
 * Adds the following fields to the {@link PersonMagdaRequest}:
 * <ul>
 * <li>startDate: the start date of the period</li>
 * <li>endDate: the end date of the period</li>
 * </ul>
 *
 * @see <a href="file:resources/templates/GeefLoopbaanARZA/02.01.0000/template.xml">XML template for this request type</a>
 * @see <a href="https://vlaamseoverheid.atlassian.net/wiki/spaces/MG/pages/526909939/Werk.GeefLoopbaanARZA-02.01">More information on this request type</a>
 */
@Getter
@ToString
public class GeefLoopbaanARZARequest extends PersonMagdaRequest {

    public static class Builder extends PersonMagdaRequest.Builder<Builder> {
        @Getter(AccessLevel.PROTECTED)
        private LocalDate startDate;
        @Getter(AccessLevel.PROTECTED)
        private LocalDate endDate;

        public Builder startDate(LocalDate date) {
            this.startDate = date;
            return this;
        }

        public Builder endDate(LocalDate date) {
            this.endDate = date;
            return this;
        }

        public GeefLoopbaanARZARequest build() {
            if(getInsz() == null) { throw new IllegalStateException("INSZ number must be given"); }
            if(getStartDate() == null) { throw new IllegalStateException("Start date must be given"); }
            return new GeefLoopbaanARZARequest(
                    getInsz(),
                    getRegistration(),
                    getStartDate(),
                    getEndDate()
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @NotNull
    private final LocalDate startDate;

    @NotNull
    private final LocalDate endDate;

    public GeefLoopbaanARZARequest(
            @NotNull INSZNumber insz,
            @NotNull String registration,
            @NotNull LocalDate startDate,
            @Nullable LocalDate endDate) {
        super(insz, registration);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public MagdaServiceIdentification magdaServiceIdentification() {
        return new MagdaServiceIdentification("GeefLoopbaanARZA", "02.01.0000");
    }

    @Override
    protected void fillIn(MagdaDocument request, MagdaRegistrationInfo magdaRegistrationInfo) {
        fillInCommonFields(request, magdaRegistrationInfo);
        var dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

        request.setValue("//Criteria/Periode/Begin", getStartDate().format(dateFormatter));
        request.removeNode("//Criteria/Periode/Einde");
        if(getEndDate() !=  null)
        {
            request.createTextNode("//Criteria/Periode", "Einde", getEndDate().format(dateFormatter));
        }
    }
}