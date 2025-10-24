package be.vlaanderen.vip.magda.client.domain.givedisabilitydossier;

import jakarta.annotation.Nullable;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public interface DisabilityDossier {

    ConsultFilesByDateResponse consultFilesByDateResponse();

    interface ConsultFilesByDateResponse {

        @Nullable
        String ssin();

        Status status();

        Results results();
    }

    interface Status {

        String value();

        String code();

        String description();

        List<Information> informations();
    }

    interface Information {

        String fieldName();

        String fieldValue();
    }

    interface Results {

        DgphResult dgphResult();
    }

    interface DgphResult {

        Status status();

        File file();
    }

    interface File {
        List<HandicapRecognition> handicapRecognitions();

        List<SocialCard> socialCards();
    }

    interface HandicapRecognition {
        RecognitionStatus recognitionStatus();

        Integer legislation();

        HandicapRecognitionDetails handicapRecognitionDetails();

        ResultRecognitionChild resultRecognitionChild();

        ResultRecognitionAdult resultRecognitionAdult();

        String decisionStatus();
    }

    interface RecognitionStatus {
        OffsetDateTime dateOfDecision();

        OffsetDateTime startDateRecognition();

        OffsetDateTime endDateRecognition();
    }

    interface HandicapRecognitionDetails {
        Boolean lowerLimbs50P();

        Boolean fullBlindness();

        Boolean upperLimbsAmputated();

        Boolean upperLimbsParalyzed();
    }

    interface ResultRecognitionChild {
        Boolean inabilityFollowCourse();

        Boolean inabilityToWork();

        Integer disabilityCode();

        Integer independencyScore();

        Pillars pillars();

        Boolean childPathology();
    }

    interface Pillars {
        Integer pillar1();
        Integer pillar2();
        Integer pillar3();
        Integer pillarsTotal();
    }

    interface ResultRecognitionAdult {
        DiminuationIndependence diminuationIndependence();

        Boolean diminuationEarnings();

        Unsuitability unsuitability();
    }

    interface DiminuationIndependence {
        Integer mobility();

        Integer nourishment();

        Integer hygiene();

        Integer household();

        Integer supervision();

        Integer socialSkills();

        Integer totalPoints();
    }

    interface Unsuitability {
        BigDecimal mentalUnsuitability();

        BigDecimal physicalUnsuitability();
    }

    interface SocialCard {

        OffsetDateTime deliveryDate();

        OffsetDateTime endDate();

        String cardNumber();

        String cardCategory();
    }
}