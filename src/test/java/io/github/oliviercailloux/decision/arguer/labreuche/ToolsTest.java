package io.github.oliviercailloux.decision.arguer.labreuche;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;

import io.github.oliviercailloux.decision.arguer.AlternativesComparison;
import io.github.oliviercailloux.decision.arguer.AlternativesComparisonLabreucheBuilder;
import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;
import io.github.oliviercailloux.uta_calculator.model.ProblemGenerator;

public class ToolsTest {

	@SuppressWarnings("unchecked")
	@Test
	void testIncludeDiscri() {
		final AlternativesComparisonLabreucheBuilder builder = new AlternativesComparisonLabreucheBuilder();

		builder.setY(ImmutableList.of(0.20, 0.60));
		builder.setX(ImmutableList.of(0.54, 0.67));
		builder.setW(ImmutableList.of(0.54, 0.67));

		final AlternativesComparison<LabreucheModel> altsComp = builder.build();
		
		List<List<Criterion>> setA = new ArrayList<>();
		setA.add(toList(altsComp.getCriteria()));

		List<List<Criterion>> setB = null;
		List<List<Criterion>> emptySet = new ArrayList<>();


		assertTrue(LabreucheTools.includeDiscri(setA, setB, altsComp.getPreferenceModel().getWeights(),
				getDelta(altsComp)));
		assertFalse(LabreucheTools.includeDiscri(setB, setA, altsComp.getPreferenceModel().getWeights(),
				getDelta(altsComp)));
		assertFalse(LabreucheTools.includeDiscri(setB, setB, altsComp.getPreferenceModel().getWeights(),
				getDelta(altsComp)));

		assertTrue(LabreucheTools.includeDiscri(emptySet, setA, altsComp.getPreferenceModel().getWeights(),
				getDelta(altsComp)));
		assertFalse(LabreucheTools.includeDiscri(setA, emptySet, altsComp.getPreferenceModel().getWeights(),
				getDelta(altsComp)));
		assertFalse(LabreucheTools.includeDiscri(emptySet, emptySet, altsComp.getPreferenceModel().getWeights(),
				getDelta(altsComp)));
	}

	private Map<Criterion, Double> getDelta(AlternativesComparison<LabreucheModel> altsComp) {
		return altsComp.getPreferenceModel().getDelta(altsComp.getX(), altsComp.getY());
	}

	@Test
	void testIsCapEmpty() {
		final AlternativesComparisonLabreucheBuilder builder = new AlternativesComparisonLabreucheBuilder();

		builder.setY(ImmutableList.of(0.20, 0.60, 0.20, 0.60, 0.20, 0.60));
		builder.setX(ImmutableList.of(0.54, 0.67, 0.54, 0.67, 0.54, 0.67));
		builder.setW(ImmutableList.of(0.54, 0.67, 0.54, 0.67, 0.54, 0.67));

		final AlternativesComparison<LabreucheModel> altsComp = builder.build();
		
		Iterator<Criterion> critIt = altsComp.getCriteria().iterator();

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
}
