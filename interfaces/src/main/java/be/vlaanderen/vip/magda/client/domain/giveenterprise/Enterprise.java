package be.vlaanderen.vip.magda.client.domain.giveenterprise;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaResponse;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import jakarta.annotation.Nullable;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public interface Enterprise {

    static Enterprise ofMagdaDocument(MagdaDocument magdaDocument) throws MagdaClientException {
        return new MagdaResponseEnterpriseAdapterJaxbImpl().adapt(new MagdaResponseWrapper(MagdaResponse.builder()
                .document(magdaDocument)
                .build()));
    }

    String enterpriseNumber();

    @Nullable
    List<BranchOffice> branchOffices();

    @Nullable
    List<Address> addresses();

    @Nullable
    CodeAndDescription statusKBO();

    @Nullable
    DateContainer startDate();

    @Nullable
    CompanyNames companyNames();

    @Nullable
    CodeAndDescription enterpriseType();

    @Nullable
    List<CodeAndDescription> legalForms();

    interface LegalSituation {

        CodeAndDescription codeAndDescription();

        String instrumentingGovernment();

        OffsetDateTime startDate();

        OffsetDateTime endDate();
    }

    interface BranchOffice {

        List<Address> addresses();

        OffsetDateTime startDate();

        OffsetDateTime endDate();
    }

    interface HeadOffice {

        CodeAndDescription relation();

        String enterpriseNumber();

        CodeAndDescription discontinuation();

        OffsetDateTime startDate();

        OffsetDateTime endDate();
    }

    interface CompanyNames {

        @Nullable
        List<CompanyName> registeredNames();
    }

    interface CompanyName {

        String name();

        String languageCode();

        OffsetDateTime startDate();

        OffsetDateTime endDate();
    }

    interface Address extends BaseAddress {
        Type type();

        @Nullable
        List<Description> descriptions();

        @Nullable
        List<Cancellation> cancellations();

        OffsetDateTime startDate();

        OffsetDateTime endDate();
    }

    interface Type {

        ValueAndDescription code();

        @Nullable
        TypeDescription description();
    }

    interface TypeDescription {

        String value();

        String origin();

        String languageCode();
    }

    interface Description {

        @Nullable
        BaseAddress address();

        @Nullable
        ContactInfo contactInfo();

        @Nullable
        String languageCode();

        @Nullable
        String supplement();
    }

    interface BaseAddress {

        @Nullable
        Street street();

        @Nullable
        String houseNumber();

        @Nullable
        String busNumber();

        @Nullable
        Municipality municipality();

        @Nullable
        String state();

        @Nullable
        Country country();
    }

    interface ContactInfo {

        @Nullable
        String phoneNumber();

        @Nullable
        String faxNumber();

        @Nullable
        String mobileNumber();

        @Nullable
        String emailAddress();

        @Nullable
        String website();
    }

    interface Cancellation {

        CodeAndDescription reason();

        OffsetDateTime startDate();

        OffsetDateTime endDate();
    }

    interface Discontinuation {

        CodeAndDescription discontinuation();

        OffsetDateTime date();
    }

    interface BankAccountNumber {

        String accountNumber();

        String target();

        String ibanNumber();

        String bicNumber();

        String nonSEPANumber();

        OffsetDateTime startDate();

        OffsetDateTime endDate();
    }

    interface Role {

        String roleHolderOf();

        CodeAndDescription designationPersonEnterprise();

        Exemption exemption();

        CodeAndDescription endOfRole();

        CodeAndDescription natureOfRole();

        PersonInfo personInfo();

        EnterpriseInfo enterpriseInfo();

        OffsetDateTime startDate();

        OffsetDateTime endDate();
    }

    interface Exemption {

        CodeAndDescription exemption();

        ValueAndDescription designationExemption();

        ValueAndDescription noKMO();
    }

    interface PersonInfo {

        String insz();

        String lastName();

        String firstName();

        Street street();
    }

    interface EnterpriseInfo {

        String enterpriseNumber();

        CompanyName registeredName();
    }

    interface Activity {

        Nace nace();

        CodeAndDescription type();

        String group();

        OffsetDateTime startDate();

        OffsetDateTime endDate();
    }

    interface Nace {

        String version();

        CodeAndDescription codeNace();
    }

    interface RelatedEnterprise {

        EnterpriseRelation relation();

        String enterpriseNumber();

        CodeAndDescription endOfRelationship();

        OffsetDateTime startDate();

        OffsetDateTime endDate();
    }

    interface EnterpriseRelation {

        CodeAndDescription relation();

        Boolean isDaughter();
    }

    interface Capacity {

        CodeAndDescription capacity();

        String instrumentingAdministration();

        OffsetDateTime registrationDate();

        Integer duration();

        CodeAndDescription phase();

        CodeAndDescription termination();

        OffsetDateTime startDate();

        OffsetDateTime endDate();
    }

    interface ExternalIdentification {

        String edrl();

        String name();
    }

    interface Annex {

        Address address();

        CodeAndDescription discontinuation();

        OffsetDateTime startDate();

        OffsetDateTime endDate();
    }

    interface ExOfficioCancellation {

        CodeAndDescription reason();

        OffsetDateTime startDate();

        OffsetDateTime endDate();
    }

    interface LegalForm {

        String abbreviation();

        OffsetDateTime startDate();

        OffsetDateTime endDate();

        CodeAndDescription legalForm();
    }

    interface Street {

        String code();

        String name();
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

    interface CodeAndDescription {

        String codeValue();

        String codeDescription();

        String descriptionValue();

        String descriptionOrigin();

        String descriptionLanguageCode();
    }

    interface ValueAndDescription {

        String value();

        String description();
    }

    interface DateContainer {

        LocalDate value();
    }
}
