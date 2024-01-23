package be.vlaanderen.vip.magda.client.xml.node;

import java.io.Serial;

public class NodeException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -4011274698040434453L;

    public NodeException(Throwable t) {
        super(t);
    }
}
