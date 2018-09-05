package io.github.oliviercailloux.decision.arguer.labreuche;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.oliviercailloux.decision.arguer.labreuche.output.Anchor;
import io.github.oliviercailloux.uta_calculator.view.MainLabreucheModel;

public class LabreucheModelTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(LabreucheModel.class);

	@Test
	public void labreucheModelNOATest() {
		MainLabreucheModel mlm5 = new MainLabreucheModel(5);
		MainLabreucheModel mlm6 = new MainLabreucheModel(6);
		MainLabreucheModel mlm13 = new MainLabreucheModel(13);
		MainLabreucheModel mlm14 = new MainLabreucheModel(14);

		LOGGER.info("anchor ALL applicable on NOA examples?");

		assertFalse(mlm5.lm.isApplicable(Anchor.ALL));
		assertFalse(mlm6.lm.isApplicable(Anchor.ALL));
		assertFalse(mlm13.lm.isApplicable(Anchor.ALL));
		assertFalse(mlm14.lm.isApplicable(Anchor.ALL));

		LOGGER.info("anchor NOA applicable on NOA examples?");

		assertTrue(mlm5.lm.isApplicable(Anchor.NOA));
		assertTrue(mlm6.lm.isApplicable(Anchor.NOA));
		assertTrue(mlm13.lm.isApplicable(Anchor.NOA));
		assertTrue(mlm14.lm.isApplicable(Anchor.NOA));

		LOGGER.info("anchor IVT applicable on NOA examples?");

		assertFalse(mlm5.lm.isApplicable(Anchor.IVT));
		assertFalse(mlm6.lm.isApplicable(Anchor.IVT));
		assertFalse(mlm13.lm.isApplicable(Anchor.IVT));
		assertFalse(mlm14.lm.isApplicable(Anchor.IVT));

		LOGGER.info("anchor RMGAVG applicable on NOA examples?");

		assertFalse(mlm5.lm.isApplicable(Anchor.RMGAVG));
		assertFalse(mlm6.lm.isApplicable(Anchor.RMGAVG));
		assertFalse(mlm13.lm.isApplicable(Anchor.RMGAVG));
		assertFalse(mlm14.lm.isApplicable(Anchor.RMGAVG));

		LOGGER.info("anchor RMGCOMP applicable on NOA examples?");

		assertFalse(mlm5.lm.isApplicable(Anchor.RMGCOMP));
		assertFalse(mlm6.lm.isApplicable(Anchor.RMGCOMP));
		assertFalse(mlm13.lm.isApplicable(Anchor.RMGCOMP));
		assertFalse(mlm14.lm.isApplicable(Anchor.RMGCOMP));

		LOGGER.info("Checking if getExplenation() return not null");

		assertNotNull(mlm5.lm.getExplanation());
		assertNotNull(mlm6.lm.getExplanation());
		assertNotNull(mlm13.lm.getExplanation());
		assertNotNull(mlm14.lm.getExplanation());

		LOGGER.info("cheking if arguer() return not null");

		assertNotNull(mlm5.lm.arguer());
		assertNotNull(mlm6.lm.arguer());
		assertNotNull(mlm13.lm.arguer());
		assertNotNull(mlm14.lm.arguer());
	}

	@Test
	public void labreucheModelRMGAVGTest() {
		MainLabreucheModel mlm = new MainLabreucheModel(18);

		LOGGER.info("anchor all anchors applicable on RMGAVG examples?");

		assertFalse(mlm.lm.isApplicable(Anchor.ALL));
		assertFalse(mlm.lm.isApplicable(Anchor.NOA));
		assertFalse(mlm.lm.isApplicable(Anchor.IVT));
		assertTrue(mlm.lm.isApplicable(Anchor.RMGAVG));
		assertFalse(mlm.lm.isApplicable(Anchor.RMGCOMP));
		assertNotNull(mlm.lm.getExplanation());
		assertNotNull(mlm.lm.arguer());
	}

	@Test
	public void labreucheModelRMGCOMPTest() {
		MainLabreucheModel mlm = new MainLabreucheModel(17);

		LOGGER.info("anchor all anchors applicable on RMGCOMP examples?");

		assertFalse(mlm.lm.isApplicable(Anchor.ALL));
		assertFalse(mlm.lm.isApplicable(Anchor.NOA));
		assertFalse(mlm.lm.isApplicable(Anchor.IVT));
		assertFalse(mlm.lm.isApplicable(Anchor.RMGAVG));
		assertTrue(mlm.lm.isApplicable(Anchor.RMGCOMP));
		assertNotNull(mlm.lm.getExplanation());
		assertNotNull(mlm.lm.arguer());
	}

	@Test
	public void labreucheModelIVTTest() {
		MainLabreucheModel mlm9 = new MainLabreucheModel(9);
		// MainLabreucheModel mlm10 = new MainLabreucheModel(10);
		MainLabreucheModel mlm15 = new MainLabreucheModel(15);
		MainLabreucheModel mlm16 = new MainLabreucheModel(16);

		LOGGER.info("anchor ALL applicable on IVT examples?");

		assertFalse(mlm9.lm.isApplicable(Anchor.ALL));
		// assertFalse(mlm10.lm.isApplicable(Anchor.ALL));
		assertFalse(mlm15.lm.isApplicable(Anchor.ALL));
		assertFalse(mlm16.lm.isApplicable(Anchor.ALL));

		LOGGER.info("anchor NOA applicable on IVT examples?");

		assertFalse(mlm9.lm.isApplicable(Anchor.NOA));
		// assertFalse(mlm10.lm.isApplicable(Anchor.NOA));
		assertFalse(mlm15.lm.isApplicable(Anchor.NOA));
		assertFalse(mlm16.lm.isApplicable(Anchor.NOA));

		LOGGER.info("anchor IVT applicable on IVT examples?");

		assertTrue(mlm9.lm.isApplicable(Anchor.IVT));
		// assertTrue(mlm10.lm.isApplicable(Anchor.IVT));
		assertTrue(mlm15.lm.isApplicable(Anchor.IVT));
		assertTrue(mlm16.lm.isApplicable(Anchor.IVT));

		LOGGER.info("anchor RMGAVG applicable on IVT examples?");

		assertFalse(mlm9.lm.isApplicable(Anchor.RMGAVG));
		// assertFalse(mlm10.lm.isApplicable(Anchor.RMGAVG));
		assertFalse(mlm15.lm.isApplicable(Anchor.RMGAVG));
		assertFalse(mlm16.lm.isApplicable(Anchor.RMGAVG));

		LOGGER.info("anchor RMGCOMP applicable on IVT examples?");

		assertFalse(mlm9.lm.isApplicable(Anchor.RMGCOMP));
		// assertFalse(mlm10.lm.isApplicable(Anchor.RMGCOMP));
		assertFalse(mlm15.lm.isApplicable(Anchor.RMGCOMP));
		assertFalse(mlm16.lm.isApplicable(Anchor.RMGCOMP));

		LOGGER.info("Checking if getExplenation() return not null");

		assertNotNull(mlm9.lm.getExplanation());
		// assertNotNull(mlm10.lm.getExplanation());
		assertNotNull(mlm15.lm.getExplanation());
		assertNotNull(mlm16.lm.getExplanation());

		LOGGER.info("Checking if aruger() return not null");

		assertNotNull(mlm9.lm.arguer());
		// assertNotNull(mlm10.lm.arguer());
		assertNotNull(mlm15.lm.arguer());
		assertNotNull(mlm16.lm.arguer());
	}

}