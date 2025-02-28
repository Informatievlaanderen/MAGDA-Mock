package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.Registration;
import be.vlaanderen.vip.magda.client.diensten.subject.SubjectIdentificationNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * A request to a "RegistreerInschrijving" (specifically version 02.01) MAGDA service, which files registrations.
 * Adds the following fields to the {@link SubjectMagdaRequest}:
 * <ul>
 * <li>startDate: the start date of the registration</li>
 * <li>endDate: the end date of the registration</li>
 * </ul>
 *
 * @see <a href="file:resources/templates/RegistreerInschrijving/02.01.0000/template.xml">XML template for this request type</a>
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class RegistreerInschrijving0201Request extends SubjectMagdaRequest {

    public static class Builder extends SubjectMagdaRequest.Builder<Builder> {

        @Getter(AccessLevel.PROTECTED)
        private LocalDate startDate;
        @Getter(AccessLevel.PROTECTED)
        private LocalDate endDate;

        public Builder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public RegistreerInschrijving0201Request build() {
            if(getSubject() == null) { throw new IllegalStateException("Subject identification number must be given"); }
            if(getStartDate() == null) { throw new IllegalStateException("Start date must be given"); }
            if(getEndDate() == null) { throw new IllegalStateException("End date must be given"); }

            return new RegistreerInschrijving0201Request(
                    getSubject(),
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

    private RegistreerInschrijving0201Request(
            @NotNull SubjectIdentificationNumber subject,
            @NotNull Registration registratie,
            @NotNull LocalDate startDate,
            @NotNull LocalDate endDate) {
        super(subject, registratie);
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public MagdaServiceIdentification magdaServiceIdentification() {
        return new MagdaServiceIdentification("RegistreerInschrijving", "02.01.0000");
    }

    @Override
    protected void fillIn(MagdaDocument request, UUID requestId, MagdaRegistrationInfo magdaRegistrationInfo) {
        fillInCommonFields(request, requestId, magdaRegistrationInfo);

        var dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        request.setValue("//Vraag/Inhoud/Inschrijving/Periode/Start", getStartDate().format(dateFormatter));
        request.setValue("//Vraag/Inhoud/Inschrijving/Periode/Einde", getEndDate().format(dateFormatter));
        request.setValue("//Vraag/Inhoud/Inschrijving/BetrokkenSubject/Type", getSubject().getSubjectType().getTypeString());
        request.setValue("//Vraag/Inhoud/Inschrijving/BetrokkenSubject/Subjecten/Subject/Type", getSubject().getSubjectType().getTypeString());
        request.setValue("//Vraag/Inhoud/Inschrijving/BetrokkenSubject/Subjecten/Subject/Sleutel", getSubject().getValue());
        request.setValue("//Vragen/Vraag/Inhoud/Inschrijving/Identificatie", magdaRegistrationInfo.getIdentification());

        var hoedanigheidscode = magdaRegistrationInfo.getHoedanigheidscode();
        if(hoedanigheidscode.isEmpty()) {
            request.removeNode("//Vragen/Vraag/Inhoud/Inschrijving/Hoedanigheid");
        } else {
            request.setValue("//Vragen/Vraag/Inhoud/Inschrijving/Hoedanigheid", hoedanigheidscode.get());
        }
    }
}