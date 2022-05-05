package be.vlaanderen.vip.magda.legallogging.service;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Fout {
    private final String identificatie;
    private final String oorsprong;
    private final String type;
    private final String diagnose;
}
