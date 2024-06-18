package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * A request to a "GeefHistoriekGezinssamenstelling" MAGDA service, which provides information on the history of family composition for an INSZ.
 * Adds the following fields to the {@link PersonMagdaRequest}:
 * <ul>
 * <li>onDate: search family composition on this date</li>
 * <li>fromDate: search family composition history from this date</li>
 * </ul>
 *
 * @see <a href="file:resources/templates/GeefHistoriekGezinssamenstelling/02.02.0000/template.xml">XML template for this request type</a>
 * @see <a href="https://vlaamseoverheid.atlassian.net/wiki/spaces/MG/pages/1124172022/Persoon.GeefHistoriekGezinssamenstelling-02.02">More information on this request type</a>
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class GeefHistoriekGezinssamenstellingRequest extends PersonMagdaRequest {

    public static class Builder extends PersonMagdaRequest.Builder<Builder> {

        @Getter(AccessLevel.PROTECTED)
        private LocalDate fromDate;
        @Getter(AccessLevel.PROTECTED)
        private LocalDate onDate;

        public Builder fromDate(LocalDate fromDate) {
            this.fromDate = fromDate;
            return this;
        }

        public Builder onDate(LocalDate onDate) {
            this.onDate = onDate;
            return this;
        }

        public GeefHistoriekGezinssamenstellingRequest build() {
            if(getInsz() == null) { throw new IllegalStateException("INSZ number must be given"); }
            if(getFromDate() == null && getOnDate() == null) { throw new IllegalStateException("FromDate or OnDate must be given"); }
            if(getFromDate() != null && getOnDate() != null) { throw new IllegalStateException("FromDate or OnDate cannot be given at the same time."); }

            return new GeefHistoriekGezinssamenstellingRequest(
                    getInsz(),
                    getRegistration(),
                    getFromDate(),
                    getOnDate()
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private final LocalDate fromDate;
    private final LocalDate onDate;

    public GeefHistoriekGezinssamenstellingRequest(
            @NotNull INSZNumber insz,
            @NotNull String registration,
            @NotNull LocalDate fromDate,
            @Nullable LocalDate onDate) {
        super(insz, registration);
        this.fromDate = fromDate;
        this.onDate = onDate;
    }

    @Override
    public MagdaServiceIdentification magdaServiceIdentification() {
        return new MagdaServiceIdentification("GeefHistoriekGezinssamenstelling", "02.02.0000");
    }

    @Override
    protected void fillIn(MagdaDocument request, UUID requestId, MagdaRegistrationInfo magdaRegistrationInfo) {
        fillInCommonFields(request, requestId, magdaRegistrationInfo);
        var dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        request.setValue("//Vragen/Vraag/Inhoud/Criteria/Datum", getFromDate() != null ? getFromDate().format(dateFormatter) : getOnDate().format(dateFormatter));
        request.removeAttribute("//Vragen/Vraag/Inhoud/Criteria/Datum/@Op");
        request.createAttribute("//Vragen/Vraag/Inhoud/Criteria/Datum", "Op", getOnDate() != null ? "0" : "1");
    }
}
