package be.vlaanderen.vip.magda.exception;

import be.vlaanderen.vip.magda.legallogging.model.Uitzondering;
import lombok.Getter;

import java.util.List;

@Getter
public class BackendUitzonderingenException extends BronException {
    private static final long serialVersionUID = 2478927288540376650L;
    
    private final String insz;
    private final List<Uitzondering> uitzonderingen;

    public BackendUitzonderingenException(String insz, List<Uitzondering> uitzonderingen) {
        super("Backend error '" + uitzonderingen.get(0).toString() + "'");
        this.insz = insz;
        this.uitzonderingen = uitzonderingen;
    }
}
