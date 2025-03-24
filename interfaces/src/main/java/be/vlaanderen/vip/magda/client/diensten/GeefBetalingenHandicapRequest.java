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

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

/**
 * A request to a "GeefBetalingenHandicap" MAGDA service, which provides information regarding disability payments for an INSZ.
 * Adds the following fields to the {@link PersonMagdaRequest}:
 * <ul>
 * <li>startDate: the start date of the period</li>
 * <li>endDate: the end date of the period</li>
 * <li>sources: include the sources to consult</li>
 * </ul>
 *
 * @see <a href="file:resources/templates/GeefBetalingenHandicap/03.00.0000/template.xml">XML template for this request type</a>
 * @see <a href="https://vlaamseoverheid.atlassian.net/wiki/spaces/MG/pages/1640235117/SocZek.GeefBetalingenHandicap-03.00">More information on this request type</a>
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class GeefBetalingenHandicapRequest extends PersonMagdaRequest {

    public static class Builder extends PersonMagdaRequest.Builder<Builder> {

        @Getter(AccessLevel.PROTECTED)
        private LocalDate startDate;
        @Getter(AccessLevel.PROTECTED)
        private LocalDate endDate;
        @Getter(AccessLevel.PROTECTED)
        private Set<HandicapAuthenticSourceType> sources;

        public Builder startDate(LocalDate date) {
            this.startDate = date;
            return this;
        }

        public Builder endDate(LocalDate date) {
            this.endDate = date;
            return this;
        }

        public Builder sources(Set<HandicapAuthenticSourceType> sources) {
            this.sources = sources;
            return this;
        }

        public GeefBetalingenHandicapRequest build() {
            if(getInsz() == null) { throw new IllegalStateException("INSZ number must be given"); }
            if(getStartDate() == null) { throw new IllegalStateException("Start date must be given"); }
            if(getEndDate() == null) { throw new IllegalStateException("End date must be given"); }

            return new GeefBetalingenHandicapRequest(
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
    private final Set<HandicapAuthenticSourceType> sources;

    public GeefBetalingenHandicapRequest(
            @NotNull INSZNumber insz,
            @NotNull Registration registration,
            @NotNull LocalDate startDate,
            @NotNull LocalDate endDate,
            @Nullable Set<HandicapAuthenticSourceType> sources) {
        super(insz, registration);
        this.startDate = startDate;
        this.endDate = endDate;
        this.sources = sources;
    }

    @Override
    public MagdaServiceIdentification magdaServiceIdentification() {
        return new MagdaServiceIdentification("GeefBetalingenHandicap", "03.00.0000");
    }

    @Override
    protected void fillIn(MagdaDocument request, UUID requestId, MagdaRegistrationInfo magdaRegistrationInfo, Instant instant) {
        fillInCommonFields(request, requestId, magdaRegistrationInfo, instant);

        var dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        request.setValue("//ConsultPaymentsCriteria/ssin", getInsz().getValue());
        request.setValue("//ConsultPaymentsCriteria/period/beginDate", getStartDate().format(dateFormatter));
        request.setValue("//ConsultPaymentsCriteria/period/endDate", getEndDate().format(dateFormatter));
        Arrays.stream(HandicapAuthenticSourceType.values()).forEach(x -> {
            request.createTextNode("//ConsultPaymentsCriteria/handicapAuthenticSources", x.getTypeString(), getSources() != null && getSources().contains(x) ? "true" : "false");
        });
    }

    public enum HandicapAuthenticSourceType {
        DGPH("DGPH"),
        VSB("VSB"),
        IRISCARE("IrisCare"),
        NICCIN("NicCin");

        private final String typeString;

        HandicapAuthenticSourceType(String typeString) {
            this.typeString = typeString;
        }

        public String getTypeString() {
            return typeString;
        }


    }
}
