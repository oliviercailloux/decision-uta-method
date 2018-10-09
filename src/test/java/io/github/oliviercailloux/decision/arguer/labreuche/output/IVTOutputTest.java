package io.github.oliviercailloux.decision.arguer.labreuche.output;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import com.google.common.graph.MutableGraph;

import io.github.oliviercailloux.decision.arguer.AlternativesComparison;
import io.github.oliviercailloux.decision.arguer.AlternativesComparisonLabreucheBuilder;
import io.github.oliviercailloux.decision.arguer.labreuche.LabreucheModel;
import io.github.oliviercailloux.decision.model.Criterion;
import io.github.oliviercailloux.decision.model.EvaluatedAlternative;
import io.github.oliviercailloux.decision.model.ProblemGenerator;

class IVTOutputTest {

	@Test
	void testSmaller() {
		final AlternativesComparisonLabreucheBuilder builder = new AlternativesComparisonLabreucheBuilder();

		builder.setY(ImmutableList.of(0.20, 0.60));
		builder.setX(ImmutableList.of(0.54, 0.67));
		builder.setW(ImmutableList.of(0.54, 0.67));

		final AlternativesComparison<LabreucheModel> altsComp = builder.build();

		Iterator<Criterion> critIt = altsComp.getCriteria().iterator();
		Criterion c1 = critIt.next();
		Criterion c2 = critIt.next();

		MutableGraph<Criterion> graph = GraphBuilder.directed().build();
		graph.putEdge(c1, c2);

		IVTOutput output = new IVTOutput(altsComp, ImmutableGraph.copyOf(graph));

		assertTrue(output.isJustSmaller(0.74, 0.75));
		assertFalse(output.isJustSmaller(0.4, 0.75));
		assertFalse(output.isJustSmaller(0.55, 0.5));
		assertFalse(output.isJustSmaller(0.9, 0.1));

		assertFalse(output.isMuchSmaller(0.7, 0.75));
		assertTrue(output.isMuchSmaller(0.5, 0.75));
		assertFalse(output.isMuchSmaller(0.9, 0.75));
		assertFalse(output.isMuchSmaller(0.9, 0.2));
	}

	public AlternativesComparison<LabreucheModel> newAlternativesComparison() {
		ProblemGenerator gen = new ProblemGenerator();
		gen.generateCriteria(2);
		gen.generateAlternatives(2);
		List<EvaluatedAlternative> alternatives = gen.getAlternatives();
		EvaluatedAlternative x = alternatives.get(0);
		EvaluatedAlternative y = alternatives.get(1);
		List<Criterion> criteria = gen.getCriteria();
		ImmutableMap<Criterion, Double> weights = genEqualWeights(criteria);
		LabreucheModel lm = new LabreucheModel(weights);
		AlternativesComparison<LabreucheModel> altsComp = new AlternativesComparison<>(x, y, lm);
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
