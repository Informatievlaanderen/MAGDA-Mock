package be.vlaanderen.vip.magda.legallogging.model;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class Uitzondering implements Serializable {
    private String identificatie;
    private String oorsprong;
    private String diagnose;
    private TypeUitzondering uitzonderingType;
    private final List<Annotatie> annotaties;

    public Uitzondering(String identificatie, String oorsprong, String diagnose, TypeUitzondering uitzonderingType, List<Annotatie> annotaties) {
        this.identificatie = identificatie;
        this.oorsprong = oorsprong;
        this.diagnose = diagnose;
        this.uitzonderingType = uitzonderingType;
        this.annotaties = annotaties;
    }

    public Uitzondering(String identificatie, String oorsprong, String diagnose, TypeUitzondering uitzonderingType) {
        this.identificatie = identificatie;
        this.oorsprong = oorsprong;
        this.diagnose = diagnose;
        this.uitzonderingType = uitzonderingType;
        this.annotaties = new ArrayList<>();
    }

    public String toString() {
        return String.format("%s %s-%s '%s'", uitzonderingType == null ? "null" : uitzonderingType.toString(), oorsprong, identificatie, diagnose);
    }

    public static Uitzondering geenAntwoord() {
        return Uitzondering.builder()
                .identificatie("GEEN_ANTWOORD")
                .oorsprong("MAGDA")
                .diagnose("Geen antwoord ontvangen van MAGDA")
                .uitzonderingType(TypeUitzondering.FOUT)
                .build();
    }
}
