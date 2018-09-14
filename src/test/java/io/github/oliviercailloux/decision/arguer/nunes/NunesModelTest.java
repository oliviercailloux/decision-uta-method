package io.github.oliviercailloux.decision.arguer.nunes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.oliviercailloux.decision.arguer.nunes.output.DominationOutput;
import io.github.oliviercailloux.decision.arguer.nunes.output.Pattern;

public class NunesModelTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(NunesModelTest.class);

	@Test
	public void testExampleCritical() {
		LOGGER.info("Example Critical test");

		NunesModel nm = Examples.getExampleCRITICAL();

		DominationOutput dom = nm.getDOMINATIONExplanation();
		DominationOutput domExpected = Examples.getExampleCRITICALOutput();

		assertTrue(nm.isApplicable(Pattern.DOMINATION));
		assertEquals(domExpected.getCritical(), dom.getCritical());
	}

	@Test
	public void testExampleDomination() {
		LOGGER.info("Example Domination test");

		NunesModel nm = Examples.getExampleDOMINATION();

		assertTrue(nm.isApplicable(Pattern.DOMINATION));
	}

}
