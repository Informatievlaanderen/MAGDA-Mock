package be.vlaanderen.vip.magda.client.domain.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@EqualsAndHashCode
public class Kwartaal implements Serializable {
    @Getter
    @Setter
    private int jaar;

    @Getter
    private int kwartaalcijfer;

    public Kwartaal(int jaar, int kwartaalcijfer) {
        setJaar(jaar);
        setKwartaalcijfer(kwartaalcijfer);
    }

    public Kwartaal setKwartaalcijfer(int kwartaalcijfer) {
        if (kwartaalcijfer >= 1 && kwartaalcijfer <= 4) {
            this.kwartaalcijfer = kwartaalcijfer;
        }
        return this;
    }

    public String toString() {
        return String.format("%d-0%d", jaar, kwartaalcijfer);
    }
}
