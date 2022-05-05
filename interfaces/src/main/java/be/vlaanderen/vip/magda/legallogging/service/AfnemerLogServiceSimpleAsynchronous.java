package be.vlaanderen.vip.magda.legallogging.service;

import be.vlaanderen.vip.magda.config.MagdaConfigDto;
import be.vlaanderen.vip.magda.legallogging.model.GefaaldeAanvraag;
import be.vlaanderen.vip.magda.legallogging.model.GeslaagdeAanvraag;
import be.vlaanderen.vip.magda.legallogging.model.MagdaAanvraag;
import be.vlaanderen.vip.magda.legallogging.model.OnbeantwoordeAanvraag;
import be.vlaanderen.vip.security.client.oauth.core.OAuthClient;
import lombok.CustomLog;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.Async;

@CustomLog(topic = "MAGDA:LegalLog")
public class AfnemerLogServiceSimpleAsynchronous extends AfnemerLogServiceOneTimeSynchronous {
    public AfnemerLogServiceSimpleAsynchronous(MagdaConfigDto config,
                                               OAuthClient oAuthClient,
                                               RestTemplateBuilder restTemplateBuilder) {
        super(config, oAuthClient, restTemplateBuilder);
    }

    @Override
    @Async
    public void logGeslaagdeAanvraag(GeslaagdeAanvraag aanvraag) {
        super.logGeslaagdeAanvraag(aanvraag);
    }

    @Override
    @Async
    public void logGefaaldeAanvraag(GefaaldeAanvraag aanvraag) {
        super.logGefaaldeAanvraag(aanvraag);
    }

    @Override
    @Async
    public void logOnbeantwoordeAanvraag(OnbeantwoordeAanvraag aanvraag) {
        super.logOnbeantwoordeAanvraag(aanvraag);
    }

    @Override
    @Async
    public void logAanvraag(MagdaAanvraag aanvraag) {
        super.logAanvraag(aanvraag);
    }
}
