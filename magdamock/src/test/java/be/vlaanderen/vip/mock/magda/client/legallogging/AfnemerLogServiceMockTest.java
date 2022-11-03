package be.vlaanderen.vip.mock.magda.client.legallogging;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import be.vlaanderen.vip.magda.legallogging.model.GefaaldeAanvraag;
import be.vlaanderen.vip.magda.legallogging.model.GeslaagdeAanvraag;
import be.vlaanderen.vip.magda.legallogging.model.MagdaAanvraag;
import be.vlaanderen.vip.magda.legallogging.model.OnbeantwoordeAanvraag;

class AfnemerLogServiceMockTest {

	private AfnemerLogServiceMock service;
	
	@BeforeEach
	void prepare() {
		service = new AfnemerLogServiceMock();
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
			service.logAanvraag(request);

			assertThrows(IllegalStateException.class, service::assertConsistent);
		}
		
		@Test
		void failsWhenRequestWithSuccessAndFailedAnswer() {
			var request = generateRequest();
			service.logAanvraag(request);
			service.logGeslaagdeAanvraag(successRequest(request));
			service.logGefaaldeAanvraag(failedRequest(request));
			service.logOnbeantwoordeAanvraag(unansweredRequest(request));

			assertThrows(IllegalStateException.class, service::assertConsistent);
		}
	}
	
	private MagdaAanvraag generateRequest() {
		return new MagdaAanvraag("test-insz", null, UUID.randomUUID(), UUID.randomUUID(), null, null, null);
	}
	
	private GeslaagdeAanvraag successRequest(MagdaAanvraag req) {
		return new GeslaagdeAanvraag(req.getInsz(), req.getOverWie(), req.getTransactieID(), req.getLocalTransactieID(), 
				Duration.ofSeconds(1), req.getDienst(), req.getDienstVersie(), req.getRegistratie());
	}
	
	private GefaaldeAanvraag failedRequest(MagdaAanvraag req) {
		return new GefaaldeAanvraag(req.getInsz(), req.getTransactieID(), req.getLocalTransactieID(), 
				Duration.ofSeconds(1), Collections.emptyList(), req.getDienst(), req.getDienstVersie(), req.getRegistratie());
	}
	
	private OnbeantwoordeAanvraag unansweredRequest(MagdaAanvraag req) {
		return new OnbeantwoordeAanvraag(req.getInsz(), null, req.getTransactieID(), req.getLocalTransactieID(), 
				req.getDienst(), req.getDienstVersie(), req.getRegistratie());
	}
}
