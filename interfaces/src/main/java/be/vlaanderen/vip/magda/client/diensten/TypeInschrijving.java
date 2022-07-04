package be.vlaanderen.vip.magda.client.diensten;

public enum TypeInschrijving {
    PERSOON("PERS"),
    ONDERNEMING("OND");

    private String typeString;

    TypeInschrijving(String typeString) {
        this.typeString = typeString;
    }

    public String getTypeString() {
        return typeString;
    }
}
