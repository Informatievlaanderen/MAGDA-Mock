package be.vlaanderen.vip.magda.exception;

//TEMP: to be replaced by version from Wwoom Common
public class BronException extends RuntimeException {
    private static final long serialVersionUID = -3155129158010790297L;

    public BronException(String bericht) {
        super(bericht);
    }

    public BronException(String bericht, Throwable oorzaak) {
        super(bericht, oorzaak);
    }
}
