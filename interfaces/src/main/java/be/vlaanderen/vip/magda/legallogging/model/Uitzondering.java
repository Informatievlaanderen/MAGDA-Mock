package be.vlaanderen.vip.magda.legallogging.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class Uitzondering {

    private String identificatie;
    private String oorsprong;
    private String diagnose;
    private TypeUitzondering uitzonderingType;
    private List<Annotatie> annotaties;

    public String toString() {
        return String.format("%s %s-%s '%s'", uitzonderingType == null ? "null" : uitzonderingType.toString(), oorsprong, identificatie, diagnose);
    }
}
