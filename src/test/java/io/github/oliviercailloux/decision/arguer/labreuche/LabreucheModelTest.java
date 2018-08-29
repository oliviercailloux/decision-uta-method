package io.github.oliviercailloux.decision.arguer.labreuche;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import io.github.oliviercailloux.decision.arguer.labreuche.LabreucheModel;
import io.github.oliviercailloux.decision.arguer.labreuche.output.Anchor;
import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;
import io.github.oliviercailloux.uta_calculator.model.ProblemGenerator;
import io.github.oliviercailloux.uta_calculator.view.MainLabreucheModel;

public class LabreucheModelTest {

	@Test
	public void labreucheModelTests() {

		Logger log = LoggerFactory.getLogger(LabreucheModel.class);

		MainLabreucheModel mlm = new MainLabreucheModel();

		AlternativesComparison alts = mlm.lm.getAlternativesComparison();
		log.info(" w = " + Tools.showVector(alts.getWeight()));
		log.info(alts.getX().getName() + " = " + Tools.showVector(alts.getX().getEvaluations()) + " : " + Tools.score(alts.getX(),alts.getWeight()));
		log.info(alts.getY().getName() + " = " + Tools.showVector(alts.getY().getEvaluations()) + " : " + Tools.score(alts.getY(),alts.getWeight()));


		assertFalse(mlm.lm.isApplicable(Anchor.ALL));
		assertTrue(mlm.lm.isApplicable(Anchor.NOA));
		assertFalse(mlm.lm.isApplicable(Anchor.IVT));
		assertFalse(mlm.lm.isApplicable(Anchor.RMGAVG));
		assertFalse(mlm.lm.isApplicable(Anchor.RMGCOMP));
		assertNotNull(mlm.lm.getExplanation());
		assertNotNull(mlm.lm.arguer());

		log.info(mlm.lm.arguer());
	}
	
	public ImmutableMap<Criterion, Double> computeWeights(Collection<Criterion> criteria, int value) {
		Builder<Criterion, Double> builder = ImmutableMap.builder();
		double weight = value;
		for (Criterion criterion : criteria) {
			builder.put(criterion, weight);
		}
		return builder.build();
	}
}