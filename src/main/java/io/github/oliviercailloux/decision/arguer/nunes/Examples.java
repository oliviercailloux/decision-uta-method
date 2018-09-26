package io.github.oliviercailloux.decision.arguer.nunes;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import com.google.common.collect.ImmutableList;

import io.github.oliviercailloux.decision.arguer.AlternativesComparison;
import io.github.oliviercailloux.decision.arguer.AlternativesComparisonBuilder;
import io.github.oliviercailloux.decision.arguer.nunes.output.DominationOutput;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class Examples {

	public static NunesModel getExampleCRITICAL() {
		final AlternativesComparisonBuilder builder = new AlternativesComparisonBuilder();

		builder.setX(ImmutableList.of(0.54, 0.67, 0.89, 0.90));
		builder.setY(ImmutableList.of(0.54, 0.67, 0.89, 0.76));
		builder.setW(ImmutableList.of(0.54, 0.67, 0.89, 0.76));

		final AlternativesComparison altsComp = builder.build();
		return new NunesModel(altsComp, new LinkedHashSet<Constraint>());
	}

	public static NunesModel getExampleDOMINATION() {
		final AlternativesComparisonBuilder builder = new AlternativesComparisonBuilder();

		builder.setX(ImmutableList.of(0.60, 0.69, 0.95, 0.91));
		builder.setY(ImmutableList.of(0.54, 0.67, 0.89, 0.76));
		builder.setW(ImmutableList.of(0.54, 0.67, 0.89, 0.76));

		final AlternativesComparison altsComp = builder.build();
		return new NunesModel(altsComp, new LinkedHashSet<Constraint>());
	}

	public static DominationOutput getExampleCRITICALOutput() {
		NunesModel nm = getExampleCRITICAL();

		Criterion critical = new Criterion(4, "c4", new ArrayList<Double>());

		return new DominationOutput(nm.getAlternativesComparison(), critical);
	}

	public static DominationOutput getExampleDOMINATIONOutput() {
		NunesModel nm = getExampleDOMINATION();

		return new DominationOutput(nm.getAlternativesComparison());
	}

}
