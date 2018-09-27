package io.github.oliviercailloux.decision.arguer.labreuche.output;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import com.google.common.graph.MutableGraph;

import io.github.oliviercailloux.decision.arguer.AlternativesComparison;
import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;
import io.github.oliviercailloux.uta_calculator.model.ProblemGenerator;

class IVTOutputTest {

	@Test
	void testSmaller() {
		AlternativesComparison altsComp = newAlternativesComparison();
		Iterator<Criterion> critIt = altsComp.getCriteria().iterator();
		Criterion c1 = critIt.next();
		Criterion c2 = critIt.next();

		MutableGraph<Criterion> graph = GraphBuilder.directed().build();
		graph.putEdge(c1, c2);

		IVTOutput output = new IVTOutput(altsComp, ImmutableGraph.copyOf(graph), 0.1);

		assertTrue(output.isJustSmaller(0.7, 0.75));
		assertFalse(output.isJustSmaller(0.4, 0.75));
		assertFalse(output.isJustSmaller(0.55, 0.5));
		assertFalse(output.isJustSmaller(0.9, 0.1));

		assertFalse(output.isMuchSmaller(0.7, 0.75));
		assertTrue(output.isMuchSmaller(0.5, 0.75));
		assertFalse(output.isMuchSmaller(0.9, 0.75));
		assertFalse(output.isMuchSmaller(0.9, 0.2));
	}

	public AlternativesComparison newAlternativesComparison() {
		ProblemGenerator gen = new ProblemGenerator();
		gen.generateCriteria(2, 0, 10, 2);
		gen.generateAlternatives(2);
		List<Alternative> alternatives = gen.getAlternatives();
		Alternative x = alternatives.get(0);
		Alternative y = alternatives.get(1);
		List<Criterion> criteria = gen.getCriteria();
		ImmutableMap<Criterion, Double> weights = genEqualWeights(criteria);
		AlternativesComparison altsComp = new AlternativesComparison(x, y, weights,"labreuche");
		return altsComp;
	}

	public ImmutableMap<Criterion, Double> genEqualWeights(Collection<Criterion> criteria) {
		Builder<Criterion, Double> builder = ImmutableMap.builder();
		double weight = 1.0 / criteria.size();
		for (Criterion criterion : criteria) {
			builder.put(criterion, weight);
		}
		return builder.build();
	}

}
