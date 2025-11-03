package be.vlaanderen.vip.magda.client.domain.mobility;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Registration {
    Titular titular;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Titular {

        Person person;
        Organisation organisation;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Person {
            NationalNr nationalNr;

            List<String> lastnames;

            List<String> firstnames;

            Integer birthYear;

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class NationalNr {
                String identificator;
            }

        }

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Organisation {
            CompanyNr companyNr;

            LanguageString organisationName;

            Boolean isLeaseCompany;

            LegalFormCode legalFormCode;

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class CompanyNr {
                String identificator;
            }

            @Data
            @JsonIgnoreProperties(ignoreUnknown = true)
            public static class LegalFormCode {
                LanguageString preflabel;
            }
        }

    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LanguageString {
        String value;

        String lang;
    }
}