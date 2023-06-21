package be.vlaanderen.vip.magda.client.diensten;

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
