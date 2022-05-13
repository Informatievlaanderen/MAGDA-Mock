package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.Aanvraag;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentificatie;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;


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
    public MagdaServiceIdentificatie magdaService() {
        return new MagdaServiceIdentificatie("RegistreerInschrijving", "02.00.0000");
    }

}