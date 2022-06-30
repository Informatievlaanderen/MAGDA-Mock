package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.Aanvraag;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentificatie;
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
    public void fillIn(MagdaDocument request) {
        super.fillIn(request);
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        request.setValue("//Vraag/Inhoud/Uitschrijving/Periode/Start", getStart().format(dateFormatter));
        request.setValue("//Vraag/Inhoud/Uitschrijving/Periode/Einde", getEinde().format(dateFormatter));
    }

}