package be.vlaanderen.vip.magda.client;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Getter
public abstract class Aanvraag {
    //private final UUID correlationId = CorrelationId.get();
    private final UUID correlationId = UUID.randomUUID();
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
}
