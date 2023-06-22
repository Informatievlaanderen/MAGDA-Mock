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
 * <li>insz: the INSZ number of the party about which the information is requested</li>
 * <li>aboutWhom: XXX</li>
 * <li>registration: registration code that can be resolved by a MagdaHoedanigService to obtain registration info</li>
 * </ul>
 */
@Getter
public abstract class MagdaRequest {

    protected abstract static class Builder<SELF extends Builder<SELF>> {
        private String insz;
        private String aboutWhom;
        private String registration;

        @SuppressWarnings("unchecked")
        public SELF insz(String insz) {
            this.insz = insz;
            return (SELF) this;
        }

        @SuppressWarnings("unchecked")
        public SELF aboutWhom(String aboutWhom) {
            this.aboutWhom = aboutWhom;
            return (SELF) this;
        }

        @SuppressWarnings("unchecked")
        public SELF registration(String registration) {
            this.registration = registration;
            return (SELF) this;
        }

        protected String getInsz() {
            return insz;
        }

        protected String getAboutWhom() {
            return StringUtils.defaultString(aboutWhom, insz);
        }

        protected String getRegistratie() {
            return StringUtils.defaultString(registration, "default");
        }
    }

    private final UUID correlationId = CorrelationId.get();
    private final UUID requestId = UUID.randomUUID();
    @NotNull
    private final String insz;
    @NotNull
    private final String aboutWhom; // XXX remove aboutWhom and replace it entirely with INSZ?
    @NotNull
    private final String registration;

    protected MagdaRequest(@NotNull String insz, @NotNull String aboutWhom, @NotNull String registration) {
        this.insz = insz;
        this.aboutWhom = aboutWhom;
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
        request.setValue("//INSZ", getAboutWhom());

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