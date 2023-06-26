package be.vlaanderen.vip.magda.client.diensten.subject;

import be.vlaanderen.vip.magda.client.diensten.SubjectType;

import java.io.Serializable;

public interface SubjectIdentificationNumber extends Serializable {

    String getValue();

    SubjectType getSubjectType();

    String getValueInLogFormat();
}