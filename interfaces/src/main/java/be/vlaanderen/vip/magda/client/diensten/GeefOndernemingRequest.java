package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaRequest;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.diensten.subject.KBONumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * A request to a "GeefOnderneming" MAGDA service, which provides company information.
 * Adds the following fields to the {@link CompanyMagdaRequest}:
 * <ul>
 * <li>includeBasicData: optional, specify whether to include the 'basic' company data or not: status, juridical forms, names, addresses,...</li>
 * <li>includeJuridicalSituations: optional, specify whether to include the juridical situations or not.</li>
 * <li>includeEstablishments: optional, specify whether to include the 'basic' establishments data or not.</li>
 * <li>includeEstablishmentsDetails: optional, specify whether to include the 'detailed' establishments data or not.</li>
 * <li>includeDescriptions: optional, specify whether to include the descriptions of specific codes or not.</li>
 * <li>startDate: optional, enables searching for data within a specific time range, starting from this date.</li>
 * <li>endDate: optional, enables searching for data within a specific time range, with an end date set to this date.</li>
 * </ul>
 *
 * @see <a href="file:resources/templates/GeefOnderneming/02.00.0000/template.xml">XML template for this request type</a>
 * @see <a href="https://vlaamseoverheid.atlassian.net/wiki/spaces/MG/pages/502137585/Onderneming.GeefOnderneming-02.00">More information on this request type</a>
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class GeefOndernemingRequest extends CompanyMagdaRequest {

    public static class Builder extends CompanyMagdaRequest.Builder<Builder> {

        @Getter(AccessLevel.PROTECTED)
        private Boolean includeBasicData;

        @Getter(AccessLevel.PROTECTED)
        private Boolean includeJuridicalSituations;

        @Getter(AccessLevel.PROTECTED)
        private Boolean includeEstablishments;

        @Getter(AccessLevel.PROTECTED)
        private Boolean includeEstablishmentsDetails;

        @Getter(AccessLevel.PROTECTED)
        private Boolean includeDescriptions;

        @Getter(AccessLevel.PROTECTED)
        private LocalDate startDate;

        @Getter(AccessLevel.PROTECTED)
        private LocalDate endDate;

        public Builder includeBasicData(Boolean includeBasicData) {
            this.includeBasicData = includeBasicData;
            return this;
        }

        public Builder includeJuridicalSituations(Boolean includeJuridicalSituations) {
            this.includeJuridicalSituations = includeJuridicalSituations;
            return this;
        }

        public Builder includeEstablishments(Boolean includeEstablishments) {
            this.includeEstablishments = includeEstablishments;
            return this;
        }

        public Builder includeEstablishmentsDetails(Boolean includeEstablishmentsDetails) {
            this.includeEstablishmentsDetails = includeEstablishmentsDetails;
            return this;
        }

        public Builder includeDescriptions(Boolean includeDescriptions) {
            this.includeDescriptions = includeDescriptions;
            return this;
        }

        public Builder startDate(LocalDate startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder endDate(LocalDate endDate) {
            this.endDate = endDate;
            return this;
        }

        public GeefOndernemingRequest build() {
            if(getKboNumber() == null) { throw new IllegalStateException("KBO number must be given"); }

            return new GeefOndernemingRequest(
                    getKboNumber(),
                    getRegistration(),
                    getIncludeBasicData(),
                    getIncludeJuridicalSituations(),
                    getIncludeEstablishments(),
                    getIncludeEstablishmentsDetails(),
                    getIncludeDescriptions(),
                    getStartDate(),
                    getEndDate()
            );
        }
    }

    @Nullable
    private final Boolean includeBasicData;
    @Nullable
    private final Boolean includeJuridicalSituations;
    @Nullable
    private final Boolean includeEstablishments;
    @Nullable
    private final Boolean includeEstablishmentsDetails;
    @Nullable
    private final Boolean includeDescriptions;
    @Nullable
    private final LocalDate startDate;
    @Nullable
    private final LocalDate endDate;

    public static Builder builder() {
        return new Builder();
    }

    private GeefOndernemingRequest(
            @NotNull KBONumber kboNumber,
            @NotNull String registratie,
            @Nullable Boolean includeBasicData,
            @Nullable Boolean includeJuridicalSituations,
            @Nullable Boolean includeEstablishments,
            @Nullable Boolean includeEstablishmentsDetails,
            @Nullable Boolean includeDescriptions,
            @Nullable LocalDate startDate,
            @Nullable LocalDate endDate) {
        super(kboNumber, registratie);
        this.includeBasicData = includeBasicData;
        this.includeJuridicalSituations = includeJuridicalSituations;
        this.includeEstablishments = includeEstablishments;
        this.includeEstablishmentsDetails = includeEstablishmentsDetails;
        this.includeDescriptions = includeDescriptions;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public MagdaServiceIdentification magdaServiceIdentification() {
        return new MagdaServiceIdentification("GeefOnderneming", "02.00.0000");
    }

    @Override
    protected void fillIn(MagdaDocument request, MagdaRegistrationInfo magdaRegistrationInfo) {
        fillInCommonFields(request, magdaRegistrationInfo);
        var dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;

        if(getIncludeBasicData() != null){
            request.setValue("//Criteria/Basisgegevens", getIncludeBasicData() ? "1" : "0");
        } else {
            request.removeNode("//Criteria/Basisgegevens");
        }

        if(getIncludeJuridicalSituations() != null){
            request.setValue("//Criteria/Rechtstoestanden", getIncludeJuridicalSituations() ? "1" : "0");
        } else {
            request.removeNode("//Criteria/Rechtstoestanden");
        }

        if(getIncludeEstablishments() != null || getIncludeEstablishmentsDetails() != null)
        {
            if(getIncludeEstablishments() != null){
                request.setValue("//Criteria/Vestigingen/Aanduiding", getIncludeEstablishments() ? "1" : "0");
            } else {
                request.removeNode("//Criteria/Vestigingen/Aanduiding");
            }
            if(getIncludeEstablishmentsDetails() != null){
                request.setValue("//Criteria/Vestigingen/Details", getIncludeEstablishmentsDetails() ? "1" : "0");
            } else {
                request.removeNode("//Criteria/Vestigingen/Details");
            }
        } else {
            request.removeNode("//Criteria/Vestigingen");
        }

        if(getIncludeDescriptions() != null){
            request.setValue("//Criteria/Omschrijvingen/Aanduiding", getIncludeDescriptions() ? "1" : "0");
        } else {
            request.removeNode("//Criteria/Omschrijvingen");
        }

        if(getStartDate() != null || getEndDate() != null)
        {
            if(getStartDate() != null){
                request.setValue("//Criteria/Datums/Periode/Begin", getStartDate().format(dateFormatter));
            } else {
                request.removeNode("//Criteria/Datums/Periode/Begin");
            }
            if(getEndDate() != null){
                request.setValue("//Criteria/Datums/Periode/Einde", getEndDate().format(dateFormatter));
            } else {
                request.removeNode("//Criteria/Datums/Periode/Einde");
            }
        } else {
            request.removeNode("//Criteria/Datums");
        }

    }
}