package be.vlaanderen.vip.magda.exception;

//TEMP: to be replaced by version from Wwoom Common
public class BronException extends RuntimeException {
    public BronException(String bericht) {
        super(bericht);
    }

    public BronException(String bericht, Throwable oorzaak) {
        super(bericht, oorzaak);
    }
}
