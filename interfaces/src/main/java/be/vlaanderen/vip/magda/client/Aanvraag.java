package be.vlaanderen.vip.magda.client;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
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

    public Aanvraag(String insz) {
        this(insz, insz);
    }

    public Aanvraag(String insz, String overWie) {
        this.insz = insz;
        this.overWie = overWie;
    }

    public abstract MagdaServiceIdentificatie magdaService();

    public void fillIn(MagdaDocument request) {
        request.setValue("//Referte", getRequestId().toString());
        request.setValue("//INSZ", getOverWie());

        final Instant now = Instant.now();
        LocalDateTime ldt = LocalDateTime.ofInstant(now, ZoneId.of("Europe/Brussels"));

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String today = ldt.format(dateFormatter);
        request.setValue("//Context/Bericht/Tijdstip/Datum", today);

        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        String time = ldt.format(timeFormat) + ".000";
        request.setValue("//Context/Bericht/Tijdstip/Tijd", time);
    }
}
