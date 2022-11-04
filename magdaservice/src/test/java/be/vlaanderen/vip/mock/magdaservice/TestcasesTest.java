package be.vlaanderen.vip.mock.magdaservice;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Slf4j
@EnabledIf("mockServerIsRunning")
@SpringBootTest
public class TestcasesTest extends MockServerTest {
    @Test
    void callsPersoonTestcases() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
        var request = new HttpEntity<String>(null, headers);

        var restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:8080/api/v1/testcases/persoon", HttpMethod.GET, request, String.class);
        System.out.println(response.getStatusCode());
        System.out.println(response.getBody());
    }

    @Test
    void callsOndernemingTestcases() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));
        var request = new HttpEntity<String>(null, headers);

        var restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange("http://localhost:8080/api/v1/testcases/onderneming", HttpMethod.GET, request, String.class);
        System.out.println(response.getStatusCode());
        System.out.println(response.getBody());
    }
}
