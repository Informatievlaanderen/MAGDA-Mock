package be.vlaanderen.vip.magda.client.domain.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class KwartaalTest {
    private static Stream<Arguments> quarterMonthParameters() {
        return Stream.of(
                Arguments.of(1,1),
                Arguments.of(2,1),
                Arguments.of(3,1),
                Arguments.of(4,2),
                Arguments.of(5,2),
                Arguments.of(6,2),
                Arguments.of(7,3),
                Arguments.of(8,3),
                Arguments.of(9,3),
                Arguments.of(10,4),
                Arguments.of(11,4),
                Arguments.of(12,4)
        );
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4})
    public void verifyPasses(int kwartaalCijfer) {
        Kwartaal kwartaal = new Kwartaal(2000, kwartaalCijfer).verify();
        assertEquals(kwartaalCijfer, kwartaal.kwartaalcijfer());
    }

    @ParameterizedTest
    @ValueSource(ints = {-10000, -1, 5, 0})
    public void verifyFails(int kwartaalCijfer) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            new Kwartaal(2000, kwartaalCijfer).verify();
        });
    }

    @ParameterizedTest
    @MethodSource("quarterMonthParameters")
    public void ofDateFirstQuarter(int month, int quarter) {
        LocalDate date = LocalDate.of(2000, month, 1);
        Kwartaal kwartaal = Kwartaal.ofDate(date);
        assertEquals(quarter, kwartaal.kwartaalcijfer());
        assertEquals(2000, kwartaal.jaar());
    }
}
