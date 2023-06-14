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
public class RegistreerInschrijving0201Aanvraag extends Aanvraag {

    private final TypeInschrijving type;
    private final LocalDate start;
    private final LocalDate einde;

    public RegistreerInschrijving0201Aanvraag(TypeInschrijving type,String insz, LocalDate start, LocalDate einde) {
        super(insz);
        this.type = type;
        this.start = start;
        this.einde = einde;
    }

    @Override
    public MagdaServiceIdentificatie magdaService() {
        return new MagdaServiceIdentificatie("RegistreerInschrijving", "02.01.0000");
    }

    @Override
    public void fillIn(MagdaDocument request, MagdaRegistrationInfo magdaRegistrationInfo) {
        super.fillIn(request, magdaRegistrationInfo);

        var dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        request.setValue("//Vraag/Inhoud/Inschrijving/Periode/Start", getStart().format(dateFormatter));
        request.setValue("//Vraag/Inhoud/Inschrijving/Periode/Einde", getEinde().format(dateFormatter));
        request.setValue("//Vraag/Inhoud/Inschrijving/BetrokkenSubject/Type", getType().getTypeString());
        request.setValue("//Vraag/Inhoud/Inschrijving/BetrokkenSubject/Subjecten/Subject/Type", getType().getTypeString());
        request.setValue("//Vraag/Inhoud/Inschrijving/BetrokkenSubject/Subjecten/Subject/Sleutel", getInsz());
        request.setValue("//Vragen/Vraag/Inhoud/Inschrijving/Identificatie", magdaRegistrationInfo.getIdentification());

        var hoedanigheidscode = magdaRegistrationInfo.getHoedanigheidscode();
        if(hoedanigheidscode.isEmpty()) {
            request.removeNode("//Vragen/Vraag/Inhoud/Inschrijving/Hoedanigheid");
        } else {
            request.setValue("//Vragen/Vraag/Inhoud/Inschrijving/Hoedanigheid", hoedanigheidscode.get());
        }
    }
}