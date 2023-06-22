package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * The common part of a request to a MAGDA service. Has subclasses for specific service/versions.
 * <p>
 * Contains:
 * <ul>
 * <li>correlationId: unique correlation ID of the request</li>
 * <li>requestId: unique ID of the request</li>
 * <li>requestingPartyInsz: the INSZ number of the requesting party</li>
 * <li>subjectInsz: the INSZ number of the party about which the information is requested (defaults to the requesting party if not specified)</li>
 * <li>registration: registration code that can be resolved by a MagdaHoedanigService to obtain registration info (defaults to code "default" if not specified)</li>
 * </ul>
 */
@Getter
public abstract class MagdaRequest {

    protected abstract static class Builder<SELF extends Builder<SELF>> {
        private String requestingPartyInsz;
        private String subjectInsz;
        private String registration;

        @SuppressWarnings("unchecked")
        public SELF requestingPartyInsz(String requestingPartyInsz) {
            this.requestingPartyInsz = requestingPartyInsz;
            return (SELF) this;
        }

        @SuppressWarnings("unchecked")
        public SELF subjectInsz(String subjectInsz) {
            this.subjectInsz = subjectInsz;
            return (SELF) this;
        }

        @SuppressWarnings("unchecked")
        public SELF registration(String registration) {
            this.registration = registration;
            return (SELF) this;
        }

        protected String getRequestingPartyInsz() {
            return requestingPartyInsz;
        }

        protected String getSubjectInsz() {
            return StringUtils.defaultString(subjectInsz, requestingPartyInsz);
        }

        protected String getRegistratie() {
            return StringUtils.defaultString(registration, "default");
        }
    }

    private final UUID correlationId = CorrelationId.get();
    private final UUID requestId = UUID.randomUUID();
    @NotNull
    private final String requestingPartyInsz;
    @NotNull
    private final String subjectInsz;
    @NotNull
    private final String registration;

    protected MagdaRequest(@NotNull String requestingPartyInsz, @NotNull String subjectInsz, @NotNull String registration) {
        this.requestingPartyInsz = requestingPartyInsz;
        this.subjectInsz = subjectInsz;
        this.registration = registration;
    }

    public abstract MagdaServiceIdentification magdaServiceIdentification();

    public MagdaDocument toMagdaDocument(MagdaRegistrationInfo magdaRegistrationInfo) {
        var serviceId = magdaServiceIdentification();
        var document = MagdaDocument.fromResource(MagdaDocument.class, "/templates/" + serviceId.getName() + "/" + serviceId.getVersion() + "/template.xml");
        fillIn(document, magdaRegistrationInfo);

        return document;
    }

    protected void fillInCommonFields(MagdaDocument request, MagdaRegistrationInfo magdaRegistrationInfo) {
        request.setValue("//Referte", getRequestId().toString());
        request.setValue("//INSZ", getSubjectInsz());

        final var now = Instant.now();
        var ldt = LocalDateTime.ofInstant(now, ZoneId.of("Europe/Brussels"));

        var dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE;
        var today = ldt.format(dateFormatter);
        request.setValue("//Context/Bericht/Tijdstip/Datum", today);

        // Hardcoded 000 milliseconden wordt door alle Magda services aanvaard
        // Afwezigheid van milliseconden of milliseconden <> 000 wordt door sommige services geweigerd
        var timeFormat = DateTimeFormatter.ofPattern("HH:mm:ss");
        var time = ldt.format(timeFormat) + ".000";
        request.setValue("//Context/Bericht/Tijdstip/Tijd", time);

        request.setValue("//Context/Bericht/Afzender/Identificatie", magdaRegistrationInfo.getIdentification());

        var hoedanigheidscode = magdaRegistrationInfo.getHoedanigheidscode();
        if(hoedanigheidscode.isEmpty()) {
            request.removeNode("//Context/Bericht/Afzender/Hoedanigheid");
        } else {
            request.setValue("//Context/Bericht/Afzender/Hoedanigheid", hoedanigheidscode.get());
        }
    }

    protected abstract void fillIn(MagdaDocument request, MagdaRegistrationInfo magdaRegistrationInfo);
}