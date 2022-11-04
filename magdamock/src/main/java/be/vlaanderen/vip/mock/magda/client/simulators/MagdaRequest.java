package be.vlaanderen.vip.mock.magda.client.simulators;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class MagdaRequest {
    private final String serviceNaam;
    private final String serviceVersie;
    private final String identificatie;
    private final String hoedanigheid;
    private final String gebruiker;
    private final String referte;
    private final String vraagReferte;

    private final List<String> expressions;
    private final List<String> keys = new ArrayList<>();

    public MagdaRequest(MagdaDocument aanvraag, List<String> expressions) {
        this.serviceNaam = aanvraag.getValue("//Verzoek/Context/Naam");
        this.serviceVersie = aanvraag.getValue("//Verzoek/Context/Versie");
        this.referte = aanvraag.getValue("//Afzender/Referte");
        this.identificatie = aanvraag.getValue("//Afzender/Identificatie");
        this.hoedanigheid = aanvraag.getValue("//Afzender/Hoedanigheid");
        this.gebruiker = aanvraag.getValue("//Afzender/Gebruiker");
        this.vraagReferte = aanvraag.getValue("//Vragen/Vraag/Referte");

        this.expressions = expressions;

        expressions.forEach(expr -> keys.add(aanvraag.getValue(expr)));
    }
}
