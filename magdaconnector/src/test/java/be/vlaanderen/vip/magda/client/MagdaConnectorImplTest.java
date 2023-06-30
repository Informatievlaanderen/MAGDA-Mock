package be.vlaanderen.vip.magda.client;

import be.vlaanderen.vip.magda.client.connection.MagdaConnection;
import be.vlaanderen.vip.magda.client.diensten.GeefBewijsRequest;
import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.diensten.subject.KBONumber;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheidService;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaRegistrationInfo;
import be.vlaanderen.vip.magda.exception.MagdaConnectionException;
import be.vlaanderen.vip.magda.exception.NoResponseException;
import be.vlaanderen.vip.magda.exception.UitzonderingenSectionInResponseException;
import be.vlaanderen.vip.magda.legallogging.model.UitzonderingEntry;
import be.vlaanderen.vip.magda.legallogging.service.ClientLogService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Document;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MagdaConnectorImplTest {

	@Nested
	class Send {

		@InjectMocks
		private MagdaConnectorImpl connector;
		@Mock
		private MagdaConnection connection;
		@Mock
		private MagdaHoedanigheidService identityService;
		@Mock
		private ClientLogService logService;

		@BeforeEach
		void prepare() {
			var identity = MagdaRegistrationInfo.builder()
					.identification("http://magda-test")
					.hoedanigheidscode("test-identity")
					.build();
			doReturn(identity).when(identityService).getDomeinService("default");
		}

		@Test
		void setsCorrelationIdOnRequest() {
			CorrelationId.clear();
			assertFalse(CorrelationId.isPresent());

			var req = GeefBewijsRequest.builder()
					.insz("test-insz")
					.build();
			mockReply(buildReplyDocument());

			assertNull(req.getCorrelationId());

			connector.send(req);

			assertNotNull(req.getCorrelationId());
			assertFalse(CorrelationId.isPresent());
		}

		@Nested
		class NoReply {

			@Test
			void sendDocumentReturnsNull() {
				var req = GeefBewijsRequest.builder()
						.insz("test-insz")
						.build();

				// if this occurs, we ought to treat it as a bug in the code
				assertThrows(IllegalStateException.class, () -> connector.send(req));
			}

			@Test
			void requestFails() throws MagdaConnectionException {
				when(connection.sendDocument(any())).thenThrow(new MagdaConnectionException("something went wrong"));

				var req = GeefBewijsRequest.builder()
						.insz("test-insz")
						.build();

				assertThrows(NoResponseException.class, () -> connector.send(req));
			}

			@Test
			void logsRequest() throws MagdaConnectionException {
				when(connection.sendDocument(any())).thenThrow(new MagdaConnectionException("something went wrong"));

				var req = GeefBewijsRequest.builder()
						.insz("test-insz")
						.build();

				assertThrows(NoResponseException.class, () -> connector.send(req));
				verify(logService).logMagdaRequest(any());
			}

			@Test
			void logsNoReply() throws MagdaConnectionException {
				when(connection.sendDocument(any())).thenThrow(new MagdaConnectionException("something went wrong"));

				var req = GeefBewijsRequest.builder()
						.insz("test-insz")
						.build();

				assertThrows(NoResponseException.class, () -> connector.send(req));
				verify(logService).logUnansweredRequest(any());
			}
		}

		@Nested
		class InvalidReply {

			@Mock
			private Document replyDocument;

			@BeforeEach
			void prepare() throws Exception {
				doReturn(replyDocument).when(connection).sendDocument(any());
			}

			@Test
			void requestFails() {
				var req = GeefBewijsRequest.builder()
						.insz("test-insz")
						.build();

				assertThrows(UitzonderingenSectionInResponseException.class, () -> connector.send(req));
			}

			@Test
			void logsRequest() {
				var req = GeefBewijsRequest.builder()
						.insz("test-insz")
						.build();

				assertThrows(UitzonderingenSectionInResponseException.class, () -> connector.send(req));
				verify(logService).logMagdaRequest(any());
			}
		}

		@Nested
		class ValidReply {

			@Test
			void returnsReply() {
				var req = GeefBewijsRequest.builder()
						.insz("test-insz")
						.build();
				mockReply(buildReplyDocument());

				var reply = connector.send(req);
				assertNotNull(reply);
			}

			@Test
			void logsRequest() {
				var req = GeefBewijsRequest.builder()
						.insz("test-insz")
						.build();
				mockReply(buildReplyDocument());

				connector.send(req);
				verify(logService).logMagdaRequest(any());
			}

			@Test
			void returnsGeneralExceptions() {
				var req = GeefBewijsRequest.builder()
						.insz("test-insz")
						.build();
				mockReply(buildReplyDocument(buildException("test-exception"), ""));

				var reply = connector.send(req);
				assertEquals(1, reply.getUitzonderingEntries().size());
			}

			@Test
			void returnsReplyExceptions() {
				var req = GeefBewijsRequest.builder()
						.insz("test-insz")
						.build();
				mockReply(buildReplyDocument("", buildException("test-exception")));

				var reply = connector.send(req);
				assertEquals(1, reply.getResponseUitzonderingEntries().size());
			}
		}

		@SneakyThrows
		private void mockReply(Document doc) {
			doReturn(doc).when(connection).sendDocument(any());
		}
	}

	@Nested
	class BuildResponse {

		@Test
		void buildsResponseFromMagdaRequestAndAccordingResponseDocument() {
			var request = GeefBewijsRequest.builder()
					.insz("123")
					.registration("456")
					.build();
			var responseDocument = MagdaDocument.fromDocument(buildReplyDocument());

			var response = constructConnectorImpl().buildResponse(request, responseDocument);

			assertEquals(request.getCorrelationId(), response.getCorrelationId());
			assertEquals(request.getRequestId(), response.getRequestId());
			assertTrue(response.getUitzonderingEntries().isEmpty());
			assertTrue(response.getResponseUitzonderingEntries().isEmpty());
			assertNotNull(response.getBody());
			assertNotNull(response.getDocument());
			assertTrue(response.isHasContents());
			assertEquals(Set.of(INSZNumber.of("123")), response.getSubjects());
		}

		@Test
		void buildsResponseThatContainsUitzonderingEntries() {
			var request = GeefBewijsRequest.builder()
					.insz("123")
					.registration("456")
					.build();
			var responseDocument = MagdaDocument.fromDocument(buildReplyDocument(
					"""
     				<Uitzondering>
     					<Identificatie>12345</Identificatie>
     				</Uitzondering>
					""",
					"""
				    <Uitzondering>
				    	<Identificatie>67890</Identificatie>
     				</Uitzondering>
					"""
			));

			var response = constructConnectorImpl().buildResponse(request, responseDocument);

			assertEquals(List.of(UitzonderingEntry.builder()
					.identification("12345")
					.annotatieFields(List.of())
					.build()), response.getUitzonderingEntries());
			assertEquals(List.of(UitzonderingEntry.builder()
					.identification("67890")
					.annotatieFields(List.of())
					.build()), response.getResponseUitzonderingEntries());
			assertNotNull(response.getBody());
			assertTrue(response.isHasContents());
		}

		@Test
		void responseContainsINSZAndKBONumbersGatheredFromRequestAndResponseXml() {
			var request = GeefBewijsRequest.builder()
					.insz("123")
					.registration("456")
					.build();
			var responseDocument = MagdaDocument.fromDocument(buildReplyDocument(
					"""
     				<INSZ>insz1</INSZ>
     				<Subsection>
     					<INSZ>insz2</INSZ>
     				</Subsection>
     				<Ondernemingsnummer>kbo1</Ondernemingsnummer>
     				<Ondernemingsnummer>kbo2</Ondernemingsnummer>
					""",
					"""
     				<Uitzondering>
     					<INSZ>insz3</INSZ>
     					<Ondernemingsnummer>kbo1</Ondernemingsnummer>
     				</Uitzondering>
					""",
					"""
			        <Uitzondering>
     					<Ondernemingsnummer>kbo3</Ondernemingsnummer>
     				</Uitzondering>
					"""
			));

			var response = constructConnectorImpl().buildResponse(request, responseDocument);

			assertEquals(Set.of(
							INSZNumber.of("123"),
							INSZNumber.of("insz1"),
							INSZNumber.of("insz2"),
							INSZNumber.of("insz3"),
							KBONumber.of("kbo1"),
							KBONumber.of("kbo2"),
							KBONumber.of("kbo3")),
					response.getSubjects());
		}

		private MagdaConnectorImpl constructConnectorImpl() {
			return new MagdaConnectorImpl(null, null, null);
		}
	}

	private Document buildReplyDocument(String content, String l1Exceptions, String l2Exceptions) {
		var replyXml = """
					<?xml version="1.0" encoding="UTF-8"?>
					<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:web="http://webservice.registreerinschrijvingdienst-02_01.repertorium-02_01.vip.vlaanderen.be">
					  <soapenv:Header/>
					  <soapenv:Body>
					    <geef:GeefBewijsResponse xmlns:geef="http://geefbewijs.bewijsraadplegingdienst.led.vlaanderen.be">
					      <Repliek>
					        <Antwoorden>
					          <Antwoord>
					            <Inhoud>
					              %s
					            </Inhoud>
					            <Uitzonderingen>
					              %s
					            </Uitzonderingen>
					          </Antwoord>
					        </Antwoorden>
					        <Uitzonderingen>
					          %s
					        </Uitzonderingen>
					      </Repliek>
					    </geef:GeefBewijsResponse>
					  </soapenv:Body>
					</soapenv:Envelope>
					""".formatted(content, l2Exceptions, l1Exceptions);
		return MagdaDocument.fromString(replyXml).getXml();
	}

	private Document buildReplyDocument(String l1Exceptions, String l2Exceptions) {
		return buildReplyDocument("", l1Exceptions, l2Exceptions);
	}

	private Document buildReplyDocument() {
		return buildReplyDocument("", "");
	}

	/**
	 * Creates an exception xml node, that can be inserted in a response
	 */
	private String buildException(String id) {
		return """
				<Uitzondering>
				  <Identificatie>%s</Identificatie>
				  <Type>FOUT</Type>
				</Uitzondering>
				""".formatted(id);
	}
}
