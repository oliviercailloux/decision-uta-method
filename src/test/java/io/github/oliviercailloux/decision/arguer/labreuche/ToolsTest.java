package io.github.oliviercailloux.decision.arguer.labreuche;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;
import io.github.oliviercailloux.uta_calculator.model.ProblemGenerator;

public class ToolsTest {
	
	@SuppressWarnings("unchecked")
	@Test
	void testIncludeDiscri() {
		AlternativesComparison altsComp = newAlternativesComparison();
		
		List<List<Criterion>> setA = new ArrayList<>();
		List<List<Criterion>> setB = new ArrayList<>();

		setA.add((List<Criterion>) altsComp.getCriteria());
		
		assertTrue(Tools.includeDiscri(setA, setB, altsComp.getWeight(), altsComp.getDelta()));
		assertFalse(Tools.includeDiscri(setB, setA, altsComp.getWeight(), altsComp.getDelta()));
	}
	
	public AlternativesComparison newAlternativesComparison() {
		ProblemGenerator gen = new ProblemGenerator();
		gen.generateCriteria(2, 0, 10, 1);
		gen.generateAlternatives(2);
		List<Alternative> alternatives = gen.getAlternatives();
		Alternative x = alternatives.get(0);
		Alternative y = alternatives.get(1);
		List<Criterion> criteria = gen.getCriteria();
		ImmutableMap<Criterion, Double> weights = genEqualWeights(criteria);
		AlternativesComparison altsComp = new AlternativesComparison(x, y, weights);
		return altsComp;
	}

	public ImmutableMap<Criterion, Double> genEqualWeights(Collection<Criterion> criteria) {
		Builder<Criterion, Double> builder = ImmutableMap.builder();
		double weight = 1 / criteria.size();
		for (Criterion criterion : criteria) {
			builder.put(criterion, weight);
		}
		return builder.build();
	}

	
}

