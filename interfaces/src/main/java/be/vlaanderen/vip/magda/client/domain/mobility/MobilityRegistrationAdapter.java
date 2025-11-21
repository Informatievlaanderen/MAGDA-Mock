package be.vlaanderen.vip.magda.client.domain.mobility;

import be.vlaanderen.vip.magda.client.rest.MagdaResponseJson;

import java.util.List;

public interface MobilityRegistrationAdapter {
    List<Registration> adapt(MagdaResponseJson response);
}
