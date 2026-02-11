package be.vlaanderen.vip.magda.client.utils;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.Template;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class MockDataTemplating {

    public static String processTemplatingValues(String response, LocalDate now) {
        try {
            Handlebars handlebars = new Handlebars();
            handlebars.registerHelper("formatDate", new DateFormatter());
            handlebars.registerHelper("dateMath", new DateMathHelper());
            handlebars.registerHelper("startOfQuarter", new StartQuarterHelper());
            handlebars.registerHelper("endOfQuarter", new EndQuarterHelper());
            Template template = handlebars.compileInline(response);
            HashMap<String, Object> data = new HashMap<>();
            data.put("now", now);
            return template.apply(data);
        } catch (IOException e) {
            return response;
        }
    }

    private static ChronoUnit parseUnit(String u) {
        return switch (u.toLowerCase()) {
            case "y" -> ChronoUnit.YEARS;
            case "m" -> ChronoUnit.MONTHS;
            case "h" -> ChronoUnit.HOURS;
            case "s" -> ChronoUnit.SECONDS;
            default -> ChronoUnit.DAYS;
        };
    }

    public static class DateFormatter implements Helper<LocalDate> {
        @Override
        public Object apply(LocalDate date, Options options) {
            String format = options.param(0, "yyyy-MM-dd");
            return DateTimeFormatter.ofPattern(format).format(date);
        }
    }

    public static class DateMathHelper implements Helper<LocalDate> {
        @Override
        public Object apply(LocalDate date, Options options) {
            String addition = options.param(0);
            Pattern dateTimeDiffPattern = Pattern.compile("([-+][0-9]*)?([a-z]+)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = dateTimeDiffPattern.matcher(addition);
            if (!matcher.find())
                return date;
            String chronoUnit = matcher.group(2).toLowerCase(Locale.ROOT);
            int amountToAdd = Integer.parseInt(matcher.group(1));
            return date.plus(amountToAdd, parseUnit(chronoUnit));
        }
    }

    public static class StartQuarterHelper implements Helper<LocalDate> {
        @Override
        public Object apply(LocalDate date, Options options) {
            LocalDate newTime = date.minusDays(0);
            int month = Math.floorDivExact(newTime.getMonthValue() - 1, 3) * 3 + 1;
            int day = 1;
            newTime = newTime.withMonth(month);
            newTime = newTime.withDayOfMonth(day);
            return newTime;
        }
    }

    public static class EndQuarterHelper implements Helper<LocalDate> {
        @Override
        public Object apply(LocalDate date, Options options) {
            LocalDate newTime = date.plusMonths(3);
            int month = Math.floorDivExact(newTime.getMonthValue() - 1, 3) * 3 + 1;
            int day = 1;
            newTime = newTime.withMonth(month);
            newTime = newTime.withDayOfMonth(day);
            newTime = newTime.minusDays(1);
            return newTime;
        }
    }
}
