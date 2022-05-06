package be.vlaanderen.vip.mock.magdaservice.util;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.Optional.ofNullable;

public class INSZ {

    private static final int START_MAAND = 2;
    private static final int START_DAG = 4;
    private static final int START_VOLGNUMMER = 6;
    private static final int START_CHECKSUM = 9;
    private static final int LENGTE_INSZ = 11;

    private INSZ() {
    }

    /**
     * Bepaalt of string voldoet aan INSZ criteria:
     * - punctuatie wordt niet in rekening genomen
     * - er zijn 11 decimale digits
     * - de eerste 9 digits zijn het basis nummer; de 2 laatste digits zijn de checksum
     * - 97 - (basis modulo 97) == checksum
     *
     * @param inszNummer insz nummer string, mag punctuatie karakters bevatten
     * @return input voldoet aan INSZ regels
     */
    public static boolean isGeldig(final String inszNummer) {
        String insz = zonderPunctuatie(inszNummer);
        if (insz.length() == LENGTE_INSZ) {
            String checksum = insz.substring(START_CHECKSUM, LENGTE_INSZ);
            String basis = insz.substring(0, START_CHECKSUM);
            long checksumNb = Long.parseLong(checksum);
            return isValidChecksum(basis, checksumNb) || isValidChecksum("2" + basis, checksumNb);
        } else {
            return false;
        }
    }

    public static boolean isGeborenNa2000(final String inszNummer) {
        String insz = zonderPunctuatie(inszNummer);
        if (insz.length() == LENGTE_INSZ) {
            String checksum = insz.substring(START_CHECKSUM, LENGTE_INSZ);
            String basis = insz.substring(0, START_CHECKSUM);
            long checksumNb = Long.parseLong(checksum);
            return isValidChecksum("2" + basis, checksumNb);
        } else {
            return false;
        }
    }

    private static boolean isValidChecksum(String basis, long checksumNb) {
        long basisNb = Long.parseLong(basis);
        long mod = 97 - (basisNb % 97);
        return checksumNb == mod;
    }

    /**
     * Haalt punctuatie uit insz string zodat enkel digits overblijven
     *
     * @param inszNummer string met digits en punctuatie
     * @return string met enkel digits
     */
    public static String zonderPunctuatie(String inszNummer) {
        return inszNummer.replaceAll("[^0-9]+", "");
    }


    public static boolean isRijksRegister(String rijksRegisterNummer) {
        return ofNullable(rijksRegisterNummer).map(
                rrn -> {
                    try {
                        return Integer.valueOf(rrn.substring(START_MAAND, START_DAG)) < 13;
                    } catch (NumberFormatException nfe) {
                        return false;
                    }
                }
        ).orElse(false);
    }

    public static boolean isKsz(String rijksRegisterNummer) {
        return ofNullable(rijksRegisterNummer).map(
                rrn -> {
                    try {
                        return Integer.valueOf(rrn.substring(START_MAAND, START_DAG)) > 20;
                    } catch (NumberFormatException nfe) {
                        return false;
                    }
                }
        ).orElse(false);
    }

    /**
     * Als de 2 laatste cijfers van het INSZ van de persoon = 00, EN als de inputbron= RR, dan is dit nummer een fictief INSZ
     *
     * @param insz INSZ dat gecontroleerd moet worden
     * @return Boolean, om aan te geven of het INSZ fictief is
     */
    public static boolean isFictief(String insz) {
        return ofNullable(insz).map(rrn -> isRijksRegister(insz) && "00".equals(insz.substring(insz.length() - 2))).orElse(false);
    }

    public static boolean isMannelijk(String insz) {
        return ofNullable(insz)
                .filter(StringUtils::isNumeric)
                .map(i -> Integer.valueOf(i.substring(START_VOLGNUMMER, START_CHECKSUM)) % 2 == 1)
                .orElse(false);
    }

    public static boolean isVrouwelijk(String insz) {
        return ofNullable(insz)
                .filter(StringUtils::isNumeric)
                .map(i -> Integer.valueOf(i.substring(START_VOLGNUMMER, START_CHECKSUM)) % 2 == 0)
                .orElse(false);
    }

    public static LocalDate isGeborenOp(String insz) {
        if (StringUtils.isEmpty(insz) || isFictief(insz)) {
            return null;
        }
        int offsetJaar = isGeborenNa2000(insz) ? 2000 : 1900;
        insz = zonderPunctuatie(insz);
        int jaar = Integer.parseInt(insz.substring(0, 2)) + offsetJaar;
        int maand = Integer.parseInt(insz.substring(2, 4));
        if (maand >= 40) {
            maand -= 40;
        }
        if (maand >= 20) {
            maand -= 20;
        }
        int dag = Integer.parseInt(insz.substring(4, 6));

        if (maand == 0) {
            maand = 12;
            dag = 31;
        } else if (dag == 0) {
            if (maand == 12) {
                return LocalDate.of(jaar, 12, 31);
            } else {
                return LocalDate.of(jaar, maand + 1, 1).minus(1, DAYS);
            }
        }

        return LocalDate.of(jaar, maand, dag);

    }

    public static LocalDate isMeerderJarigOp(String insz) {
        final LocalDate geborenOp = isGeborenOp(insz);
        if (geborenOp == null) {
            return null;
        }
        return geborenOp.plus(18, ChronoUnit.YEARS);
    }
}
