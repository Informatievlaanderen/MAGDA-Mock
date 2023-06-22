package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaRequest;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * A request to a "RegistreerInschrijving" MAGDA service, which files registrations.
 * Adds the following fields to the {@link MagdaRequest}:
 * <ul>
 * <li>start: the start date of the registration</li>
 * <li>end: the end date of the registration</li>
 * </ul>
 *
 * @see <a href="file:resources/templates/RegistreerInschrijving/02.00.0000/template.xml">XML template for this request type</a>
 */
@Getter
@ToString
public class RegistreerInschrijvingRequest extends MagdaRequest {

    public static class Builder<SELF extends Builder<SELF>> extends MagdaRequest.Builder<SELF> {

        @Getter(AccessLevel.PROTECTED)
        private LocalDate start;
        @Getter(AccessLevel.PROTECTED)
        private LocalDate einde;

        @SuppressWarnings("unchecked")
        public SELF start(LocalDate start) {
            this.start = start;
            return (SELF) this;
        }

        @SuppressWarnings("unchecked")
        public SELF einde(LocalDate einde) {
            this.einde = einde;
            return (SELF) this;
        }

        public RegistreerInschrijvingRequest build() {
            return new RegistreerInschrijvingRequest(
                    getRequestingPartyInsz(),
                    getSubjectInsz(),
                    getRegistratie(),
                    getStart(),
                    getEinde()
            );
        }
    }

    public static Builder<? extends Builder<?>> builder() {
        return new Builder();
    }

    private final LocalDate start;
    private final LocalDate einde;

    private RegistreerInschrijvingRequest(String requestingPartyInsz, String subjectInsz, String registratie, LocalDate start, LocalDate einde) {
        super(requestingPartyInsz, subjectInsz, registratie);
        this.start = start;
        this.einde = einde;
    }

    @Override
    public MagdaServiceIdentification magdaServiceIdentification() {
        return new MagdaServiceIdentification("RegistreerInschrijving", "02.00.0000");
    }

    @Override
    protected void fillIn(MagdaDocument request, MagdaRegistrationInfo magdaRegistrationInfo) {
        fillInCommonFields(request, magdaRegistrationInfo);

        var dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        request.setValue("//Vraag/Inhoud/Inschrijving/Periode/Begin", getStart().format(dateFormatter));
        request.setValue("//Vraag/Inhoud/Inschrijving/Periode/Einde", getEinde().format(dateFormatter));
        request.setValue("//Vragen/Vraag/Inhoud/Inschrijving/Identificatie", magdaRegistrationInfo.getIdentification());

        var hoedanigheidscode = magdaRegistrationInfo.getHoedanigheidscode();
        if(hoedanigheidscode.isEmpty()) {
            request.removeNode("//Vragen/Vraag/Inhoud/Inschrijving/Hoedanigheid");
        } else {
            request.setValue("//Vragen/Vraag/Inhoud/Inschrijving/Hoedanigheid", hoedanigheidscode.get());
        }
    }
}