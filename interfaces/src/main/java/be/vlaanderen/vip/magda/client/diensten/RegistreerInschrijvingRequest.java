package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * A request to a "RegistreerInschrijving" MAGDA service, which files registrations.
 * Adds the following fields to the {@link PersonMagdaRequest}:
 * <ul>
 * <li>startDate: the start date of the registration</li>
 * <li>endDate: the end date of the registration</li>
 * </ul>
 *
 * @see <a href="file:resources/templates/RegistreerInschrijving/02.00.0000/template.xml">XML template for this request type</a>
 */
@Getter
@ToString
public class RegistreerInschrijvingRequest extends PersonMagdaRequest {

    public static class Builder<SELF extends Builder<SELF>> extends PersonMagdaRequest.Builder<SELF> {

        @Getter(AccessLevel.PROTECTED)
        private LocalDate startDate;
        @Getter(AccessLevel.PROTECTED)
        private LocalDate endDate;

        @SuppressWarnings("unchecked")
        public SELF startDate(LocalDate startDate) {
            this.startDate = startDate;
            return (SELF) this;
        }

        @SuppressWarnings("unchecked")
        public SELF endDate(LocalDate endDate) {
            this.endDate = endDate;
            return (SELF) this;
        }

        public RegistreerInschrijvingRequest build() {
            if(getInsz() == null) { throw new IllegalStateException("INSZ number must be given"); }
            if(getStartDate() == null) { throw new IllegalStateException("Start date must be given"); }
            if(getEndDate() == null) { throw new IllegalStateException("End date must be given"); }

            return new RegistreerInschrijvingRequest(
                    getInsz(),
                    getRegistration(),
                    getStartDate(),
                    getEndDate()
            );
        }
    }

    public static Builder<? extends Builder<?>> builder() {
        return new Builder();
    }

    @NotNull
    private final LocalDate startDate;
    @NotNull
    private final LocalDate endDate;

    private RegistreerInschrijvingRequest(
            @NotNull INSZNumber insz,
            @NotNull String registratie,
            @NotNull LocalDate startDate,
            @NotNull LocalDate endDate) {
        super(insz, registratie);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public MagdaServiceIdentification magdaServiceIdentification() {
        return new MagdaServiceIdentification("RegistreerInschrijving", "02.00.0000");
    }

    @Override
    protected void fillIn(MagdaDocument request, MagdaRegistrationInfo magdaRegistrationInfo) {
        fillInCommonFields(request, magdaRegistrationInfo);

        var dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        request.setValue("//Vraag/Inhoud/Inschrijving/Periode/Begin", getStartDate().format(dateFormatter));
        request.setValue("//Vraag/Inhoud/Inschrijving/Periode/Einde", getEndDate().format(dateFormatter));
        request.setValue("//Vragen/Vraag/Inhoud/Inschrijving/Identificatie", magdaRegistrationInfo.getIdentification());

        var hoedanigheidscode = magdaRegistrationInfo.getHoedanigheidscode();
        if(hoedanigheidscode.isEmpty()) {
            request.removeNode("//Vragen/Vraag/Inhoud/Inschrijving/Hoedanigheid");
        } else {
            request.setValue("//Vragen/Vraag/Inhoud/Inschrijving/Hoedanigheid", hoedanigheidscode.get());
        }
    }
}