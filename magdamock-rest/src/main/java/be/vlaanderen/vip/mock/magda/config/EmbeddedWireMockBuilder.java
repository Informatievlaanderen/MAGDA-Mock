package be.vlaanderen.vip.mock.magda.config;

import be.vlaanderen.vip.magda.client.utils.MockDataTemplating;
import com.github.jknack.handlebars.Helper;
import com.github.jknack.handlebars.Options;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.direct.DirectCallHttpServerFactory;
import com.github.tomakehurst.wiremock.extension.TemplateHelperProviderExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.FileCopyUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmbeddedWireMockBuilder {
    
    public static WireMockData wireMockServer() {
        return wireMockServer(0);
    }

    private static String unpackWireMockResources() {
        try {
            Path tempDir = Files.createTempDirectory("magdamock-rest-wiremock");

            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath*:wiremock/**/*");

            for (Resource resource : resources) {
                if (resource.exists() && resource.isReadable() && resource.getURL().getPath().endsWith(".json")) {
                    String fullPath = resource.getURL().getPath();

                    String wiremockPrefix = "wiremock/";
                    String relativePath = fullPath.substring(fullPath.indexOf(wiremockPrefix) + wiremockPrefix.length());

                    if (relativePath.isEmpty()) continue;

                    Path target = tempDir.resolve(relativePath);
                    Files.createDirectories(target.getParent());

                    try (var is = resource.getInputStream()) {
                        FileCopyUtils.copy(is, Files.newOutputStream(target));
                    }
                }
            }
            return tempDir.toAbsolutePath().toString();
        } catch (Exception e) {
            throw new RuntimeException("Unable to provide files for wiremock", e);
        }
    }

    public static WireMockData wireMockServer(Integer wiremockPort) {
        String fileSource = unpackWireMockResources();
        DirectCallHttpServerFactory factory = new DirectCallHttpServerFactory();

        WireMockConfiguration config = WireMockConfiguration.wireMockConfig()
                .port(wiremockPort)
                .httpServerFactory(factory)
                .globalTemplating(true)
                .extensions(getTemplateHelperExtensions())
                .usingFilesUnderDirectory(fileSource);

        return new WireMockData(new WireMockServer(config), factory);
    }

    private static TemplateHelperProviderExtension getTemplateHelperExtensions() {
        return new TemplateHelperProviderExtension() {
            @Override
            public String getName() {
                return "custom-helpers";
            }

            @Override
            public Map<String, Helper<?>> provideTemplateHelpers() {
                return Map.of("parseDate", new DateParseHelper(),
                        "formatDate", new DateFormatter(),
                        "dateMath", new DateMathHelper(),
                        "startOfQuarter", new StartQuarterHelper(),
                        "endOfQuarter", new EndQuarterHelper());
            }
        };
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

    private static class DateParseHelper implements Helper<Object> {
        @Override
        public Object apply(Object toParse, Options options) {
            LocalDate date;
            try {
                date = LocalDate.parse(toParse.toString(), DateTimeFormatter.RFC_1123_DATE_TIME);
            } catch (Exception ex) {
                date = LocalDate.now();
            }
            return date;
        }
    }

    private static class DateFormatter implements Helper<LocalDate> {
        @Override
        public Object apply(LocalDate date, Options options) {
            String format = options.param(0, "yyyy-MM-dd");
            return DateTimeFormatter.ofPattern(format).format(date);
        }
    }

    private static class DateMathHelper implements Helper<LocalDate> {
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

    private static class StartQuarterHelper implements Helper<LocalDate> {
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

    private static class EndQuarterHelper implements Helper<LocalDate> {
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