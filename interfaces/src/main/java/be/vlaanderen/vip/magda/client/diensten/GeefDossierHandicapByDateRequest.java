package be.vlaanderen.vip.magda.client.diensten;

import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaServiceIdentification;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Set;

/**
 * A request to a "GeefDossierHandicap" MAGDA service, which provides information regarding disability for an INSZ.
 * Adds the following fields to the {@link PersonMagdaRequest}:
 * <ul>
 * <li>referenceDate: the reference date</li>
 * <li>sources: include the sources to consult</li>
 * <li>parts: include file parts</li>
 * <li></li>
 * </ul>
 *
 * @see <a href="file:resources/templates/GeefDossierHandicap/03.00.0000/template.xml">XML template for this request type</a>
 * @see <a href="https://vlaamseoverheid.atlassian.net/wiki/spaces/MG/pages/1624016756/SocZek.GeefDossierHandicap-03.00">More information on this request type</a>
 */
@Getter
@ToString
public class GeefDossierHandicapByDateRequest extends PersonMagdaRequest {

    public static class Builder extends PersonMagdaRequest.Builder<Builder> {

        @Getter(AccessLevel.PROTECTED)
        private LocalDate referenceDate;
        @Getter(AccessLevel.PROTECTED)
        private Set<HandicapAuthenticSourceType> sources;
        @Getter(AccessLevel.PROTECTED)
        private Set<HandicapFilePartType> parts;


        public Builder referenceDate(LocalDate date) {
            this.referenceDate = date;
            return this;
        }

        public Builder sources(Set<HandicapAuthenticSourceType> sources) {
            this.sources = sources;
            return this;
        }

        public Builder parts(Set<HandicapFilePartType> parts) {
            this.parts = parts;
            return this;
        }

        public GeefDossierHandicapByDateRequest build() {
            if(getInsz() == null) { throw new IllegalStateException("INSZ number must be given"); }
            if(getReferenceDate() == null) { throw new IllegalStateException("Reference date must be given"); }

            return new GeefDossierHandicapByDateRequest(
                    getInsz(),
                    getRegistration(),
                    getReferenceDate(),
                    getSources(),
                    getParts()
            );
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @NotNull
    private final LocalDate referenceDate;
    @Nullable
    private final Set<HandicapAuthenticSourceType> sources;
    @Nullable
    private final Set<HandicapFilePartType> parts;

    public GeefDossierHandicapByDateRequest(
            @NotNull INSZNumber insz,
            @NotNull String registration,
            @NotNull LocalDate referenceDate,
            @Nullable Set<HandicapAuthenticSourceType> sources,
            @Nullable Set<HandicapFilePartType> parts) {
        super(insz, registration);
        this.referenceDate = referenceDate;
        this.sources = sources;
        this.parts = parts;
    }

    @Override
    public MagdaServiceIdentification magdaServiceIdentification() {
        return new MagdaServiceIdentification("GeefDossierHandicap", "03.00.0000");
    }

    @Override
    protected void fillIn(MagdaDocument request, MagdaRegistrationInfo magdaRegistrationInfo) {
        fillInCommonFields(request, magdaRegistrationInfo);

        var dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        request.setValue("//ConsultFilesByDateCriteria/ssin", getInsz().getValue());
        request.setValue("//ConsultFilesByDateCriteria/referenceDate", getReferenceDate().format(dateFormatter));
        Arrays.stream(HandicapAuthenticSourceType.values()).forEach(x -> {
            request.createTextNode("//ConsultFilesByDateCriteria/handicapAuthenticSources", x.getTypeString(), getSources() != null && getSources().contains(x) ? "true" : "false");
        });
        Arrays.stream(HandicapFilePartType.values()).forEach(x -> {
            request.createTextNode("//ConsultFilesByDateCriteria/parts", x.getTypeString(), getParts() != null && getParts().contains(x) ? "true" : "false");
        });

        request.removeNode("//ConsultFilesByPeriodCriteria");
    }

    public enum HandicapAuthenticSourceType {
        DGPH("DGPH"),
        VSB("VSB"),
        IRISCARE("IrisCare"),
        NICCIN("NicCin"),
        AVIQ("AVIQ"),
        DSL("DSL");

        private final String typeString;

        HandicapAuthenticSourceType(String typeString) {
            this.typeString = typeString;
        }

        public String getTypeString() {
            return typeString;
        }
    }

    public enum HandicapFilePartType {
        EVOLUTION_OF_REQUEST("evolutionOfRequest"),
        HANDICAP_RECOGNITIONS("handicapRecognitions"),
        RIGHTS("rights"),
        SOCIAL_CARDS("socialCards");

        private final String typeString;

        HandicapFilePartType(String typeString) {
            this.typeString = typeString;
        }

        public String getTypeString() {
            return typeString;
        }
    }
}
