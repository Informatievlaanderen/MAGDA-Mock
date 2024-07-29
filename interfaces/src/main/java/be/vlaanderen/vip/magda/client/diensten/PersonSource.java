package be.vlaanderen.vip.magda.client.diensten;

import lombok.Getter;

@Getter
public enum PersonSource {

    RR("RR"),
    KSZ("KSZ");

    private final String value;

    PersonSource(String value) {
        this.value = value;
    }
}
