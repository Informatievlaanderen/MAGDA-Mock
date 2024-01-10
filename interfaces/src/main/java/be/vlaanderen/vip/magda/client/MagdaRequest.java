package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.client.diensten.subject.SubjectIdentificationNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

/**
 * The common part of a request to a MAGDA service. Has subclasses for specific service/versions.
 * <p>
 * Contains:
 * <ul>
 * <li>correlationId: unique correlation ID of the request</li>
 * <li>requestId: unique ID of the request</li>
 * <li>registration: registration code that can be resolved by a MagdaHoedanigService to obtain registration info (defaults to code "default" if not specified)</li>
 * </ul>
 */
@Getter
public abstract class MagdaRequest {

    protected abstract static class Builder<SELF extends Builder<SELF>> {
        private String registration;

        @SuppressWarnings("unchecked")
        public SELF registration(String registration) {
            this.registration = registration;
            return (SELF) this;
        }

        protected String getRegistration() {
            return Objects.toString(registration, DEFAULT_REGISTRATION);
        }
    }

    public static final String DEFAULT_REGISTRATION = "default";

    @Setter
    private UUID correlationId;
    private final UUID requestId = UUID.randomUUID();
    @NotNull
    private final String registration;

    protected MagdaRequest(@NotNull String registration) {
        this.registration = registration;
    }

    public abstract MagdaServiceIdentification magdaServiceIdentification();

    public abstract SubjectIdentificationNumber getSubject();

    public MagdaDocument toMagdaDocument(MagdaRegistrationInfo magdaRegistrationInfo) {
        var serviceId = magdaServiceIdentification();
        var document = Objects.requireNonNull(MagdaDocument.fromResource(MagdaDocument.class, "/templates/" + serviceId.getName() + "/" + serviceId.getVersion() + "/template.xml"));
        fillIn(document, magdaRegistrationInfo);

        return document;
    }

    protected void fillInCommonFields(MagdaDocument request, MagdaRegistrationInfo magdaRegistrationInfo) {
        request.setValue("//Referte", getRequestId().toString());
        request.setValue(getSubject().getXPathExpression(), getSubject().getValue());

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