package be.vlaanderen.vip.magda.client.domain.model.disability;

import be.vlaanderen.vip.magda.client.domain.givedisabilitydossier.DisabilityDossier;
import be.vlaanderen.vip.magda.client.domain.model.shared.OffsetDateXmlAdapter;
import jakarta.annotation.Nullable;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "Inhoud")
@Accessors(fluent = true)
@Getter
public class DisabilityDossierJaxb implements DisabilityDossier, Serializable {

    @XmlElement(name = "ConsultFilesByDateResponse")
    ConsultFilesByDateResponse consultFilesByDateResponse;

    @Getter
    private static class ConsultFilesByDateResponse implements DisabilityDossier.ConsultFilesByDateResponse, Serializable {

        @XmlElement(name = "ssin")
        @Nullable
        String ssin;

        @XmlElement(name = "status")
        Status status;

        @XmlElement(name = "results")
        Results results;
    }

    @Getter
    private static class Status implements DisabilityDossier.Status, Serializable {

        @XmlElement(name = "value")
        String value;

        @XmlElement(name = "code", nillable = true)
        String code;

        @XmlElement(name = "description", nillable = true)
        String description;

        @XmlElement(name = "information")
        ArrayList<Information> informations;

        @Override
        public List<DisabilityDossier.Information> informations() {
            if (informations == null) return null;
            return informations.stream()
                    .map(information -> (DisabilityDossier.Information) information)
                    .toList();
        }
    }

    @Getter
    private static class Information implements DisabilityDossier.Information, Serializable {

        @XmlElement(name = "fieldName", nillable = true)
        String fieldName;

        @XmlElement(name = "fieldValue", nillable = true)
        String fieldValue;
    }

    @Getter
    private static class Results implements DisabilityDossier.Results, Serializable {

        @XmlElement(name = "dgphResult")
        DgphResult dgphResult;
    }

    @Getter
    private static class DgphResult implements DisabilityDossier.DgphResult, Serializable {

        @XmlElement(name = "status")
        Status status;

        @XmlElement(name = "file")
        File file;
    }

    @Getter
    private static class File implements DisabilityDossier.File, Serializable {

        @XmlElementWrapper(name = "handicapRecognitions")
        @XmlElement(name = "handicapRecognition")
        ArrayList<HandicapRecognition> handicapRecognitions;

        @XmlElementWrapper(name = "socialCards")
        @XmlElement(name = "socialCard")
        ArrayList<SocialCard> socialCards;

        @Override
        public List<DisabilityDossier.HandicapRecognition> handicapRecognitions() {
            if (handicapRecognitions == null) return null;
            return handicapRecognitions.stream().map(
                    handicapRecognition -> (DisabilityDossier.HandicapRecognition) handicapRecognition
            ).toList();
        }

        @Override
        public List<DisabilityDossier.SocialCard> socialCards() {
            if (socialCards == null) return null;
            return socialCards.stream()
                    .map(socialCard -> (DisabilityDossier.SocialCard) socialCard)
                    .toList();
        }
    }

    @Getter
    private static class HandicapRecognition implements DisabilityDossier.HandicapRecognition, Serializable {
        @XmlElement(name = "recognitionStatus")
        RecognitionStatus recognitionStatus;

        @XmlElement(name = "legislation")
        Integer legislation;

        @XmlElement(name = "handicapRecognitionDetails")
        HandicapRecognitionDetails handicapRecognitionDetails;

        @XmlElement(name = "resultRecognitionChild")
        ResultRecognitionChild resultRecognitionChild;

        @XmlElement(name = "resultRecognitionAdult")
        ResultRecognitionAdult resultRecognitionAdult;

        @XmlElement(name = "decisionStatus")
        String decisionStatus;
    }

    @Getter
    private static class RecognitionStatus implements DisabilityDossier.RecognitionStatus, Serializable {
        @XmlElement(name = "dateOfDecision")
        @XmlJavaTypeAdapter(OffsetDateXmlAdapter.class)
        OffsetDateTime dateOfDecision;

        @XmlElement(name = "startDateRecognition")
        @XmlJavaTypeAdapter(OffsetDateXmlAdapter.class)
        OffsetDateTime startDateRecognition;

        @XmlElement(name = "endDateRecognition")
        @XmlJavaTypeAdapter(OffsetDateXmlAdapter.class)
        OffsetDateTime endDateRecognition;
    }

    @Getter
    private static class HandicapRecognitionDetails implements DisabilityDossier.HandicapRecognitionDetails, Serializable {
        @XmlElement(name = "lowerLimbs50p")
        Boolean lowerLimbs50P;

        @XmlElement(name = "fullBlindness")
        Boolean fullBlindness;

        @XmlElement(name = "upperLimbsAmputated")
        Boolean upperLimbsAmputated;

        @XmlElement(name = "upperLimbsParalyzed")
        Boolean upperLimbsParalyzed;
    }

    @Getter
    private static class ResultRecognitionChild implements DisabilityDossier.ResultRecognitionChild, Serializable {
        @XmlElement(name = "inabilityFollowCourse")
        Boolean inabilityFollowCourse;

        @XmlElement(name = "inabilityToWork")
        Boolean inabilityToWork;

        @XmlElement(name = "disabilityCode")
        Integer disabilityCode;

        @XmlElement(name = "independencyScore")
        Integer independencyScore;

        @XmlElement(name = "pillars")
        Pillars pillars;

        @XmlElement(name = "childPathology")
        Boolean childPathology;
    }

    @Getter
    private static class Pillars implements DisabilityDossier.Pillars, Serializable {
        @XmlElement(name = "pillar1")
        Integer pillar1;

        @XmlElement(name = "pillar2")
        Integer pillar2;

        @XmlElement(name = "pillar3")
        Integer pillar3;

        @XmlElement(name = "pillarsTotal")
        Integer pillarsTotal;
    }

    @Getter
    private static class ResultRecognitionAdult implements DisabilityDossier.ResultRecognitionAdult, Serializable {
        @XmlElement(name = "diminuationIndependence")
        DiminuationIndependence diminuationIndependence;

        @XmlElement(name = "diminuationEarnings")
        Boolean diminuationEarnings;

        @XmlElement(name = "unsuitability")
        Unsuitability unsuitability;
    }

    @Getter
    private static class DiminuationIndependence implements DisabilityDossier.DiminuationIndependence, Serializable {
        @XmlElement(name = "mobility")
        Integer mobility;

        @XmlElement(name = "nourishment")
        Integer nourishment;

        @XmlElement(name = "hygiene")
        Integer hygiene;

        @XmlElement(name = "household")
        Integer household;

        @XmlElement(name = "supervision")
        Integer supervision;

        @XmlElement(name = "socialSkills")
        Integer socialSkills;

        @XmlElement(name = "totalPoints")
        Integer totalPoints;
    }

    @Getter
    private static class Unsuitability implements DisabilityDossier.Unsuitability, Serializable {
        @XmlElement(name = "mentalUnsuitability")
        BigDecimal mentalUnsuitability;

        @XmlElement(name = "physicalUnsuitability")
        BigDecimal physicalUnsuitability;
    }

    @Getter
    private static class SocialCard implements DisabilityDossier.SocialCard, Serializable {

        @XmlElement(name = "deliveryDate", nillable = true)
        @XmlJavaTypeAdapter(OffsetDateXmlAdapter.class)
        OffsetDateTime deliveryDate;

        @XmlElement(name = "endDate", nillable = true)
        @XmlJavaTypeAdapter(OffsetDateXmlAdapter.class)
        OffsetDateTime endDate;

        @XmlElement(name = "cardNumber", nillable = true)
        String cardNumber;

        @XmlElement(name = "cardCategory", nillable = true)
        String cardCategory;
    }
}