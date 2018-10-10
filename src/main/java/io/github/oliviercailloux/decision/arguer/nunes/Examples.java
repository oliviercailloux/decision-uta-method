package io.github.oliviercailloux.decision.arguer.nunes;

import java.util.LinkedHashSet;
import java.util.Set;

import com.google.common.collect.ImmutableList;

import io.github.oliviercailloux.decision.arguer.AlternativesComparison;
import io.github.oliviercailloux.decision.arguer.AlternativesComparisonNunesBuilder;
import io.github.oliviercailloux.decision.arguer.nunes.output.CriticalAttributeOutput;
import io.github.oliviercailloux.decision.arguer.nunes.output.DominationOutput;
import io.github.oliviercailloux.decision.model.Criterion;
import io.github.oliviercailloux.decision.model.EvaluatedAlternative;

public class Examples {

	private Examples() {
		throw new IllegalStateException("Nunes Examples class");
	}

	public static NunesComputer getExampleCRITICAL() {
		final AlternativesComparisonNunesBuilder builder = new AlternativesComparisonNunesBuilder();

		builder.setX(ImmutableList.of(0.54, 0.67, 0.89, 0.90));
		builder.setY(ImmutableList.of(0.54, 0.67, 0.89, 0.76));
		builder.setW(ImmutableList.of(0.54, 0.67, 0.89, 0.76));

		final AlternativesComparison<NunesModel> altsComp = builder.build();

		Set<EvaluatedAlternative> set = new LinkedHashSet<>();
		set.add(altsComp.getX());
		set.add(altsComp.getY());

		return new NunesComputer(altsComp, new LinkedHashSet<Constraint>(), set);
	}

	public static NunesComputer getExampleDOMINATION() {
		final AlternativesComparisonNunesBuilder builder = new AlternativesComparisonNunesBuilder();

		builder.setX(ImmutableList.of(0.60, 0.69, 0.95, 0.91));
		builder.setY(ImmutableList.of(0.54, 0.67, 0.89, 0.76));
		builder.setW(ImmutableList.of(0.54, 0.67, 0.89, 0.76));

		final AlternativesComparison<NunesModel> altsComp = builder.build();

		Set<EvaluatedAlternative> set = new LinkedHashSet<>();
		set.add(altsComp.getX());
		set.add(altsComp.getY());

		return new NunesComputer(altsComp, new LinkedHashSet<Constraint>(), set);
	}

	public static CriticalAttributeOutput getExampleCRITICALOutput() {
		NunesComputer nm = getExampleCRITICAL();

		Criterion critical = new Criterion(4, "c4");

		return new CriticalAttributeOutput(nm.getAlternativesComparison(), critical);
	}

	public static DominationOutput getExampleDOMINATIONOutput() {
		NunesComputer nm = getExampleDOMINATION();

		return new DominationOutput(nm.getAlternativesComparison());
	}

}
