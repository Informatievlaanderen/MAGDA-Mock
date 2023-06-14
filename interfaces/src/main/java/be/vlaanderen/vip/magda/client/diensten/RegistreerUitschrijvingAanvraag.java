package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.Aanvraag;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentificatie;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Getter
@ToString
public class RegistreerUitschrijvingAanvraag extends Aanvraag {

    private final LocalDate start;
    private final LocalDate einde;

    public RegistreerUitschrijvingAanvraag(String insz, LocalDate start, LocalDate einde) {
        super(insz);
        this.start = start;
        this.einde = einde;
    }

    @Override
    public MagdaServiceIdentificatie magdaService() {
        return new MagdaServiceIdentificatie("RegistreerUitschrijving", "02.00.0000");
    }

    @Override
    public void fillIn(MagdaDocument request, MagdaRegistrationInfo magdaRegistrationInfo) {
        super.fillIn(request, magdaRegistrationInfo);

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