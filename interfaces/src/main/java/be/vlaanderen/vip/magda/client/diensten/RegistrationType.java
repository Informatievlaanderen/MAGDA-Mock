package be.vlaanderen.vip.magda.client.diensten;

/**
 * The type of registration into MAGDA (a person or an enterprise)
 */
public enum RegistrationType {
    PERSON("PERS"),
    ENTERPRISE("OND");

    private final String typeString;

    RegistrationType(String typeString) {
        this.typeString = typeString;
    }

    public String getTypeString() {
        return typeString;
    }
}
