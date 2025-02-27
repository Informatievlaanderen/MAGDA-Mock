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
import java.util.List;
import java.util.UUID;

/**
 * A request to a "GeefAanslagBiljet" MAGDA service, which provides tax bills.
 * Adds the following fields to the {@link PersonMagdaRequest}:
 * <ul>
 * <li>incomeYear: the income year to which the tax bill pertains</li>
 * <li>ipcalCodes: the list of IPCAL codes for the tax bill (optional)</li>
 * </ul>
 *
 * @see <a href="file:resources/templates/GeefAanslagbiljetPersonenbelasting/02.00.0000/template.xml">XML template for this request type</a>
 * @see <a href="https://vlaamseoverheid.atlassian.net/wiki/spaces/MG/pages/499777761/Inkomen.GeefAanslagbiljetPersonenbelasting-02.00">More information on this request type</a>
 * @see <a href="https://vlaamseoverheid.atlassian.net/wiki/spaces/MG/pages/499974512/Codetabellen+GeefAanslagbiljetPersonenbelasting-02.00">More information on IPCAL codes</a>
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class GeefAanslagbiljetPersonenbelastingRequest extends PersonMagdaRequest {

    public static class Builder extends PersonMagdaRequest.Builder<Builder> {

        @Getter(AccessLevel.PROTECTED)
        private Year incomeYear;
        @Getter(AccessLevel.PROTECTED)
        private List<String> ipcalCodes;

        public Builder incomeYear(Year incomeYear) {
            this.incomeYear = incomeYear;
            return this;
        }

        public Builder ipcalCodes(List<String> ipcalCodes) {
            this.ipcalCodes = ipcalCodes;
            return this;
        }

        public GeefAanslagbiljetPersonenbelastingRequest build() {
            if(getInsz() == null) { throw new IllegalStateException("INSZ number must be given"); }
            if(getIncomeYear() == null) { throw new IllegalStateException("Income year must be given"); }

            return new GeefAanslagbiljetPersonenbelastingRequest(
                    getInsz(),
                    getRegistration(),
                    getIncomeYear(),
                    getIpcalCodes()
            );
        }
    }

    /**
     * @deprecated use 'builder2' instead
     */
    @Deprecated
    public static Builder builder() {
        return new Builder()
                // these are the values in the template.xml file that accords to this request.
                // to retain backward compatibility with the fact that these fields used to be static constant values, we provide them here.
                .incomeYear(Year.of(2011))
                .ipcalCodes(List.of("7555", "7557"));
    }

    public static Builder builder2() {
        return new Builder();
    }

    @NotNull
    private final Year incomeYear;
    @Nullable
    private final List<String> ipcalCodes;

    private GeefAanslagbiljetPersonenbelastingRequest(
            @NotNull INSZNumber insz,
            @NotNull Registration registratie,
            @NotNull Year incomeYear,
            @Nullable List<String> ipcalCodes) {
        super(insz, registratie);
        this.incomeYear = incomeYear;
        this.ipcalCodes = ipcalCodes;
    }

    @Override
    public MagdaServiceIdentification magdaServiceIdentification() {
        return new MagdaServiceIdentification("GeefAanslagbiljetPersonenbelasting", "02.00.0000");
    }

    @Override
    protected void fillIn(MagdaDocument request, UUID requestId, MagdaRegistrationInfo magdaRegistrationInfo) {
        fillInCommonFields(request, requestId, magdaRegistrationInfo);

        request.setValue("//Vragen/Vraag/Inhoud/Criteria/Inkomensjaar", incomeYear.toString());

        request.removeNode("//Vragen/Vraag/Inhoud/Criteria/IPCALCodes");
        if(ipcalCodes != null) {
            request.createNode("//Vragen/Vraag/Inhoud/Criteria", "IPCALCodes");
            for(var ipcalCode : ipcalCodes) {
                request.createTextNode("//Vragen/Vraag/Inhoud/Criteria/IPCALCodes", "IPCALCode", ipcalCode);
            }
        }
    }
}