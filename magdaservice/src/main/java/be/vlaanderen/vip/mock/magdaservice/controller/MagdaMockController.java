package be.vlaanderen.vip.mock.magdaservice.controller;


import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.connection.MagdaConnection;
import be.vlaanderen.vip.magda.exception.MagdaConnectionException;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.http.MediaType.TEXT_XML;
import static org.springframework.util.MimeTypeUtils.APPLICATION_XML_VALUE;
import static org.springframework.util.MimeTypeUtils.TEXT_XML_VALUE;

@RestController
@Slf4j
public class MagdaMockController {
    // Gemeenschappelijk endpoint voor alle MAGDA SOAP 2.0 webservices
    private static final String MAGDA_SOAP_02_00 = "Magda-02.00/soap/WebService";

    private final MagdaConnection mockConnection;

    public MagdaMockController(@Qualifier("magdaSoapConnection") MagdaConnection mockConnection) {
        this.mockConnection = mockConnection;
    }

    @PostMapping(value = {MAGDA_SOAP_02_00, "api/" + MAGDA_SOAP_02_00}, produces = {TEXT_XML_VALUE}, consumes = {APPLICATION_XML_VALUE, TEXT_XML_VALUE})
    public ResponseEntity<String> magdaSoap0200WebService(@RequestBody String request) throws MagdaConnectionException {
        return processMagdaMockRequest(request);
    }

    private ResponseEntity<String> processMagdaMockRequest(String request) throws MagdaConnectionException {
        //TODO: handle request parsing errors and return Magda Uitzondering error
        var requestDocument = MagdaDocument.fromString(request);

        var magdaResponse = mockConnection.sendDocument(requestDocument.getXml());
        if (magdaResponse != null) {
            return parseInputstream(MagdaDocument.fromDocument(magdaResponse));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // !!!NOTE: very much WIP!!!
    private String processTemplatingValues(String response) {
        MustacheFactory mf = new DefaultMustacheFactory();
        DateTime now = DateTime.now();
        Function<String, String> dateTemplateFunction = (text) -> processDateTemplate(text, now);
        Map<String, Object> scope = Map.of(
                "year", now.year(),
                "month", now.monthOfYear(),
                "day", now.dayOfMonth(),
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
     * - months (with "+1m" or "-5m" or any other number)
     * - days (with "+1d" or "-5d" or any other number)
     * - start of the current quarter (with "sq")
     *
     * Each modifier should be separated by a ',' and they are executed from left to right.
     *
     * Example: "-1y,sq,-2m,-3d" would do the following steps:
     * - take 1 year before today
     * - take the first day of the quarter
     * - take 2 months before that
     * - take 3 days before that
     */
    private String processDateTemplate(String dateTemplate, DateTime dateTime) {
        List<String> parts = Arrays.stream(dateTemplate.split(",")).toList();
        DateTime newTime = dateTime.toDateTime();
        for (String part : parts){
            Pattern dateTimeDiffPattern = Pattern.compile("([-+][0-9])?([a-z]+)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = dateTimeDiffPattern.matcher(part);
            if (!matcher.find())
                continue;
            String group = matcher.group(2).toLowerCase(Locale.ROOT);
            if (group.endsWith("y")) {
                var years = Integer.parseInt(matcher.group(1));
                newTime.plusYears(years);
            }
            if (group.endsWith("m")) {
                var months = Integer.parseInt(matcher.group(1));
                newTime.plusMonths(months);
            }
            if (group.endsWith("d")) {
                var days = Integer.parseInt(matcher.group(1));
                newTime.plusDays(days);
            }
            if (group.endsWith("sq")) {
                // determine start of the quarter
                int month = Math.floorDivExact(dateTime.getMonthOfYear()-1, 4) * 4 + 1;
                int day = 1;
                newTime = newTime.monthOfYear().setCopy(month);
                newTime = newTime.dayOfMonth().setCopy(day);
            }
            // TODO: add more options, first day of quarter, days, months, weeks
        }
        log.debug("Parsing date template {} on {} returns date {}", dateTemplate, dateTime, newTime);
        return newTime.toString("yyyy-MM-dd");
    }

    private ResponseEntity<String> parseInputstream(MagdaDocument magdaDocument) {
        if (magdaDocument != null) {
            String string = magdaDocument.toString();
            String templatedString = processTemplatingValues(string);
            return ResponseEntity.ok().contentType(TEXT_XML).body(templatedString);
        } else {
            log.error("Could not find XML");

            // TODO: maak en return MAGDA Uitzondering antwoord
            return ResponseEntity.notFound().build();
        }
    }
}
