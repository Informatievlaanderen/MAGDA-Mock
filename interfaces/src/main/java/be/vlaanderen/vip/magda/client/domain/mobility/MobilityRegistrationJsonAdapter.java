package be.vlaanderen.vip.magda.client.domain.mobility;

import be.vlaanderen.vip.magda.client.rest.MagdaResponseJson;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class MobilityRegistrationJsonAdapter implements MobilityRegistrationAdapter {
    private final ObjectMapper mapper;

    public MobilityRegistrationJsonAdapter() {
        mapper = new ObjectMapper();
    }

    public List<Registration> adapt(MagdaResponseJson response) {
        if (response.statusCode() == 204) {
            return new ArrayList<>();
        }
        JsonNode mainNode = response.json();
        return Optional.ofNullable(mainNode.get("items"))
                .map(items -> StreamSupport.stream(items.spliterator(), false))
                .orElse(Stream.empty())
                .map(item -> mapper.convertValue(item, Registration.class))
                .toList();
    }
}
