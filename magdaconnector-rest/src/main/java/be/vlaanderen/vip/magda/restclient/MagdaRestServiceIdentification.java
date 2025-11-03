package be.vlaanderen.vip.magda.restclient;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class MagdaRestServiceIdentification implements Serializable {

    @Serial
    private static final long serialVersionUID = 3951178196558221225L;
    private String name;
}
