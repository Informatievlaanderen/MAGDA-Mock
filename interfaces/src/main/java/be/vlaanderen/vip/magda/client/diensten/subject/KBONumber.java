package be.vlaanderen.vip.magda.client.diensten.subject;

import be.vlaanderen.vip.magda.client.diensten.SubjectType;

public class KBONumber implements SubjectIdentificationNumber { // XXX test

    public static KBONumber of(String value) {
        return new KBONumber(value);
    }

    private final String value;

    private KBONumber(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public SubjectType getSubjectType() {
        return SubjectType.ENTERPRISE;
    }
}