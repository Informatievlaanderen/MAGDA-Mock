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
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A request to a "GeefHistoriekGezinssamenstelling" MAGDA service, which provides information on the history of family composition for an INSZ.
 * Adds the following fields to the {@link PersonMagdaRequest}:
 * <ul>
 * <li>date: the date on which the information on the social status was in effect</li>
 * <li>dateOp: the date operation: false when history from this date; true when history on this date</li>
 * </ul>
 *
 * @see <a href="file:resources/templates/GeefHistoriekGezinssamenstelling/02.02.0000/template.xml">XML template for this request type</a>
 * @see <a href="https://vlaamseoverheid.atlassian.net/wiki/spaces/MG/pages/1124172022/Persoon.GeefHistoriekGezinssamenstelling-02.02">More information on this request type</a>
 */
@Getter
@ToString
public class GeefHistoriekGezinssamenstellingRequest extends PersonMagdaRequest {

    public static class Builder extends PersonMagdaRequest.Builder<Builder> {

        @Getter(AccessLevel.PROTECTED)
        private LocalDate date;
        @Getter(AccessLevel.PROTECTED)
        private Boolean dateOp;

        public Builder date(LocalDate date) {
            this.date = date;
            return this;
        }

        public Builder dateOp(Boolean dateOp) {
            this.dateOp = dateOp;
            return this;
        }

        public GeefHistoriekGezinssamenstellingRequest build() {
            if(getInsz() == null) { throw new IllegalStateException("INSZ number must be given"); }
            if(date == null) { throw new IllegalStateException("Date must be given"); }

            return new GeefHistoriekGezinssamenstellingRequest(
                    getInsz(),
                    getRegistration(),
                    getDate(),
                    getDateOp()
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private final LocalDate date;
    private final Boolean dateOp;

    public GeefHistoriekGezinssamenstellingRequest(
            @NotNull INSZNumber insz,
            @NotNull String registration,
            @NotNull LocalDate date,
            @Nullable Boolean dateOp) {
        super(insz, registration);
        this.date = date;
        this.dateOp = dateOp;
    }

    @Override
    public MagdaServiceIdentification magdaServiceIdentification() {
        return new MagdaServiceIdentification("GeefHistoriekGezinssamenstelling", "02.02.0000");
    }

    @Override
    protected void fillIn(MagdaDocument request, MagdaRegistrationInfo magdaRegistrationInfo) {
        fillInCommonFields(request, magdaRegistrationInfo);
        var dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        request.setValue("//Vragen/Vraag/Inhoud/Criteria/Datum", getDate().format(dateFormatter));
        request.removeAttribute("//Vragen/Vraag/Inhoud/Criteria/Datum/@Op");
        if(getDateOp() != null)
        {
            request.createAttribute("//Vragen/Vraag/Inhoud/Criteria/Datum", "Op", getDateOp() ? "1" : "0");
        }
    }
}
