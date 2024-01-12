package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * A request to a "GeefBetalingenHandicap" MAGDA service, which provides information regarding disability payments for an INSZ.
 * Adds the following fields to the {@link PersonMagdaRequest}:
 * <ul>
 * <li>startDate: the start date of the period</li>
 * <li>endDate: the end date of the period</li>
 * <li>consultDGPH: consult the source DGPH</li>
 * <li>consultVSB: consult the source VSB</li>
 * <li>consultIrisCare: consult the source IrisCare</li>
 * <li>consultNicCin: consult the NicCin</li>
 * </ul>
 *
 * @see <a href="file:resources/templates/GeefBetalingenHandicap/03.00.0000/template.xml">XML template for this request type</a>
 * @see <a href="https://vlaamseoverheid.atlassian.net/wiki/spaces/MG/pages/1640235117/SocZek.GeefBetalingenHandicap-03.00">More information on this request type</a>
 */
@Getter
@ToString
public class GeefBetalingenHandicapRequest extends PersonMagdaRequest {

    public static class Builder extends PersonMagdaRequest.Builder<Builder> {

        @Getter(AccessLevel.PROTECTED)
        private LocalDate startDate;
        @Getter(AccessLevel.PROTECTED)
        private LocalDate endDate;
        @Getter(AccessLevel.PROTECTED)
        private Boolean consultDGPH;
        @Getter(AccessLevel.PROTECTED)
        private Boolean consultVSB;
        @Getter(AccessLevel.PROTECTED)
        private Boolean consultIrisCare;
        @Getter(AccessLevel.PROTECTED)
        private Boolean consultNicCin;

        public Builder startDate(LocalDate date) {
            this.startDate = date;
            return this;
        }

        public Builder endDate(LocalDate date) {
            this.endDate = date;
            return this;
        }

        public Builder consultDGPH(Boolean value) {
            this.consultDGPH = value;
            return this;
        }

        public Builder consultVSB(Boolean value) {
            this.consultVSB = value;
            return this;
        }

        public Builder consultIrisCare(Boolean value) {
            this.consultIrisCare = value;
            return this;
        }

        public Builder consultNicCin(Boolean value) {
            this.consultNicCin = value;
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
                    getConsultDGPH(),
                    getConsultVSB(),
                    getConsultIrisCare(),
                    getConsultNicCin()
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
    private final Boolean consultDGPH;
    @Nullable
    private final Boolean consultVSB;
    @Nullable
    private final Boolean consultIrisCare;
    @Nullable
    private final Boolean consultNicCin;

    public GeefBetalingenHandicapRequest(
            @NotNull INSZNumber insz,
            @NotNull String registration,
            @NotNull LocalDate startDate,
            @NotNull LocalDate endDate,
            @Nullable Boolean consultDGPH,
            @Nullable Boolean consultVSB,
            @Nullable Boolean consultIrisCare,
            @Nullable Boolean consultNicCin) {
        super(insz, registration);
        this.startDate = startDate;
        this.endDate = endDate;
        this.consultDGPH = consultDGPH;
        this.consultVSB = consultVSB;
        this.consultIrisCare = consultIrisCare;
        this.consultNicCin = consultNicCin;
    }

    @Override
    public MagdaServiceIdentification magdaServiceIdentification() {
        return new MagdaServiceIdentification("GeefBetalingenHandicap", "03.00.0000");
    }

    @Override
    protected void fillIn(MagdaDocument request, MagdaRegistrationInfo magdaRegistrationInfo) {
        fillInCommonFields(request, magdaRegistrationInfo);

        var dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        request.setValue("//ConsultPaymentsCriteria/ssin", getInsz().getValue());
        request.setValue("//ConsultPaymentsCriteria/period/beginDatum", getStartDate().format(dateFormatter));
        request.setValue("//ConsultPaymentsCriteria/period/eindDatum", getEndDate().format(dateFormatter));
        if(getConsultDGPH() != null) {
            request.createTextNode("//ConsultPaymentsCriteria/handicapAuthenticSources", "DGPH", getBooleanTextValue(getConsultDGPH()));
        }
        if(getConsultVSB() != null) {
            request.createTextNode("//ConsultPaymentsCriteria/handicapAuthenticSources", "VSB", getBooleanTextValue(getConsultVSB()));
        }
        if(getConsultIrisCare() != null) {
            request.createTextNode("//ConsultPaymentsCriteria/handicapAuthenticSources", "IrisCare", getBooleanTextValue(getConsultIrisCare()));
        }
        if(getConsultNicCin() != null) {
            request.createTextNode("//ConsultPaymentsCriteria/handicapAuthenticSources", "NicCin", getBooleanTextValue(getConsultNicCin()));
        }
    }

    private String getBooleanTextValue(Boolean value) {
        return value ? "true" : "false";
    }
}
