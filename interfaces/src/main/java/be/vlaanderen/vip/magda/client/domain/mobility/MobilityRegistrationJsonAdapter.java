package be.vlaanderen.vip.magda.client.domain.mobility;

import be.vlaanderen.vip.magda.client.rest.MagdaResponseJson;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class MobilityRegistrationJsonAdapter implements MobilityRegistrationAdapter {
    public List<Registration> adapt(MagdaResponseJson response) {
        ObjectMapper mapper = new ObjectMapper();
        List<Registration> registrations = new ArrayList<>();
        JsonNode mainNode = response.json();
        mainNode.get("items").iterator().forEachRemaining(item -> registrations.add(mapper.convertValue(item, Registration.class)));
        return registrations;
    }
}
