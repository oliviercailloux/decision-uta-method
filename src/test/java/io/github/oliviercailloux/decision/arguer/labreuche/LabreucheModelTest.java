package io.github.oliviercailloux.decision.arguer.labreuche;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

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

		MainLabreucheModel mlm = new MainLabreucheModel();

		ProblemGenerator pb = new ProblemGenerator();

		pb.generateCriteria(5, 0, 10, 1);
		pb.generateAlternatives(2);

		Alternative x = pb.getAlternatives().get(0);
		Alternative y = pb.getAlternatives().get(1);
		List<Criterion> criteria = pb.getCriteria();
		ImmutableMap<Criterion, Double> weights = computeWeights(criteria, 10);

		AlternativesComparison alts = new AlternativesComparison(x, y, weights); 
		
		LabreucheModel lm = new LabreucheModel(alts);
		System.out.println(" w = " + Tools.showVector(weights));
		System.out
				.println(x.getName() + " = " + Tools.showVector(x.getEvaluations()) + " : " + Tools.score(x, weights));
		System.out
				.println(y.getName() + " = " + Tools.showVector(y.getEvaluations()) + " : " + Tools.score(y, weights));

		lm.getExplanation();

		assertFalse(mlm.lm.isApplicable(Anchor.ALL));
		assertTrue(mlm.lm.isApplicable(Anchor.NOA));
		assertNotNull(lm.getExplanation());
		assertNotNull(lm.arguer());

		System.out.println(lm.arguer());
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