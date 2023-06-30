package be.vlaanderen.vip.magda.client.diensten;

/**
 * The type of subject to register into MAGDA (a person or an enterprise)
 */
public enum SubjectType {
    PERSON("PERS"),
    ENTERPRISE("OND");

    private final String typeString;

    SubjectType(String typeString) {
        this.typeString = typeString;
    }

    public String getTypeString() {
        return typeString;
    }
}
