package be.vlaanderen.vip.magda.client.domain.dto;

import java.io.Serial;

public class IncompleteDateMissingPartException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 8924607486939344351L;

    public IncompleteDateMissingPartException(String message) {
        super(message);
    }
}