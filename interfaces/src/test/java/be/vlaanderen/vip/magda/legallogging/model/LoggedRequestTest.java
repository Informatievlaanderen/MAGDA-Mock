package be.vlaanderen.vip.magda.legallogging.model;

import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.diensten.subject.KBONumber;
import be.vlaanderen.vip.magda.client.diensten.subject.SubjectIdentificationNumber;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoggedRequestTest {

    @Test
    void getSubjectsInLogFormat_formatsSubjectList() {
        var loggedRequest = constructLoggedRequestWithSubjects(List.of(INSZNumber.of("123"), KBONumber.of("456")));

        assertEquals("123 (INSZ), 456 (KBO)", loggedRequest.getSubjectsInLogFormat());
    }

    @Test
    void getSubjectsInLogFormat_noneIfEmpty() {
        var loggedRequest = constructLoggedRequestWithSubjects(List.of());

        assertEquals("(none)", loggedRequest.getSubjectsInLogFormat());
    }

    private LoggedRequest constructLoggedRequestWithSubjects(List<SubjectIdentificationNumber> subjects) {
        return new LoggedRequest(subjects, null, null, null, null, null);
    }
}