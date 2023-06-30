package be.vlaanderen.vip.mock.magda.client.legallogging;

import be.vlaanderen.vip.magda.client.diensten.subject.INSZNumber;
import be.vlaanderen.vip.magda.client.diensten.subject.SubjectIdentificationNumber;
import be.vlaanderen.vip.magda.legallogging.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
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
			var invalidRequest = new MagdaLoggedRequest(INSZNumber.of("test-insz"), uuid, uuid, null, null, null);
			
			service.logMagdaRequest(invalidRequest);
			service.logSucceededRequest(successRequest(invalidRequest));

			assertThrows(IllegalStateException.class, service::assertConsistent);
		}
	}
	
	@Nested
	class AssertAllRequestsFor {

		@Test
		void successWhenAllForSameSubjects() {
			var request = generateRequest();
			var subjects = request.getSubjects();
			
			service.logMagdaRequest(request);
			service.logSucceededRequest(successRequest(request));

			assertAlleVragenEnAntwoordenVoor(service, subjects);
		}

		private void assertAlleVragenEnAntwoordenVoor(ClientLogServiceMock serviceMock, List<SubjectIdentificationNumber> subjects) {
			serviceMock.getMagdaLoggedRequests().forEach(log -> assertLogFor(log, subjects));
			serviceMock.getSucceededLoggedRequests().forEach(log -> assertLogFor(log, subjects));
			serviceMock.getFailedLoggedRequests().forEach(log -> assertLogFor(log, subjects));
			serviceMock.getUnansweredLoggedRequests().forEach(log -> assertLogFor(log, subjects));
		}

		private void assertLogFor(LoggedRequest log, List<SubjectIdentificationNumber> subjects) {
			assert subjects.equals(log.getSubjects()) : String.format("Log for service %s doesn't contain the expected INSZ of the requesting party", log.getServiceName());
		}
	}
	
	private MagdaLoggedRequest generateRequest() {
		return new MagdaLoggedRequest(INSZNumber.of("test-insz"), UUID.randomUUID(), UUID.randomUUID(), null, null, null);
	}
	
	private SucceededLoggedRequest successRequest(MagdaLoggedRequest req) {
		return new SucceededLoggedRequest(req.getSubjects(), req.getTransactionID(), req.getLocalTransactionID(),
				Duration.ofSeconds(1), req.getServiceName(), req.getServiceVersion(), req.getRegistrationInfo());
	}
	
	private FailedLoggedRequest failedRequest(MagdaLoggedRequest req) {
		return new FailedLoggedRequest(req.getTransactionID(), req.getLocalTransactionID(),
				Duration.ofSeconds(1), Collections.emptyList(), req.getServiceName(), req.getServiceVersion(), req.getRegistrationInfo());
	}
	
	private UnansweredLoggedRequest unansweredRequest(MagdaLoggedRequest req) {
		return new UnansweredLoggedRequest(INSZNumber.of("test-insz"), req.getTransactionID(), req.getLocalTransactionID(),
				req.getServiceName(), req.getServiceVersion(), req.getRegistrationInfo());
	}
}