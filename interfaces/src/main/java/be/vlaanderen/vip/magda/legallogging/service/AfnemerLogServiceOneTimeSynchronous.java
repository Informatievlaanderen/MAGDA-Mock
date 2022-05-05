package be.vlaanderen.vip.magda.legallogging.service;

import be.vlaanderen.vip.magda.config.MagdaConfigDto;
import be.vlaanderen.vip.magda.legallogging.model.GefaaldeAanvraag;
import be.vlaanderen.vip.magda.legallogging.model.GelogdeAanvraag;
import be.vlaanderen.vip.magda.legallogging.model.GeslaagdeAanvraag;
import be.vlaanderen.vip.magda.legallogging.model.MagdaAanvraag;
import be.vlaanderen.vip.magda.legallogging.model.OnbeantwoordeAanvraag;
import be.vlaanderen.vip.magda.legallogging.model.Uitzondering;
import be.vlaanderen.vip.security.client.oauth.core.OAuthClient;
import lombok.CustomLog;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

@CustomLog(topic = "MAGDA:LegalLog")
public class AfnemerLogServiceOneTimeSynchronous implements AfnemerLogService {
    private final MagdaConfigDto config;
    private final RestTemplate restTemplate;
    private final OAuthClient oAuthClient;

    public AfnemerLogServiceOneTimeSynchronous(MagdaConfigDto config,
                                               OAuthClient oAuthClient,
                                               RestTemplateBuilder restTemplateBuilder) {
        this.config = config;
        this.oAuthClient = oAuthClient;
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(3))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
    }

    @Override
    public void logGeslaagdeAanvraag(GeslaagdeAanvraag aanvraag) {
        if (dientGeloggedTeWorden(aanvraag)) {
            ZonedDateTime dateTime = ZonedDateTime.now(ZoneId.of("Z"));
            final Antwoord antwoord = Antwoord.builder()
                    .transactieID(aanvraag.getTransactieID().toString())
                    .referte(aanvraag.getLocalTransactieID().toString())
                    .dienst(aanvraag.getDienst())
                    .gebruiker(DigestUtils.sha256Hex(aanvraag.getInsz()))
                    .overWie(hashInsz(aanvraag.getOverWie()))
                    .wie(new Toepassing(aanvraag.getRegistratie().getNaam(), aanvraag.getRegistratie().getUri(), aanvraag.getRegistratie().getHoedanigheid()))
                    .wanneer(dateTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
                    .build();
            callLegalLoggingService(antwoord, "/antwoord");
        }
    }

    @Override
    public void logGefaaldeAanvraag(GefaaldeAanvraag aanvraag) {
        if (dientGeloggedTeWorden(aanvraag)) {
            ZonedDateTime dateTime = ZonedDateTime.now(ZoneId.of("Z"));
            final UitzonderingBoodschap antwoord = UitzonderingBoodschap.builder()
                    .transactieID(aanvraag.getTransactieID().toString())
                    .referte(aanvraag.getLocalTransactieID().toString())
                    .dienst(aanvraag.getDienst())
                    .gebruiker(DigestUtils.sha256Hex(aanvraag.getInsz()))
                    .wie(new Toepassing(aanvraag.getRegistratie().getNaam(), aanvraag.getRegistratie().getUri(), aanvraag.getRegistratie().getHoedanigheid()))
                    .wanneer(dateTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
                    .fouten(mapFouten(aanvraag.getUitzonderingen()))
                    .build();
            callLegalLoggingService(antwoord, "/uitzondering");
        }
    }

    @Override
    public void logOnbeantwoordeAanvraag(OnbeantwoordeAanvraag aanvraag) {
        if (dientGeloggedTeWorden(aanvraag)) {
            ZonedDateTime dateTime = ZonedDateTime.now(ZoneId.of("Z"));
            final Onbeantwoord antwoord = Onbeantwoord.builder()
                    .transactieID(aanvraag.getTransactieID().toString())
                    .referte(aanvraag.getLocalTransactieID().toString())
                    .dienst(aanvraag.getDienst())
                    .gebruiker(DigestUtils.sha256Hex(aanvraag.getInsz()))
                    .wie(new Toepassing(aanvraag.getRegistratie().getNaam(), aanvraag.getRegistratie().getUri(), aanvraag.getRegistratie().getHoedanigheid()))
                    .wanneer(dateTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
                    .build();
            callLegalLoggingService(antwoord, "/onbeantwoord");
        }
    }

    @Override
    public void logAanvraag(MagdaAanvraag aanvraag) {
        if (dientGeloggedTeWorden(aanvraag)) {
            ZonedDateTime dateTime = ZonedDateTime.now(ZoneId.of("Z"));
            final Vraag vraag = Vraag.builder()
                    .transactieID(aanvraag.getTransactieID().toString())
                    .referte(aanvraag.getLocalTransactieID().toString())
                    .dienst(aanvraag.getDienst())
                    .gebruiker(DigestUtils.sha256Hex(aanvraag.getInsz()))
                    .overWie(hashInsz(aanvraag.getOverWie()))
                    .wie(new Toepassing(aanvraag.getRegistratie().getNaam(), aanvraag.getRegistratie().getUri(), aanvraag.getRegistratie().getHoedanigheid()))
                    .wanneer(dateTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME))
                    .build();
            callLegalLoggingService(vraag, "/vraag");
        }
    }

    private boolean dientGeloggedTeWorden(GelogdeAanvraag aanvraag) {
        return StringUtils.isNotEmpty(aanvraag.getInsz()) && aanvraag.getOverWie() != null && aanvraag.getOverWie().size() > 0;
    }

    private List<Fout> mapFouten(List<Uitzondering> uitzonderingen) {
        return uitzonderingen.stream().map(this::maakFout).collect(toList());
    }

    private Fout maakFout(Uitzondering uitzondering) {
        return Fout.builder()
                .diagnose(uitzondering.getDiagnose())
                .identificatie(uitzondering.getIdentificatie())
                .oorsprong(uitzondering.getOorsprong())
                .type(uitzondering.getUitzonderingType().name())
                .build();
    }

    private List<String> hashInsz(List<String> overWie) {
        return overWie.stream()
                .map(DigestUtils::sha256Hex)
                .collect(toList());
    }

    private void callLegalLoggingService(Object vraag, String resource) {
        if (config.getLegalloggingUrl() == null) {
            log.error("Geen URL geconfigureerd voor LegalLogging");
        }

        final String token = oAuthClient.getAccessToken(OAuthClient.LEGAL_LOGGING_REGISTRATION);

        if (token != null) {
            final String url = config.getLegalloggingUrl() + resource;
            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
                headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
                headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
                restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(vraag, headers), Object.class);
            } catch (Exception e) {
                log.warn("Legal logging oproep naar '{}' gefaald", url, e);
            }
        }
    }
}
