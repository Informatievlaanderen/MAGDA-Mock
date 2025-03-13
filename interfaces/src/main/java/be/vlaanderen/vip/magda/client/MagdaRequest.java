package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.client.diensten.subject.SubjectIdentificationNumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
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
 * <li>registration: registration code that can be resolved by a MagdaHoedanigService to obtain registration info (defaults to code "default" if not specified); a MagdaRegistrationInfo object can be directly given too.</li>
 * </ul>
 */
@Getter
@EqualsAndHashCode
public abstract class MagdaRequest {

    protected abstract static class Builder<SELF extends Builder<SELF>> {
        private String registrationKey;
        private MagdaRegistrationInfo registrationInfo;

        @SuppressWarnings("unchecked")
        public SELF registration(String registrationKey) {
            this.registrationKey = registrationKey;
            return (SELF) this;
        }

        @SuppressWarnings("unchecked")
        public SELF registration(MagdaRegistrationInfo registrationInfo) {
            this.registrationInfo = registrationInfo;
            return (SELF) this;
        }

        protected Registration getRegistration() {
            if(registrationKey != null) {
                if(registrationInfo != null) {
                    throw new IllegalStateException("Cannot provide both registration key and registration info.");
                } else {
                    return new KeyRegistration(registrationKey);
                }
            } else {
                if(registrationInfo != null) {
                    return new DirectRegistration(registrationInfo);
                } else {
                    return new KeyRegistration(DEFAULT_REGISTRATION);
                }
            }
        }
    }

    public static final String DEFAULT_REGISTRATION = "default";

    @Setter
    private UUID correlationId;
    @NotNull
    private final Registration registration;

    protected MagdaRequest(@NotNull Registration registration) {
        this.registration = registration;
    }

    public abstract MagdaServiceIdentification magdaServiceIdentification();

    public abstract SubjectIdentificationNumber getSubject();

    public MagdaDocument toMagdaDocument(UUID requestId, MagdaRegistrationInfo magdaRegistrationInfo, Instant instant) {
        var serviceId = magdaServiceIdentification();
        var document = Objects.requireNonNull(MagdaDocument.fromResource(MagdaDocument.class, "/templates/" + serviceId.getName() + "/" + serviceId.getVersion() + "/template.xml"));
        fillIn(document, requestId, magdaRegistrationInfo, instant);

        return document;
    }

    public MagdaDocument toMagdaDocument(UUID requestId, MagdaRegistrationInfo magdaRegistrationInfo) {
        return toMagdaDocument(requestId, magdaRegistrationInfo, Instant.now());
    }

    protected void fillInCommonFields(MagdaDocument request, UUID requestId, MagdaRegistrationInfo magdaRegistrationInfo, Instant instant) {
        request.setValue("//Referte", requestId.toString());
        request.setValue(getSubject().getXPathExpression(), getSubject().getValue());

        var ldt = LocalDateTime.ofInstant(instant, ZoneId.of("Europe/Brussels"));

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

    protected abstract void fillIn(MagdaDocument request, UUID requestId, MagdaRegistrationInfo magdaRegistrationInfo, Instant instant);
}