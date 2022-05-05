package be.vlaanderen.vip.magda.legallogging.service;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UitzonderingBoodschap {
    private final String transactieID;
    private final String referte;
    private final String dienst;
    private final String wanneer;
    private final Toepassing wie;
    private final String gebruiker;

    private final List<Fout> fouten;
}
