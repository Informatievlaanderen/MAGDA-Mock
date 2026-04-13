package be.vlaanderen.vip.magda.client.domain.geefdmfavoorwerknemer;

import be.vlaanderen.vip.magda.client.MagdaClientException;
import be.vlaanderen.vip.magda.client.MagdaDocument;
import be.vlaanderen.vip.magda.client.MagdaResponse;
import be.vlaanderen.vip.magda.client.MagdaResponseWrapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class MagdaResponseDmfaVoorWerknemerAdapterJaxbImplTest {

    private final MagdaResponseDmfaVoorWerknemerAdapterJaxbImpl adapter = new MagdaResponseDmfaVoorWerknemerAdapterJaxbImpl();

    @Nested
    class IsMoreInformationAvailable {

        @Test
        void whenDocumentContainsNoUitzonderingenSection_moreInformationIsNotAvailable() throws MagdaClientException {
            var attesten = adapter.adapt(constructResponse("""
                    <web:GeefDmfaVoorWerknemerResponse xmlns:web="http://magda.vlaanderen.be/werk/soap/geefdmfavoorwerknemer/v03_00">
                        <Repliek>
                            <Antwoorden>
                                <Antwoord>
                                    <Inhoud>
                                        <Attesten>
                                            <Attest>
                                                <Identificatie>
                                                    <Nummer>1234567890</Nummer>
                                                </Identificatie>
                                            </Attest>
                                        </Attesten>
                                    </Inhoud>
                                </Antwoord>
                            </Antwoorden>
                        </Repliek>
                    </web:GeefDmfaVoorWerknemerResponse>
                    """));

            assertFalse(attesten.isMoreInformationAvailable());
        }

        static Stream<Arguments> whenDocumentContainsUitzonderingen_moreInformationIsAvailableIfContains30040InformationFromMagda_params() {
            return Stream.of(
                    Arguments.of("INFORMATIE", "MAGDA", "30040", true),
                    Arguments.of("INFORMATIE", "MAGDA", "30041", false),
                    Arguments.of("INFORMATIE", "NEEDAMAGNIE", "30040", false),
                    Arguments.of("INFORMATIE", "NEEDAMAGNIE", "30041", false),
                    Arguments.of("WAARSCHUWING", "MAGDA", "30040", false),
                    Arguments.of("WAARSCHUWING", "MAGDA", "30041", false),
                    Arguments.of("WAARSCHUWING", "NEEDAMAGNIE", "30040", false),
                    Arguments.of("WAARSCHUWING", "NEEDAMAGNIE", "30041", false));
        }

        @ParameterizedTest
        @MethodSource("whenDocumentContainsUitzonderingen_moreInformationIsAvailableIfContains30040InformationFromMagda_params")
        void whenDocumentContainsUitzonderingen_moreInformationIsAvailableIfContains30040InformationFromMagda(String type, String oorsprong, String identificatie, boolean isMoreInformationAvailable) throws MagdaClientException {
            var attesten = adapter.adapt(constructResponse("""
                    <web:GeefDmfaVoorWerknemerResponse xmlns:web="http://magda.vlaanderen.be/werk/soap/geefdmfavoorwerknemer/v03_00">
                        <Repliek>
                            <Antwoorden>
                                <Antwoord>
                                    <Inhoud>
                                        <Attesten>
                                            <Attest>
                                                <Identificatie>
                                                    <Nummer>1234567890</Nummer>
                                                </Identificatie>
                                            </Attest>
                                        </Attesten>
                                    </Inhoud>
                                    <Uitzonderingen>
                                        <Uitzondering>
                                            <Identificatie>%s</Identificatie>
                                            <Oorsprong>%s</Oorsprong>
                                            <Type>%s</Type>
                                            <Tijdstip>
                                                <Datum>2023-12-12</Datum>
                                                <Tijd>15:11:27.938</Tijd>
                                            </Tijdstip>
                                            <Diagnose>foo</Diagnose>
                                        </Uitzondering>
                                    </Uitzonderingen>
                                </Antwoord>
                            </Antwoorden>
                        </Repliek>
                    </web:GeefDmfaVoorWerknemerResponse>
                    """.formatted(identificatie, oorsprong, type)));

            assertEquals(isMoreInformationAvailable, attesten.isMoreInformationAvailable());
        }
    }

    private MagdaResponseWrapper constructResponse(String content) {
        return new MagdaResponseWrapper(new MagdaResponse(
                null,
                null,
                null,
                null,
                null,
                MagdaDocument.fromString(content),
                true,
                null));
    }
}