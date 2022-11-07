package be.vlaanderen.vip.mock.magda.inventory;

public enum TestcaseType {
    PERSONA("PERSONA"),
    DOMAIN("DOMAIN"),
    FUNCTIONAL_ERROR("FUNCTIONAL_ERROR"),
    TECHNICAL_ERROR("TECHNICAL_ERROR");

    private final String typeString;

    TestcaseType(String typeString) {
        this.typeString = typeString;
    }

    public String getTypeString() {
        return typeString;
    }
}
