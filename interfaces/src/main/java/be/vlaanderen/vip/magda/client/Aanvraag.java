package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheid;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
public abstract class Aanvraag {
    private final UUID correlationId = CorrelationId.get();
    private final UUID requestId = UUID.randomUUID();
    @NotNull
    private final String insz;
    @NotNull
    private final String overWie;
    @Setter
    @NotNull
    private String registratie = "default";

    protected Aanvraag(String insz) {
        this(insz, insz);
    }

    protected Aanvraag(String insz, String overWie) {
        this.insz = insz;
        this.overWie = overWie;
    }

    public abstract MagdaServiceIdentificatie magdaService();

    public void fillIn(MagdaDocument request, MagdaHoedanigheid magdaHoedanigheid) {
        request.setValue("//Referte", getRequestId().toString());
        request.setValue("//INSZ", getOverWie());

        final Instant now = Instant.now();
        LocalDateTime ldt = LocalDateTime.ofInstant(now, ZoneId.of("Europe/Brussels"));

        DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        String today = ldt.format(dateFormatter);
        request.setValue("//Context/Bericht/Tijdstip/Datum", today);

        // Hardcoded 000 milliseconden wordt door alle Magda services aanvaard
        // Afwezigheid van milliseconden of milliseconden <> 000 wordt door sommige services geweigerd
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time = ldt.format(timeFormat) + ".000";
        request.setValue("//Context/Bericht/Tijdstip/Tijd", time);

        request.setValue("//Context/Bericht/Afzender/Identificatie", magdaHoedanigheid.getUri());
        request.setValue("//Context/Bericht/Afzender/Hoedanigheid", magdaHoedanigheid.getHoedanigheid());
    }
}
