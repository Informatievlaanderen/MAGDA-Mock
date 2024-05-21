package be.vlaanderen.vip.magda.client.domain.giveworkrelations;

import java.time.OffsetDateTime;
import java.util.List;

public interface WorkRelations {

    List<Contract> contracts();

    interface Contract {

        Relation relation();

        ValueAndDescription source();

        Declaration declaration();

        InterimEnterprise interimEnterprise();

        PlaceOfEmployment placeOfEmployment();
    }

    interface Relation {

        Employer employer();

        Employee employee();
    }

    interface Employer {

        String rszNumber();

        String enterpriseNumber();

        String subEntity();
    }

    interface Employee {

        String insz();

        ValueAndDescription identificationPersonValidated();

        String lastName();

        String firstName();
    }

    interface Declaration {

        String dimonaNumber();

        Period period();

        String jointCommittee();

        String natureOfEmployee();

        ValueAndDescription serviceCategory();

        Integer numberOfPlannedWorkDays();

        Boolean cancelled();

        ValueAndDescription lastDimonaAction();

        ControlCards controlCards();

        OffsetDateTime dateTimeOfCreation();

        OffsetDateTime dateTimeOfModification();
    }

    interface ControlCards {

        String cardNumberFirstMonth();

        String cardNumberNextMonth();
    }

    interface InterimEnterprise {

        String name();

        String rszNumber();

        String enterpriseNumber();

        String jointCommittee();
    }

    interface PlaceOfEmployment {

        String name();

        Address address();
    }

    interface Address {

        String street();

        String houseNumber();

        String busNumber();

        Municipality municipality();

        Country country();
    }

    interface Municipality {

        String nisCode();

        Integer postCode();

        String name();
    }

    interface Country {

        String nisCode();

        String name();

        String isoCode();
    }

    interface ValueAndDescription {

        String value();

        String description();
    }

    interface Period {

        OffsetDateTime startDate();

        OffsetDateTime endDate();
    }
}