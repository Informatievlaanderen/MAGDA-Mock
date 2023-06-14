package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

// TODO make builders for the subclasses to get rid of the long param lists and the logic in the constructors
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

    protected Aanvraag(@NotNull String insz, @NotNull String overWie) {
        this.insz = insz;
        this.overWie = overWie;
    }

    public abstract MagdaServiceIdentificatie magdaService();

    public void fillIn(MagdaDocument request, MagdaRegistrationInfo magdaRegistrationInfo) {
        request.setValue("//Referte", getRequestId().toString());
        request.setValue("//INSZ", getOverWie());

        final var now = Instant.now();
        var ldt = LocalDateTime.ofInstant(now, ZoneId.of("Europe/Brussels"));

        var dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        var today = ldt.format(dateFormatter);
        request.setValue("//Context/Bericht/Tijdstip/Datum", today);

        // Hardcoded 000 milliseconden wordt door alle Magda services aanvaard
        // Afwezigheid van milliseconden of milliseconden <> 000 wordt door sommige services geweigerd
        var timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        var time = ldt.format(timeFormat) + ".000";
        request.setValue("//Context/Bericht/Tijdstip/Tijd", time);

        request.setValue("//Context/Bericht/Afzender/Identificatie", magdaRegistrationInfo.getIdentification());

        var hoedanigheidscode = magdaRegistrationInfo.getHoedanigheidscode();
        if(hoedanigheidscode.isEmpty()) {
            request.removeNode("//Context/Bericht/Afzender/Hoedanigheid");
        } else {
            request.setValue("//Context/Bericht/Afzender/Hoedanigheid", hoedanigheidscode.get());
        }
    }
}
