package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.Aanvraag;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;


@Getter
@ToString
public class RegistreerInschrijvingAanvraag extends Aanvraag {

    private final LocalDate start;
    private final LocalDate einde;

    public RegistreerInschrijvingAanvraag(String insz, LocalDate start, LocalDate einde) {
        super(insz);
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