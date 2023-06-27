package be.vlaanderen.vip.magda.exception;

import be.vlaanderen.vip.magda.legallogging.model.UitzonderingEntry;
import lombok.Getter;

import java.io.Serial;
import java.util.List;

/**
 * An exception thrown when a response contained an Uitzonderingen section. The UitzonderingEntries are included.
 *
 * @see UitzonderingEntry
 */
@Getter
public class UitzonderingenSectionInResponseException extends ServerException {
    @Serial
    private static final long serialVersionUID = 2478927288540376650L;
    
    private final String insz;
    private final transient List<UitzonderingEntry> uitzonderingEntries;

    public UitzonderingenSectionInResponseException(String insz, List<UitzonderingEntry> uitzonderingEntries) {
        super("Backend error '" + uitzonderingEntries.get(0).toString() + "'");
        this.insz = insz;
        this.uitzonderingEntries = uitzonderingEntries;
    }
}
