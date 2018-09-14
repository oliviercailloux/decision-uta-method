package io.github.oliviercailloux.decision.arguer.labreuche;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.oliviercailloux.decision.arguer.labreuche.output.Anchor;
import io.github.oliviercailloux.decision.arguer.labreuche.output.IVTOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.NOAOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.RMGAVGOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.RMGCOMPOutput;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class LabreucheModelTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(LabreucheModelTest.class);

	@Test
	public void testExampleAll() {
		LOGGER.info("Example All test");

		LabreucheModel lm = Examples.getExampleAll();

		assertTrue(lm.isApplicable(Anchor.ALL));
	}

	@Test
	public void testExampl5() {
		LOGGER.info("Example 5 test");

		LabreucheModel lm = Examples.getExample5();

		NOAOutput noa = lm.getNOAExplanation();
		NOAOutput noaExpected = Examples.getExample5Output();

		assertTrue(lm.isApplicable(Anchor.NOA));
		assertEquals(noaExpected.getNoaCriteria(), noa.getNoaCriteria());
	}

	@Test
	public void testExampl6() {
		LOGGER.info("Example 6 test");

		LabreucheModel lm = Examples.getExample6();

		NOAOutput noa = lm.getNOAExplanation();
		NOAOutput noaExpected = Examples.getExample6Output();

		assertTrue(lm.isApplicable(Anchor.NOA));
		assertEquals(noaExpected.getNoaCriteria(), noa.getNoaCriteria());
	}

	@Test
	public void testExample13() {
		LOGGER.info("Example 13 test");

		LabreucheModel lm = Examples.getExample13();

		NOAOutput noa = lm.getNOAExplanation();
		NOAOutput noaExpected = Examples.getExample13Output();

		assertTrue(lm.isApplicable(Anchor.NOA));
		assertEquals(noaExpected.getNoaCriteria(), noa.getNoaCriteria());
	}

	@Test
	public void testExampl14() {
		LOGGER.info("Example 14 test");

		LabreucheModel lm = Examples.getExample14();

		NOAOutput noa = lm.getNOAExplanation();
		NOAOutput noaExpected = Examples.getExample14Output();

		assertTrue(lm.isApplicable(Anchor.NOA));
		assertEquals(noaExpected.getNoaCriteria(), noa.getNoaCriteria());
	}

	@Test
	public void testExample18() {
		LOGGER.info("Example 18 test");

		LabreucheModel lm = Examples.getExample18();

		RMGAVGOutput rmgavg = lm.getRMGAVGExplanation();

		assertTrue(lm.isApplicable(Anchor.RMGAVG));
		assertNotNull(rmgavg);
	}

	@Test
	public void testExample17() {
		LOGGER.info("Example 17 test");

		LabreucheModel lm = Examples.getExample17();

		RMGCOMPOutput rmgcomp = lm.getRMGCOMPExplanation();

		assertTrue(lm.isApplicable(Anchor.RMGCOMP));
		assertNotNull(rmgcomp);
	}

	@Test
	public void testExample9() {
		LOGGER.info("Example 9 test");

		LabreucheModel lm = Examples.getExample9();

		IVTOutput ivt = lm.getIVTExplanation();
		IVTOutput ivtExpected = Examples.getExample9Output();

		assertTrue(lm.isApplicable(Anchor.IVT));
		assertEquals(ivtExpected.getRStar(), ivt.getRStar());
	}

	@Test
	public void testExample15() {
		LOGGER.info("Example 15 test");

		LabreucheModel lm = Examples.getExample15();

		List<List<Criterion>> permutationExpected = Examples.getExample15Permutation();

		assertTrue(lm.isApplicable(Anchor.IVT));
		assertEquals(permutationExpected, lm.getIVTPermutation());
	}

	@Test
	public void testExample16Permutation() {
		LOGGER.info("Example 16 test");

		LabreucheModel lm = Examples.getExample16();

		List<List<Criterion>> permutationExpected = Examples.getExample16Permutation();

		assertTrue(lm.isApplicable(Anchor.IVT));
		assertEquals(permutationExpected, lm.getIVTPermutation());
	}

	// @Test
	public void testExample10Permutation() {
		LOGGER.info("Example 10 test");

		LabreucheModel lm = Examples.getExample10();

		List<List<Criterion>> permutationExpected = Examples.getExample10Permutation();

		assertTrue(lm.isApplicable(Anchor.IVT));
		assertEquals(permutationExpected, lm.getIVTPermutation());
	}

}