package be.vlaanderen.vip.magda.legallogging.service;

import be.vlaanderen.vip.magda.legallogging.model.GefaaldeAanvraag;
import be.vlaanderen.vip.magda.legallogging.model.GeslaagdeAanvraag;
import be.vlaanderen.vip.magda.legallogging.model.MagdaAanvraag;
import be.vlaanderen.vip.magda.legallogging.model.OnbeantwoordeAanvraag;

public interface AfnemerLogService {

    void logGeslaagdeAanvraag(GeslaagdeAanvraag aanvraag);

    void logGefaaldeAanvraag(GefaaldeAanvraag gefaaldeAanvraag);

    void logOnbeantwoordeAanvraag(OnbeantwoordeAanvraag onbeantwoordeAanvraag);

    void logAanvraag(MagdaAanvraag aanvraag);
}
