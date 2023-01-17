package be.vlaanderen.vip.magda.tester.tests;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
public class TestcasesTest extends MockServerTest {

    @BeforeAll
    @SneakyThrows
    void beforeAll() {
        assertServiceAvailable();
    }

    @BeforeEach
    @SneakyThrows
    void setup() {
        assertServiceAvailable();
    }

    @Test
    void callsPersoonTestcases() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
        var request = new HttpEntity<String>(null, headers);

        var restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(testerConfig.getServiceUrl() + "/api/v1/testcases/persoon", HttpMethod.GET, request, String.class);
        log.debug(response.getStatusCode().toString());
        log.debug(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void callsOndernemingTestcases() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
        var request = new HttpEntity<String>(null, headers);

        var restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(testerConfig.getServiceUrl() + "/api/v1/testcases/onderneming", HttpMethod.GET, request, String.class);
        log.debug(response.getStatusCode().toString());
        log.debug(response.getBody());

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
