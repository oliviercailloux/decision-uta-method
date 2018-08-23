package io.github.oliviercailloux.labreuche.model;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import io.github.oliviercailloux.decision.arguer.labreuche.LabreucheModel;
import io.github.oliviercailloux.decision.arguer.labreuche.Tools;
import io.github.oliviercailloux.decision.arguer.labreuche.output.Anchor;
import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;
import io.github.oliviercailloux.uta_calculator.view.MainBuyingNewCar;

public class LMTests {

	@Test
	public void labreucheModelTests() {

		MainLabreucheModel mlm = new MainLabreucheModel();

		// assertEquals("Is anchor ALL
		// applicable?",true,mlm.lm.isAnchorALLApplicable());

		assertEquals("Is anchor NOA applicable?", true, mlm.lm.isAnchorNOAApplicable());
	}
}
