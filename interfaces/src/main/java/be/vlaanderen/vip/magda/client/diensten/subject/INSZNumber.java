package be.vlaanderen.vip.magda.client.diensten.subject;

import be.vlaanderen.vip.magda.client.diensten.SubjectType;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class INSZNumber implements SubjectIdentificationNumber {

    public static INSZNumber of(String value) {
        if(value != null) {
            return new INSZNumber(value);
        } else {
            throw new IllegalArgumentException("INSZNumber value cannot be null");
        }
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

    @Override
    public String getXPathExpression() {
        return "//INSZ";
    }

    @Override
    public String getValueInLogFormat() {
        return "%s (INSZ)".formatted(getValue());
    }
}