package be.vlaanderen.vip.mock.magda.inventory;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TestcaseService {
    private final String service ;
    private final String version ;
    private final String description ;

}
