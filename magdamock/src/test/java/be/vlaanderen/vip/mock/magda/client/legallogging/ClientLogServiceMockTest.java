package be.vlaanderen.vip.mock.magda.client.legallogging;

import be.vlaanderen.vip.magda.legallogging.model.FailedLoggedRequest;
import be.vlaanderen.vip.magda.legallogging.model.MagdaLoggedRequest;
import be.vlaanderen.vip.magda.legallogging.model.UnansweredLoggedRequest;
import be.vlaanderen.vip.magda.legallogging.model.SucceededLoggedRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ClientLogServiceMockTest {

	private ClientLogServiceMock service;
	
	@BeforeEach
	void prepare() {
		service = new ClientLogServiceMock();
	}
	
	@Nested
	class AssertConsistent {
		
		@Test
		void consistentWhenEmpty() {
			// No exceptions expected
			service.assertConsistent();
		}
		
		@Test
		void failsWhenRequestWithoutAnswer() {
			var request = generateRequest();
			service.logMagdaRequest(request);

			assertThrows(IllegalStateException.class, service::assertConsistent);
		}
		
		@Test
		void failsWhenRequestWithSuccessAndFailedAnswer() {
			var request = generateRequest();
			service.logMagdaRequest(request);
			service.logSucceededRequest(successRequest(request));
			service.logFailedRequest(failedRequest(request));
			service.logUnansweredRequest(unansweredRequest(request));

			assertThrows(IllegalStateException.class, service::assertConsistent);
		}
		
		@Test
		void consistentWhenRequestHasSingleResponse() {
			var request = generateRequest();
			service.logMagdaRequest(request);
			service.logSucceededRequest(successRequest(request));

			// No exceptions expected
			service.assertConsistent();
		}
		
		@Test
		void failsWhenDuplicateAnswers() {
			var request = generateRequest();
			service.logMagdaRequest(request);
			service.logSucceededRequest(successRequest(request));
			service.logSucceededRequest(successRequest(request));

			assertThrows(IllegalStateException.class, service::assertConsistent);
		}
		
		@Test
		void failsWhenDuplicateUnanswered() {
			var request = generateRequest();
			service.logMagdaRequest(request);
			service.logUnansweredRequest(unansweredRequest(request));
			service.logUnansweredRequest(unansweredRequest(request));

			assertThrows(IllegalStateException.class, service::assertConsistent);
		}
		
		@Test
		void failsWhenRequestHasSameLocalAsTransactionId() {
			var uuid = UUID.randomUUID();
			var invalidRequest = new MagdaLoggedRequest("test-insz", null, uuid, uuid, null, null, null);
			
			service.logMagdaRequest(invalidRequest);
			service.logSucceededRequest(successRequest(invalidRequest));

			assertThrows(IllegalStateException.class, service::assertConsistent);
		}
	}
	
	@Nested
	class AssertAllRequestsFor {

		@Test
		void successWhenAllForSameInsz() {
			var request = generateRequest();
			var insz = request.getInsz();
			
			service.logMagdaRequest(request);
			service.logSucceededRequest(successRequest(request));

			service.assertAlleVragenEnAntwoordenVoor(insz);
		}
	}
	
	private MagdaLoggedRequest generateRequest() {
		return new MagdaLoggedRequest("test-insz", null, UUID.randomUUID(), UUID.randomUUID(), null, null, null);
	}
	
	private SucceededLoggedRequest successRequest(MagdaLoggedRequest req) {
		return new SucceededLoggedRequest(req.getInsz(), req.getOverWie(), req.getTransactieID(), req.getLocalTransactieID(),
				Duration.ofSeconds(1), req.getDienst(), req.getDienstVersie(), req.getRegistratie());
	}
	
	private FailedLoggedRequest failedRequest(MagdaLoggedRequest req) {
		return new FailedLoggedRequest(req.getInsz(), req.getTransactieID(), req.getLocalTransactieID(),
				Duration.ofSeconds(1), Collections.emptyList(), req.getDienst(), req.getDienstVersie(), req.getRegistratie());
	}
	
	private UnansweredLoggedRequest unansweredRequest(MagdaLoggedRequest req) {
		return new UnansweredLoggedRequest(req.getInsz(), null, req.getTransactieID(), req.getLocalTransactieID(),
				req.getDienst(), req.getDienstVersie(), req.getRegistratie());
	}
}
