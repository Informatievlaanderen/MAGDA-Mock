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

@Getter
@ToString
public class RegistreerUitschrijvingRequest extends MagdaRequest {

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

        public RegistreerUitschrijvingRequest build() {
            return new RegistreerUitschrijvingRequest(
                    getInsz(),
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

    private RegistreerUitschrijvingRequest(String insz, LocalDate start, LocalDate einde) {
        super(insz, insz);
        this.start = start;
        this.einde = einde;
    }

    @Override
    public MagdaServiceIdentification magdaServiceIdentification() {
        return new MagdaServiceIdentification("RegistreerUitschrijving", "02.00.0000");
    }

    @Override
    protected void fillIn(MagdaDocument request, MagdaRegistrationInfo magdaRegistrationInfo) {
        fillInCommonFields(request, magdaRegistrationInfo);

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
        if(getStart() != null || getEinde() != null) {
            request.createNode("//Vragen/Vraag/Inhoud/Uitschrijving", "Periode");

            var dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

            if(getStart() != null) {
                request.createTextNode("//Vraag/Inhoud/Uitschrijving/Periode", "Begin", getStart().format(dateFormatter));
            }

            if(getEinde() != null) {
                request.createTextNode("//Vraag/Inhoud/Uitschrijving/Periode", "Einde", getEinde().format(dateFormatter));
            }
        }
    }
}