package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.Registration;
import be.vlaanderen.vip.magda.client.diensten.subject.KBONumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.Instant;
import java.time.Year;
import java.util.UUID;

/**
 * A request to a "GeefJaarrekeningen" MAGDA service, which provides information on a company's annual accounts.
 * Adds the following fields to the {@link CompanyMagdaRequest}:
 * <ul>
 * <li>financialYear: the financial year of the accounts.</li>
 * </ul>
 *
 * @see <a href="file:resources/templates/GeefJaarrekeningen/02.00.0000/template.xml">XML template for this request type</a>
 * @see <a href="https://vlaamseoverheid.atlassian.net/wiki/spaces/MG/pages/499975764/Onderneming.GeefJaarrekeningen-02.00">More information on this request type</a>
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class GeefJaarrekeningenRequest extends CompanyMagdaRequest {

    public static class Builder extends CompanyMagdaRequest.Builder<Builder> {

        @Getter(AccessLevel.PROTECTED)
        private Year financialYear;

        public Builder financialYear(Year financialYear) {
            this.financialYear = financialYear;
            return this;
        }

        public GeefJaarrekeningenRequest build() {
            if(getKboNumber() == null) { throw new IllegalStateException("KBO number must be given"); }
            if(getFinancialYear() == null) { throw new IllegalStateException("Financial year must be given"); }

            return new GeefJaarrekeningenRequest(
                    getKboNumber(),
                    getRegistration(),
                    getFinancialYear()
            );
        }
    }

    @NotNull
    private final Year financialYear;

    public static Builder builder() {
        return new Builder();
    }

    private GeefJaarrekeningenRequest(
            @NotNull KBONumber kboNumber,
            @NotNull Registration registratie,
            @NotNull Year financialYear) {
        super(kboNumber, registratie);
        this.financialYear = financialYear;
    }

    @Override
    public MagdaServiceIdentification magdaServiceIdentification() {
        return new MagdaServiceIdentification("GeefJaarrekeningen", "02.00.0000");
    }

    @Override
    protected void fillIn(MagdaDocument request, UUID requestId, MagdaRegistrationInfo magdaRegistrationInfo, Instant instant) {
        fillInCommonFields(request, requestId, magdaRegistrationInfo, instant);


        request.setValue("//Criteria/Boekjaar", getFinancialYear().toString());
    }
}