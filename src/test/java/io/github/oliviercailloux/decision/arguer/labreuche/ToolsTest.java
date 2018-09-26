package io.github.oliviercailloux.decision.arguer.labreuche;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;

import io.github.oliviercailloux.decision.arguer.AlternativesComparison;
import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;
import io.github.oliviercailloux.uta_calculator.model.ProblemGenerator;

public class ToolsTest {

	@SuppressWarnings("unchecked")
	@Test
	void testIncludeDiscri() {
		AlternativesComparison altsComp = newAlternativesComparison();

		List<List<Criterion>> setA = new ArrayList<>();
		List<List<Criterion>> setB = null;

		List<List<Criterion>> emptySet = new ArrayList<>();

		setA.add(toList(altsComp.getCriteria()));

		assertTrue(LabreucheTools.includeDiscri(setA, setB, altsComp.getWeight(), altsComp.getDelta()));
		assertFalse(LabreucheTools.includeDiscri(setB, setA, altsComp.getWeight(), altsComp.getDelta()));
		assertFalse(LabreucheTools.includeDiscri(setB, setB, altsComp.getWeight(), altsComp.getDelta()));

		assertTrue(LabreucheTools.includeDiscri(emptySet, setA, altsComp.getWeight(), altsComp.getDelta()));
		assertFalse(LabreucheTools.includeDiscri(setA, emptySet, altsComp.getWeight(), altsComp.getDelta()));
		assertFalse(LabreucheTools.includeDiscri(emptySet, emptySet, altsComp.getWeight(), altsComp.getDelta()));
	}

	@Test
	void testIsCapEmpty() {
		AlternativesComparison alts = newAlternativesComparison();

		Iterator<Criterion> critIt = alts.getCriteria().iterator();

		Criterion c1 = critIt.next();
		Criterion c2 = critIt.next();
		Criterion c3 = critIt.next();
		Criterion c4 = critIt.next();
		Criterion c5 = critIt.next();
		Criterion c6 = critIt.next();

		List<Criterion> list1 = new ArrayList<>();
		List<Criterion> list2 = new ArrayList<>();
		List<List<Criterion>> list3 = new ArrayList<>();
		List<Criterion> list4 = new ArrayList<>();
		List<Criterion> list5 = new ArrayList<>();

		list1.add(c1);
		list1.add(c4);
		list2.add(c2);
		list2.add(c3);
		list3.add(list1);
		list3.add(list2);
		list4.add(c5);
		list4.add(c4);
		list5.add(c5);
		list5.add(c6);

		assertFalse(LabreucheTools.isCapEmpty(list3, list4));
		assertTrue(LabreucheTools.isCapEmpty(list3, list5));
	}

	private List<Criterion> toList(ImmutableSet<Criterion> criteria) {
		List<Criterion> l = new ArrayList<>();

		for (Criterion c : criteria)
			l.add(c);

		return l;
	}

	public AlternativesComparison newAlternativesComparison() {
		ProblemGenerator gen = new ProblemGenerator();
		gen.generateCriteria(6, 0, 10, 2);
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
		double weight = 1.0 / criteria.size();
		for (Criterion criterion : criteria) {
			builder.put(criterion, weight);
		}
		return builder.build();
	}

}
