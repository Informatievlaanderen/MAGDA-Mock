package be.vlaanderen.vip.magda.client.diensten.subject;

import be.vlaanderen.vip.magda.client.diensten.SubjectType;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class KBONumber implements SubjectIdentificationNumber {

    public static KBONumber of(String value) {
        if(value != null) {
            return new KBONumber(value);
        } else {
            throw new IllegalArgumentException("KBONumber value cannot be null");
        }
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

    @Override
    public String getXPathExpression() {
        return "//Ondernemingsnummer";
    }

    @Override
    public String getValueInLogFormat() {
        return "%s (KBO)".formatted(getValue());
    }
}