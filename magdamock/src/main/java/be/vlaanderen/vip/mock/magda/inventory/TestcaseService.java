package be.vlaanderen.vip.mock.magda.inventory;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@Builder
@EqualsAndHashCode
public class TestcaseService {
    private final String service;
    private final String version;

    @Override
    public String toString() {
        return String.join("/", service, version);
    }
}
