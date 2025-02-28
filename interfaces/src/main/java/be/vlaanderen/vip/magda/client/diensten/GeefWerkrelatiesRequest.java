package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.Registration;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * A request to a "GeefWerkrelaties" MAGDA service allows you to consult the work relations for an INSZ.
 * Adds the following fields to the {@link PersonMagdaRequest}:
 * <ul>
 * <li>source: optional, search in specific source.</li>
 * <li>startDate: required, the start date of the period</li>
 * <li>endDate: optional, the end date of the period</li>
 * <li>startedOrEndedType: optional, indication to include all periods overlapping with the requested period or only the periods that started or ended within the requested period in the response.</li>
 * <li>deletionIndicationType: required, indication to include all contracts or only deleted contracts or only non-deleted contracts in the response.</li>
 * <li>interimIndicationType: optional, indication to include interim &amp; non-interim, interim only or non-interim only in the response.</li>
 * </ul>
 *
 * @see <a href="file:resources/templates/GeefWerkrelaties/02.00.0000/template.xml">XML template for this request type</a>
 * @see <a href="https://vlaamseoverheid.atlassian.net/wiki/spaces/MG/pages/1209565474/Werk.GeefWerkrelaties-02.00">More information on this request type</a>
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class GeefWerkrelatiesRequest  extends PersonMagdaRequest{

    public static class Builder extends PersonMagdaRequest.Builder<Builder> {
        @Getter(AccessLevel.PROTECTED)
        private SourceType source;
        @Getter(AccessLevel.PROTECTED)
        private LocalDate startDate;
        @Getter(AccessLevel.PROTECTED)
        private LocalDate endDate;
        @Getter(AccessLevel.PROTECTED)
        private StartedOrEndedType startedOrEndedType;
        @Getter(AccessLevel.PROTECTED)
        private DeletionIndicationType deletionIndicationType;
        @Getter(AccessLevel.PROTECTED)
        private InterimIndicationType interimIndicationType;

        public Builder source(SourceType source) {
            this.source = source;
            return this;
        }

        public Builder startDate(LocalDate date) {
            this.startDate = date;
            return this;
        }

        public Builder endDate(LocalDate date) {
            this.endDate = date;
            return this;
        }

        public Builder startedOrEndedType(StartedOrEndedType type) {
            this.startedOrEndedType = type;
            return this;
        }

        public Builder deletionIndicationType(DeletionIndicationType type) {
            this.deletionIndicationType = type;
            return this;
        }

        public Builder interimIndicationType(InterimIndicationType type) {
            this.interimIndicationType = type;
            return this;
        }

        public GeefWerkrelatiesRequest build() {
            if(getInsz() == null) { throw new IllegalStateException("INSZ number must be given"); }
            if(getStartDate() == null) { throw new IllegalStateException("Start date must be given"); }
            if(getDeletionIndicationType() == null) { throw new IllegalStateException("Deletion indication must be given"); }
            return new GeefWerkrelatiesRequest(
                    getInsz(),
                    getRegistration(),
                    getSource(),
                    getStartDate(),
                    getEndDate(),
                    getStartedOrEndedType(),
                    getDeletionIndicationType(),
                    getInterimIndicationType()
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }
    @Nullable
    private final SourceType source;
    @NotNull
    private final LocalDate startDate;
    @Nullable
    private final LocalDate endDate;
    @Nullable
    private final StartedOrEndedType startedOrEndedType;
    @NotNull
    private final DeletionIndicationType deletionIndicationType;
    @Nullable
    private final InterimIndicationType interimIndicationType;

    public GeefWerkrelatiesRequest(
            @NotNull INSZNumber insz,
            @NotNull Registration registration,
            @Nullable SourceType source,
            @NotNull LocalDate startDate,
            @Nullable LocalDate endDate,
            @Nullable StartedOrEndedType startedOrEndedType,
            @NotNull DeletionIndicationType deletionIndicationType,
            @Nullable InterimIndicationType interimIndicationType) {
        super(insz, registration);
        this.source = source;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startedOrEndedType = startedOrEndedType;
        this.deletionIndicationType = deletionIndicationType;
        this.interimIndicationType = interimIndicationType;
    }

    @Override
    public MagdaServiceIdentification magdaServiceIdentification() {
        return new MagdaServiceIdentification("GeefWerkrelaties", "02.00.0000");
    }

    @Override
    protected void fillIn(MagdaDocument request, UUID requestId, MagdaRegistrationInfo magdaRegistrationInfo) {
        fillInCommonFields(request, requestId, magdaRegistrationInfo);
        var dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        if (getSource() != null) {
            request.setValue("//Criteria/Bron", getSource().getTypeString());
        } else {
            request.removeNode("//Criteria/Bron");
        }
        request.setValue("//Criteria/Periode/Begin", getStartDate().format(dateFormatter));
        if (getEndDate() != null) {
            request.setValue("//Criteria/Periode/Einde", getEndDate().format(dateFormatter));
        } else {
            request.removeNode("//Criteria/Periode/Einde");
        }
        if (getStartedOrEndedType() != null) {
            request.setValue("//Criteria/EnkelGestartOfBeeindigd", getStartedOrEndedType().getTypeString());
        } else {
            request.removeNode("//Criteria/EnkelGestartOfBeeindigd");
        }
        request.setValue("//Criteria/SchrappingsIndicatie", getDeletionIndicationType().getTypeString());
        if (getInterimIndicationType() != null) {
            request.setValue("//Criteria/InterimIndicatie", getInterimIndicationType().getTypeString());
        } else {
            request.removeNode("//Criteria/InterimIndicatie");
        }
    }

    public enum StartedOrEndedType {
        /**
         * Include all periods that overlaps with the requested period.
         */
        ALL("0"),
        /**
         * Include only the periods that started or ended within the requested period.
         */
        STARTED_OR_ENDED_ONLY("1");

        private final String typeString;

        StartedOrEndedType(String typeString) {
            this.typeString = typeString;
        }

        public String getTypeString() {
            return typeString;
        }
    }

    public enum DeletionIndicationType {
        /**
         * Include all contracts.
         */
        ALL("0"),
        /**
         * Include non-deleted contracts only.
         */
        NON_DELETED("1"),
        /**
         * Include deleted contracts only.
         */
        DELETED("2");

        private final String typeString;

        DeletionIndicationType(String typeString) {
            this.typeString = typeString;
        }

        public String getTypeString() {
            return typeString;
        }
    }

    public enum InterimIndicationType {
        /**
         * Include interim &amp; non-interim.
         */
        ALL("0"),
        /**
         * Include interim only.
         */
        INTERIM_ONLY("1"),
        /**
         * Include non-interim only.
         */
        NON_INTERIM_ONLY("2");

        private final String typeString;

        InterimIndicationType(String typeString) {
            this.typeString = typeString;
        }

        public String getTypeString() {
            return typeString;
        }
    }

    public enum SourceType {
        RSZ("RSZ"),
        DIBISS("DIBISS");

        private final String typeString;

        SourceType(String typeString) {
            this.typeString = typeString;
        }

        public String getTypeString() {
            return typeString;
        }
    }
}
