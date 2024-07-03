package be.vlaanderen.vip.magda.client.diensten;

import lombok.Getter;

@Getter
public enum EducationEnrollmentSource {
    HO("HO"),
    INT("INT"),
    LP("LP"),
    VWO("VWO");

    private final String value;

    EducationEnrollmentSource(String value) {
        this.value = value;
    }
}
