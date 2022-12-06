package be.vlaanderen.vip.magda.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.w3c.dom.Document;

import be.vlaanderen.vip.magda.client.connection.MagdaConnection;
import be.vlaanderen.vip.magda.client.diensten.GeefBewijsAanvraag;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheid;
import be.vlaanderen.vip.magda.client.domeinservice.MagdaHoedanigheidService;
import be.vlaanderen.vip.magda.client.endpoints.MagdaEndpoints;
import be.vlaanderen.vip.magda.exception.BackendUitzonderingenException;
import be.vlaanderen.vip.magda.exception.GeenAntwoordException;
import be.vlaanderen.vip.magda.legallogging.service.AfnemerLogService;
import lombok.SneakyThrows;

@ExtendWith(MockitoExtension.class)
class MagdaConnectorImplTest {

	@InjectMocks
	private MagdaConnectorImpl connector;
	@Mock
	private MagdaConnection connection;
	@Mock
	private MagdaEndpoints endpoints;
	@Mock
	private MagdaHoedanigheidService identityService;
	@Mock
	private AfnemerLogService logService;
	
	@BeforeEach
	void prepare() {
		doReturn("http://magda-test").when(endpoints).magdaUrl(any(MagdaServiceIdentificatie.class));
		
		var identity = new MagdaHoedanigheid("test-identity-name", "http://magda-test", "test-identity");
		doReturn(identity).when(identityService).getDomeinService("default");
	}
	
	@Nested
	class NoReply {
		
		@Test
		void requestFails() {
			var req = new GeefBewijsAanvraag("test-insz");
			var reqDoc = MagdaDocument.fromTemplate(req);
			
			assertThrows(GeenAntwoordException.class, () -> connector.send(req, reqDoc));
		}
		
		@Test
		void logsRequest() {
			var req = new GeefBewijsAanvraag("test-insz");
			var reqDoc = MagdaDocument.fromTemplate(req);
			
			assertThrows(GeenAntwoordException.class, () -> connector.send(req, reqDoc));
			verify(logService).logAanvraag(any());
		}

		@Test
		void logsNoReply() {
			var req = new GeefBewijsAanvraag("test-insz");
			var reqDoc = MagdaDocument.fromTemplate(req);
			
			assertThrows(GeenAntwoordException.class, () -> connector.send(req, reqDoc));
			verify(logService).logOnbeantwoordeAanvraag(any());
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
			var req = new GeefBewijsAanvraag("test-insz");
			var reqDoc = MagdaDocument.fromTemplate(req);
			
			assertThrows(BackendUitzonderingenException.class, () -> connector.send(req, reqDoc));
		}
		
		@Test
		void logsRequest() {
			var req = new GeefBewijsAanvraag("test-insz");
			var reqDoc = MagdaDocument.fromTemplate(req);
			
			assertThrows(BackendUitzonderingenException.class, () -> connector.send(req, reqDoc));
			verify(logService).logAanvraag(any());
		}
	}
	
	@Nested
	class ValidReply {
		
		@Test
		void returnsReply() {
			var req = new GeefBewijsAanvraag("test-insz");
			var reqDoc = MagdaDocument.fromTemplate(req);
			mockReply(buildReplyDocument());
			
			var reply = connector.send(req, reqDoc);
			assertNotNull(reply);
		}
		
		@Test
		void logsRequest() {
			var req = new GeefBewijsAanvraag("test-insz");
			var reqDoc = MagdaDocument.fromTemplate(req);
			mockReply(buildReplyDocument());
			
			connector.send(req, reqDoc);
			verify(logService).logAanvraag(any());
		}
		
		@Test
		void returnsGeneralExceptions() {
			var req = new GeefBewijsAanvraag("test-insz");
			var reqDoc = MagdaDocument.fromTemplate(req);
			mockReply(buildReplyDocument(buildException("test-exception"), ""));
			
			var reply = connector.send(req, reqDoc);
			assertEquals(1, reply.getUitzonderingen().size());
		}
		
		@Test
		void returnsReplyExceptions() {
			var req = new GeefBewijsAanvraag("test-insz");
			var reqDoc = MagdaDocument.fromTemplate(req);
			mockReply(buildReplyDocument("", buildException("test-exception")));
			
			var reply = connector.send(req, reqDoc);
			assertEquals(1, reply.getAntwoordUitzonderingen().size());
		}
		
		private Document buildReplyDocument(String l1Exceptions, String l2Exceptions) {
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
    				""".formatted(l2Exceptions, l1Exceptions);
			return MagdaDocument.fromString(replyXml).getXml();
		}
		
		private Document buildReplyDocument() {
			return buildReplyDocument("", "");
		}

		@SneakyThrows
		private void mockReply(Document doc) {
			doReturn(doc).when(connection).sendDocument(any());
		}
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
