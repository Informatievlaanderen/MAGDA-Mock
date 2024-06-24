package be.vlaanderen.vip.mock.magda.client;

import be.vlaanderen.vip.magda.client.diensten.GeefMultipleSociaalStatuutRequest;
import be.vlaanderen.vip.magda.client.diensten.SociaalStatuutRequestCriteria;
import be.vlaanderen.vip.mock.magda.client.legallogging.ClientLogServiceMock;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class GeefMultipleSociaalStatuutTest extends MockTestBase {
    private static final UUID REQUEST_ID = UUID.fromString("64fb1939-0ca7-432b-b7f4-3b53f7fc3789");

    @Test
    @SneakyThrows
    void geefMultipleSociaalStatuutGivesResponse_withRequestedSocialStatutesOnly() {
        final var requestInsz = "62691699717";
        final var socialStatutes = Set.of(
                SociaalStatuutRequestCriteria.builder()
                        .socialStatusName("LIVING_WAGES_PCSA_HELP")
                        .date(LocalDate.now())
                        .build(),
                SociaalStatuutRequestCriteria.builder()
                        .socialStatusName("PCSA_AND_BIM_HELP")
                        .date(LocalDate.now())
                        .build()
        );

        var request = GeefMultipleSociaalStatuutRequest.builder()
                .insz(requestInsz)
                .socialStatutes(socialStatutes)
                .build();

        var clientLogService = new ClientLogServiceMock();

        var connector = makeMagdaConnector(clientLogService);

        var antwoord = connector.send(request, REQUEST_ID);
        log.info("{}", antwoord.getDocument());

        assertThat(antwoord.isBodyFilledIn()).isTrue();
        assertThat(antwoord.isHasContents()).isTrue();
        assertThat(antwoord.getUitzonderingEntries()).isEmpty();
        assertThat(antwoord.getResponseUitzonderingEntries()).isEmpty();

        assertThat(clientLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfFailedLoggedRequests()).isZero();

        var doc = antwoord.getDocument();

        var referte = doc.getValue("//Antwoorden/Antwoord/Referte");
        assertThat(referte).isEqualTo(REQUEST_ID.toString());

        var inszNumber = doc.getValue("//Persoon/INSZ");
        assertThat(inszNumber).isEqualTo(requestInsz);

        var statusNames = doc.getValues("//SocialeStatuten/SociaalStatuut/Naam");
        assertThat(statusNames.size()).isEqualTo(2);

        assertThat(doc.getValue("//SociaalStatuut[Naam[text()='LIVING_WAGES_PCSA_HELP']]/Naam")).isEqualTo("LIVING_WAGES_PCSA_HELP");
        assertThat(doc.getValue("//SociaalStatuut[Naam[text()='LIVING_WAGES_PCSA_HELP']]/Resultaat/Code")).isEqualTo("1");

        assertThat(doc.getValue("//SociaalStatuut[Naam[text()='PCSA_AND_BIM_HELP']]/Naam")).isEqualTo("PCSA_AND_BIM_HELP");
        assertThat(doc.getValue("//SociaalStatuut[Naam[text()='PCSA_AND_BIM_HELP']]/Resultaat/Code")).isEqualTo("1");
    }

    @Test
    @SneakyThrows
    void geefSociaalStatuutGivesResponse_withRequestedSocialStatutesOnlyAndNotApplied() {
        final var requestInsz = "62691699717";
        final var socialStatutes = Set.of(
                SociaalStatuutRequestCriteria.builder()
                        .socialStatusName("SOCIAL_STATUTE_NAME_1")
                        .date(LocalDate.now())
                        .build(),
                SociaalStatuutRequestCriteria.builder()
                        .socialStatusName("SOCIAL_STATUTE_NAME_2")
                        .date(LocalDate.now())
                        .build()
        );

        var request = GeefMultipleSociaalStatuutRequest.builder()
                .insz(requestInsz)
                .socialStatutes(socialStatutes)
                .build();

        var clientLogService = new ClientLogServiceMock();

        var connector = makeMagdaConnector(clientLogService);

        var antwoord = connector.send(request, REQUEST_ID);
        log.info("{}", antwoord.getDocument());

        assertThat(antwoord.isBodyFilledIn()).isTrue();
        assertThat(antwoord.isHasContents()).isTrue();
        assertThat(antwoord.getUitzonderingEntries()).isEmpty();
        assertThat(antwoord.getResponseUitzonderingEntries()).isEmpty();

        assertThat(clientLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfFailedLoggedRequests()).isZero();

        var doc = antwoord.getDocument();

        var referte = doc.getValue("//Antwoorden/Antwoord/Referte");
        assertThat(referte).isEqualTo(REQUEST_ID.toString());

        var inszNumber = doc.getValue("//Persoon/INSZ");
        assertThat(inszNumber).isEqualTo(requestInsz);

        var statusNames = doc.getValues("//SocialeStatuten/SociaalStatuut/Naam");
        assertThat(statusNames).hasSize(2);

        assertThat(doc.getValue("//SociaalStatuut[Naam[text()='SOCIAL_STATUTE_NAME_1']]/Naam")).isEqualTo("SOCIAL_STATUTE_NAME_1");
        assertThat(doc.getValue("//SociaalStatuut[Naam[text()='SOCIAL_STATUTE_NAME_1']]/Resultaat/Code")).isEqualTo("0");

        assertThat(doc.getValue("//SociaalStatuut[Naam[text()='SOCIAL_STATUTE_NAME_2']]/Naam")).isEqualTo("SOCIAL_STATUTE_NAME_2");
        assertThat(doc.getValue("//SociaalStatuut[Naam[text()='SOCIAL_STATUTE_NAME_2']]/Resultaat/Code")).isEqualTo("0");
    }

    @Test
    @SneakyThrows
    void geefSociaalStatuutGivesResponse_withRequestedSocialStatutesOnlyAndNotFound() {
        final var requestInsz = "00010100173";
        final var socialStatutes = Set.of(
                SociaalStatuutRequestCriteria.builder()
                        .socialStatusName("SOCIAL_STATUTE_NAME_1")
                        .date(LocalDate.now())
                        .build(),
                SociaalStatuutRequestCriteria.builder()
                        .socialStatusName("SOCIAL_STATUTE_NAME_2")
                        .date(LocalDate.now())
                        .build()
        );

        var request = GeefMultipleSociaalStatuutRequest.builder()
                .insz(requestInsz)
                .socialStatutes(socialStatutes)
                .build();

        var clientLogService = new ClientLogServiceMock();

        var connector = makeMagdaConnector(clientLogService);

        var antwoord = connector.send(request, REQUEST_ID);
        log.info("{}", antwoord.getDocument());

        assertThat(antwoord.isBodyFilledIn()).isTrue();
        assertThat(antwoord.isHasContents()).isTrue();
        assertThat(antwoord.getUitzonderingEntries()).isEmpty();
        assertThat(antwoord.getResponseUitzonderingEntries()).isEmpty();

        assertThat(clientLogService.getNumberOfMagdaLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfSucceededLoggedRequests()).isEqualTo(1);
        assertThat(clientLogService.getNumberOfFailedLoggedRequests()).isZero();

        var doc = antwoord.getDocument();

        var referte = doc.getValue("//Antwoorden/Antwoord/Referte");
        assertThat(referte).isEqualTo(REQUEST_ID.toString());

        var inszNumber = doc.getValue("//Persoon/INSZ");
        assertThat(inszNumber).isEqualTo(requestInsz);

        var statusNames = doc.getValues("//SocialeStatuten/SociaalStatuut/Naam");
        assertThat(statusNames).hasSize(2);

        assertThat(doc.getValue("//SociaalStatuut[Naam[text()='SOCIAL_STATUTE_NAME_1']]/Naam")).isEqualTo("SOCIAL_STATUTE_NAME_1");
        assertThat(doc.getValue("//SociaalStatuut[Naam[text()='SOCIAL_STATUTE_NAME_1']]/Resultaat/Code")).isEqualTo("0");

        assertThat(doc.getValue("//SociaalStatuut[Naam[text()='SOCIAL_STATUTE_NAME_2']]/Naam")).isEqualTo("SOCIAL_STATUTE_NAME_2");
        assertThat(doc.getValue("//SociaalStatuut[Naam[text()='SOCIAL_STATUTE_NAME_2']]/Resultaat/Code")).isEqualTo("0");
    }
}
