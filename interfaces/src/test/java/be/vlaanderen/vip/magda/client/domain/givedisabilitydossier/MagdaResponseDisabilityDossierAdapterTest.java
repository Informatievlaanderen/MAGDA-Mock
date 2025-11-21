package be.vlaanderen.vip.magda.client.domain.givedisabilitydossier;

import be.vlaanderen.vip.magda.TestHelpers;
import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaResponse;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MagdaResponseDisabilityDossierAdapterTest {

    private final MagdaResponseDisabilityDossierAdapter adapter = new MagdaResponseDisabilityDossierAdapterJaxbImpl();

    @Test
    void producesEnterpriseFunctionsFromResponseXml() throws IOException, MagdaClientException {
        var magdaResponse = MagdaResponse.builder()
                .document(MagdaDocument.fromString(TestHelpers.getResourceAsString(getClass(), "/magdamock/disabilitydossier/sample.xml")))
                .build();

        var dossier = adapter.adapt(new MagdaResponseWrapper(magdaResponse));
        assertNotNull(dossier);
        var filesByDateResponse = dossier.consultFilesByDateResponse();
        assertNotNull(filesByDateResponse);
        var status = filesByDateResponse.status();
        assertNotNull(status);
        assertEquals("MSG00000", status.code());
        assertEquals("DATA_FOUND", status.value());
        assertEquals("Treatment successful", status.description());

        var results = filesByDateResponse.results();
        assertNotNull(results);
        var dgphResult = results.dgphResult();
        assertNotNull(dgphResult);

        var dgphResultStatus = dgphResult.status();
        assertNotNull(status);
        assertEquals("MSG00000", dgphResultStatus.code());
        assertEquals("DATA_FOUND", dgphResultStatus.value());
        assertEquals("Treatment successful", dgphResultStatus.description());

        var dgphResultFile = dgphResult.file();
        assertNotNull(dgphResultFile);

        var handicapRecognitions = dgphResultFile.handicapRecognitions();
        assertNotNull(handicapRecognitions);
        assertEquals(1, handicapRecognitions.size());
        var handicapRecognition = handicapRecognitions.get(0);
        assertNotNull(handicapRecognition);
        assertEquals("RECOGNITION_DETERMINED", handicapRecognition.decisionStatus());

        var recognitionStatus = handicapRecognition.recognitionStatus();
        assertNotNull(recognitionStatus);
        assertEquals(2022, recognitionStatus.dateOfDecision().getYear());
        assertEquals(8, recognitionStatus.dateOfDecision().getMonthValue());
        assertEquals(29, recognitionStatus.dateOfDecision().getDayOfMonth());
        assertEquals(2022, recognitionStatus.startDateRecognition().getYear());
        assertEquals(7, recognitionStatus.startDateRecognition().getMonthValue());
        assertEquals(1, recognitionStatus.startDateRecognition().getDayOfMonth());

        var legislation = handicapRecognition.legislation();
        assertNotNull(legislation);
        assertEquals(3, legislation);

        var handicapRecognitionDetails = handicapRecognition.handicapRecognitionDetails();
        assertNotNull(handicapRecognitionDetails);
        assertFalse(handicapRecognitionDetails.fullBlindness());
        assertFalse(handicapRecognitionDetails.lowerLimbs50P());
        assertFalse(handicapRecognitionDetails.upperLimbsAmputated());
        assertFalse(handicapRecognitionDetails.upperLimbsParalyzed());

        var resultRecognitionAdult = handicapRecognition.resultRecognitionAdult();
        assertNotNull(resultRecognitionAdult);
        assertTrue(resultRecognitionAdult.diminuationEarnings());
        var diminuationIndependence = resultRecognitionAdult.diminuationIndependence();
        assertNotNull(diminuationIndependence);
        assertEquals(2, diminuationIndependence.mobility());
        assertEquals(1, diminuationIndependence.nourishment());
        assertEquals(1, diminuationIndependence.hygiene());
        assertEquals(2, diminuationIndependence.household());
        assertEquals(1, diminuationIndependence.supervision());
        assertEquals(1, diminuationIndependence.socialSkills());
        assertEquals(8, diminuationIndependence.totalPoints());

        var socialCards = dgphResultFile.socialCards();
        assertNull(socialCards);
    }

    @Test
    void throwsErrorWhenNoResponse() throws IOException {
        var magdaResponse = MagdaResponse.builder()
                .document(MagdaDocument.fromString(TestHelpers.getResourceAsString(getClass(), "/magdamock/disabilitydossier/error.xml")))
                .build();

        try {
            adapter.adapt(new MagdaResponseWrapper(magdaResponse));
        } catch (MagdaClientException e) {
            assertEquals("Could not parse magda response", e.getMessage());
        }
    }
}
