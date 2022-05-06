package be.vlaanderen.vip.mock.magdaservice.exception;

public class AttestNotFoundException extends Exception {

    public AttestNotFoundException(String name) {
        super(String.format("Attest '%s' niet gevonden", name));
    }

    public AttestNotFoundException(String name, Exception cause) {
        super(String.format("Attest '%s' niet gevonden", name), cause);
    }
}
