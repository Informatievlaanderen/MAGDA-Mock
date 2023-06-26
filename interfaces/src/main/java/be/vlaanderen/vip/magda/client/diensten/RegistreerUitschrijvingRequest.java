package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaRequest;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.diensten.subject.SubjectIdentificationNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * A request to a "RegistreerUitschrijving" MAGDA service, which files deregistrations.
 * Adds the following fields to the {@link MagdaRequest}:
 * <ul>
 * <li>insz: the INSZ number of the party about which the information is requested</li>
 * <li>startDate: the start date of the deregistration</li>
 * <li>endDate: the end date of the deregistration</li>
 * </ul>
 *
 * @see <a href="file:resources/templates/RegistreerUitschrijving/02.00.0000/template.xml">XML template for this request type</a>
 */
@Getter
@ToString
public class RegistreerUitschrijvingRequest extends MagdaRequest {

    public static class Builder<SELF extends Builder<SELF>> extends MagdaRequest.Builder<SELF> {

        @Getter(AccessLevel.PROTECTED)
        private INSZNumber insz;
        @Getter(AccessLevel.PROTECTED)
        private LocalDate startDate;
        @Getter(AccessLevel.PROTECTED)
        private LocalDate endDate;

        @SuppressWarnings("unchecked")
        public SELF insz(INSZNumber insz) {
            this.insz = insz;
            return (SELF) this;
        }

        public SELF insz(String insz) {
            return insz(INSZNumber.of(insz));
        }

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

        public RegistreerUitschrijvingRequest build() {
            if(getInsz() == null) { throw new IllegalStateException("INSZ number must be given"); }

            return new RegistreerUitschrijvingRequest(
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
    private final INSZNumber insz;
    private final LocalDate startDate;
    private final LocalDate endDate;

    private RegistreerUitschrijvingRequest(
            @NotNull INSZNumber insz,
            @NotNull String registratie,
            LocalDate startDate,
            LocalDate endDate) {
        super(registratie);
        this.insz = insz;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public MagdaServiceIdentification magdaServiceIdentification() {
        return new MagdaServiceIdentification("RegistreerUitschrijving", "02.00.0000");
    }

    @Override
    public SubjectIdentificationNumber getSubject() {
        return insz;
    }

    @Override
    protected void fillIn(MagdaDocument request, MagdaRegistrationInfo magdaRegistrationInfo) {
        fillInCommonFields(request, magdaRegistrationInfo);

        request.setValue("//INSZ", getInsz().getValue());

        setDateFields(request);
        request.setValue("//Vraag/Inhoud/Uitschrijving/Identificatie", magdaRegistrationInfo.getIdentification());

        var hoedanigheidscode = magdaRegistrationInfo.getHoedanigheidscode();
        if(hoedanigheidscode.isEmpty()) {
            request.removeNode("//Vraag/Inhoud/Uitschrijving/Hoedanigheid");
        } else {
            request.setValue("//Vraag/Inhoud/Uitschrijving/Hoedanigheid", hoedanigheidscode.get());
        }
    }
    
    private void setDateFields(MagdaDocument request) {
        if(getStartDate() != null || getEndDate() != null) {
            request.createNode("//Vragen/Vraag/Inhoud/Uitschrijving", "Periode");

            var dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

            if(getStartDate() != null) {
                request.createTextNode("//Vraag/Inhoud/Uitschrijving/Periode", "Begin", getStartDate().format(dateFormatter));
            }

            if(getEndDate() != null) {
                request.createTextNode("//Vraag/Inhoud/Uitschrijving/Periode", "Einde", getEndDate().format(dateFormatter));
            }
        }
    }
}