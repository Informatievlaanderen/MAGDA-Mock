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

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;

/**
 * A request to the "Onderwijs.GeefHistoriekInschrijving" MAGDA service, which provides education enrollment histories for a person.
 * Adds the following fields to the {@link PersonMagdaRequest}:
 * <ul>
 * <li>startDate: the start date of the period</li>
 * <li>endDate: the end date of the period</li>
 * <li>sources: the sources to be consulted (optional)</li>
 * </ul>
 *
 * @see <a href="file:resources/templates/Onderwijs.GeefHistoriekInschrijvingDienst/02.01.0000/template.xml">XML template for this request type</a>
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class GeefHistoriekInschrijvingRequest extends PersonMagdaRequest {

    public static class Builder extends PersonMagdaRequest.Builder<Builder> {

        @Getter(AccessLevel.PROTECTED)
        private LocalDate startDate;
        @Getter(AccessLevel.PROTECTED)
        private LocalDate endDate;
        @Getter(AccessLevel.PROTECTED)
        private Set<EducationEnrollmentSource> sources;

        public Builder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder sources(Set<EducationEnrollmentSource> sources) {
            this.sources = sources;
            return this;
        }

        public GeefHistoriekInschrijvingRequest build() {
            if(getInsz() == null) { throw new IllegalStateException("INSZ number must be given"); }
            if(getStartDate() == null) { throw new IllegalStateException("Start date must be given"); }
            if(getEndDate() == null) { throw new IllegalStateException("End date must be given"); }

            return new GeefHistoriekInschrijvingRequest(
                    getInsz(),
                    getRegistration(),
                    getStartDate(),
                    getEndDate(),
                    getSources()
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
    @Nullable
    private final Set<EducationEnrollmentSource> sources;

    private GeefHistoriekInschrijvingRequest(
            @NotNull INSZNumber insz,
            @NotNull Registration registratie,
            @NotNull LocalDate startDate,
            @NotNull LocalDate endDate,
            @Nullable Set<EducationEnrollmentSource> sources) {
        super(insz, registratie);
        this.startDate = startDate;
        this.endDate = endDate;
        this.sources = sources;
    }

    @Override
    public MagdaServiceIdentification magdaServiceIdentification() {
        return new MagdaServiceIdentification("Onderwijs.GeefHistoriekInschrijving", "02.01.0000");
    }

    @Override
    protected void fillIn(MagdaDocument request, UUID requestId, MagdaRegistrationInfo magdaRegistrationInfo) {
        fillInCommonFields(request, requestId, magdaRegistrationInfo);

        request.setValue("//Criteria/Periode/Begin", startDate.format(ISO_LOCAL_DATE));
        request.setValue("//Criteria/Periode/Einde", endDate.format(ISO_LOCAL_DATE));
        if(sources != null) {
            request.createNode("//Criteria", "Bronnen");
            sources.forEach(source -> request.createTextNode("//Criteria/Bronnen", "Bron", source.getValue()));
        }
    }
}
