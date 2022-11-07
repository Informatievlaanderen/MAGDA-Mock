package be.vlaanderen.vip.magda.client.diensten;

public enum TypeInschrijving {
    PERSOON("PERS"),
    ONDERNEMING("OND");

    private final String typeString;

    TypeInschrijving(String typeString) {
        this.typeString = typeString;
    }

    public String getTypeString() {
        return typeString;
    }
}
