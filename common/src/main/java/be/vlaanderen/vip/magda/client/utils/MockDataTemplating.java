package be.vlaanderen.vip.magda.client.utils;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.StringReader;
import java.io.StringWriter;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class MockDataTemplating {

    // !!!NOTE: very much WIP!!!
    public static String processTemplatingValues(String response) {
        MustacheFactory mf = new DefaultMustacheFactory();
        OffsetDateTime now = OffsetDateTime.now();
        Function<String, String> dateTemplateFunction = (text) -> processDateTemplate(text, now);
        Map<String, Object> scope = Map.of(
                "year", now.getYear(),
                "month", now.getMonthValue(),
                "day", now.getDayOfMonth(),
                "dateTemplate", dateTemplateFunction
        );
        Mustache mustache = mf.compile(new StringReader(response), "example");

        StringWriter writer = new StringWriter();
        mustache.execute(writer, scope);

        return writer.toString();
    }

    /**
     * Function to process the datetime templating, which happens by adjusting the current timestamp with the following possible modifiers:
     * - years (with "+1y" or "-5y" or any other number)
     * - quarters (with "+1q" or "-3q" or any other number)
     * - months (with "+1m" or "-5m" or any other number)
     * - days (with "+1d" or "-5d" or any other number)
     * - start of the current quarter (with "sq")
     * <p>
     * Each modifier should be separated by a ',' and they are executed from left to right.
     * <p>
     * Example: "-1y,sq,-2m,-3d" would do the following steps:
     * - take 1 year before today
     * - take the first day of the quarter
     * - take 2 months before that
     * - take 3 days before that
     */
    protected static String processDateTemplate(String dateTemplate, OffsetDateTime dateTime) {
        List<String> parts = Arrays.stream(dateTemplate.split(",")).toList();
        OffsetDateTime newTime = dateTime.minusDays(0);
        for (String part : parts) {
            Pattern dateTimeDiffPattern = Pattern.compile("([-+][0-9]*)?([a-z]+)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = dateTimeDiffPattern.matcher(part);
            if (!matcher.find())
                continue;
            String group = matcher.group(2).toLowerCase(Locale.ROOT);
            if (group.endsWith("sq")) {
                // determine start of the quarter
                int month = Math.floorDivExact(newTime.getMonthValue() - 1, 3) * 3 + 1;
                int day = 1;
                newTime = newTime.withMonth(month);
                newTime = newTime.withDayOfMonth(day);
            } else if (group.endsWith("y")) {
                var years = Integer.parseInt(matcher.group(1));
                newTime = newTime.plusYears(years);
            } else if (group.endsWith("q")) {
                var months = Integer.parseInt(matcher.group(1)) * 3;
                newTime = newTime.plusMonths(months);
            } else if (group.endsWith("m")) {
                var months = Integer.parseInt(matcher.group(1));
                newTime = newTime.plusMonths(months);
            } else if (group.endsWith("d")) {
                var days = Integer.parseInt(matcher.group(1));
                newTime = newTime.plusDays(days);
            }
            // QUESTION: should the format be added? with a sensible default
            // TODO: add more options, first day of quarter, days, months, weeks
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        log.debug("Parsing date template {} on {} returns date {}", dateTemplate, dateTime, newTime);
        return newTime.format(formatter);
    }
}
