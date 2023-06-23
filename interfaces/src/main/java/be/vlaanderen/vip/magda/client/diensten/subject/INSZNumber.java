package be.vlaanderen.vip.magda.client.diensten.subject;

import be.vlaanderen.vip.magda.client.diensten.SubjectType;

public class INSZNumber implements SubjectIdentificationNumber { // XXX test

    public static INSZNumber of(String value) {
        return new INSZNumber(value);
    }

    private final String value;

    private INSZNumber(String value) {
        this.value = value;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public SubjectType getSubjectType() {
        return SubjectType.PERSON;
    }
}