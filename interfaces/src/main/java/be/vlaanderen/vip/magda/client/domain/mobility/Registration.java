package be.vlaanderen.vip.magda.client.domain.mobility;

import java.util.List;

public interface Registration {
    Titular titular();

    interface Titular {
        Person person();

        Organisation organisation();
    }

    interface Person {
        NationalNr nationalNr();

        List<String> lastnames();

        List<String> firstnames();

        Integer birthYear();

        interface NationalNr {
            String identificator();
        }
    }

    interface Organisation {
        CompanyNr companyNr();

        LanguageString organisationName();

        Boolean isLeaseCompany();

        LegalFormCode legalFormCode();

        interface CompanyNr {
            String identificator();
        }

        interface LegalFormCode {
            LanguageString preflabel();
        }
    }

    interface LanguageString {
        String value();

        String lang();
    }
}