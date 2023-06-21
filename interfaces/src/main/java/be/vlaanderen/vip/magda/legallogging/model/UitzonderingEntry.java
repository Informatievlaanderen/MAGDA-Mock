package be.vlaanderen.vip.magda.legallogging.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UitzonderingEntry {

    private String identificatie;
    private String oorsprong;
    private String diagnose;
    private UitzonderingType uitzonderingType;
    private List<AnnotatieField> annotaties;

    public String toString() {
        return String.format("%s %s-%s '%s'", uitzonderingType == null ? "null" : uitzonderingType.toString(), oorsprong, identificatie, diagnose);
    }
}
