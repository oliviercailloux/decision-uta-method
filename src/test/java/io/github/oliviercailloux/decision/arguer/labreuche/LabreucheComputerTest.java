package io.github.oliviercailloux.decision.arguer.labreuche;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.oliviercailloux.decision.arguer.labreuche.output.ALLOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.Anchor;
import io.github.oliviercailloux.decision.arguer.labreuche.output.IVTOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.NOAOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.RMGAVGOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.RMGCOMPOutput;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class LabreucheComputerTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(LabreucheComputerTest.class);

	@Test
	public void testExampleAll() {
		LOGGER.info("Example All test");

		LabreucheComputer lm = Examples.getExampleAll();
		ALLOutput allExpected = Examples.getExampleAllOutput();

		assertEquals(allExpected, lm.getALLExplanation());
	}

	@Test
	public void testExampl5() {
		LOGGER.info("Example 5 test");

		LabreucheComputer lm = Examples.getExample5();
		NOAOutput noaExpected = Examples.getExample5Output();

		assertEquals(noaExpected, lm.getNOAExplanation());
	}

	@Test
	public void testExampl6() {
		LOGGER.info("Example 6 test");

		LabreucheComputer lm = Examples.getExample6();
		NOAOutput noaExpected = Examples.getExample6Output();

		assertEquals(noaExpected, lm.getNOAExplanation());
	}

	@Test
	public void testExample13() {
		LOGGER.info("Example 13 test");

		LabreucheComputer lm = Examples.getExample13();
		NOAOutput noaExpected = Examples.getExample13Output();

		assertEquals(noaExpected, lm.getNOAExplanation());
	}

	@Test
	public void testExampl14() {
		LOGGER.info("Example 14 test");

		LabreucheComputer lm = Examples.getExample14();
		NOAOutput noaExpected = Examples.getExample14Output();

		assertEquals(noaExpected, lm.getNOAExplanation());
	}

	@Test
	public void testExample18() {
		LOGGER.info("Example 18 test");

		LabreucheComputer lm = Examples.getExample18();
		RMGCOMPOutput avgExpected = Examples.getExample18Output();
		
		assertEquals(avgExpected, lm.getRMGCOMPExplanation());
	}

	@Test
	public void testExample17() {
		LOGGER.info("Example 17 test");

		LabreucheComputer lm = Examples.getExample17();
		RMGCOMPOutput compExpected = Examples.getExample17Output();

		assertEquals(compExpected, lm.getRMGCOMPExplanation());
	}

	@Test
	public void testExample9() {
		LOGGER.info("Example 9 test");

		LabreucheComputer lm = Examples.getExample9();
		IVTOutput ivtExpected = Examples.getExample9Output();

		assertEquals(ivtExpected, lm.getIVTExplanation());
	}

	@Test
	public void testExample15Permutation() {
		LOGGER.info("Example 15 test");

		LabreucheComputer lm = Examples.getExample15();

		List<List<Criterion>> permutationExpected = Examples.getExample15Permutation();

		assertTrue(lm.isApplicable(Anchor.IVT));
		assertEquals(permutationExpected, lm.getIVTPermutations());
	}

	@Test
	public void testExample16Permutation() {
		LOGGER.info("Example 16 test");

		LabreucheComputer lm = Examples.getExample16();

		List<List<Criterion>> permutationExpected = Examples.getExample16Permutation();

		assertTrue(lm.isApplicable(Anchor.IVT));
		assertEquals(permutationExpected, lm.getIVTPermutations());
	}

	//@Test
	public void testExample10Permutation() {
		LOGGER.info("Example 10 test");

		LabreucheComputer lm = Examples.getExample10();

		List<List<Criterion>> permutationExpected = Examples.getExample10Permutation();

		assertTrue(lm.isApplicable(Anchor.IVT));
		assertEquals(permutationExpected, lm.getIVTPermutations());
	}

}