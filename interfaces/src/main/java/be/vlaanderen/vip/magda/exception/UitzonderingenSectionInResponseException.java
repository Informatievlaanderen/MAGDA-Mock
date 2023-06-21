package be.vlaanderen.vip.magda.exception;

import be.vlaanderen.vip.magda.legallogging.model.UitzonderingEntry;
import lombok.Getter;

import java.io.Serial;
import java.util.List;

@Getter
public class UitzonderingenSectionInResponseException extends ServerException {
    @Serial
    private static final long serialVersionUID = 2478927288540376650L;
    
    private final String insz;
    private final transient List<UitzonderingEntry> uitzonderingen;

    public UitzonderingenSectionInResponseException(String insz, List<UitzonderingEntry> uitzonderingen) {
        super("Backend error '" + uitzonderingen.get(0).toString() + "'");
        this.insz = insz;
        this.uitzonderingen = uitzonderingen;
    }
}
