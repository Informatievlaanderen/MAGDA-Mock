package be.vlaanderen.vip.magda.client.domeinservice;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class MagdaHoedanigheid {
    private String naam;
    private String uri;
    private String hoedanigheid;
}