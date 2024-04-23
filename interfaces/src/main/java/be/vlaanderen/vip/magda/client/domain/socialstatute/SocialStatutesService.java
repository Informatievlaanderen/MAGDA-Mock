package be.vlaanderen.vip.magda.client.domain.socialstatute;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.diensten.GeefMultipleSociaalStatuutRequest;
import be.vlaanderen.vip.magda.client.diensten.GeefSociaalStatuutRequest;

public interface SocialStatutesService {

    SocialStatutes getSocialStatutes(GeefMultipleSociaalStatuutRequest request) throws MagdaClientException;

}
