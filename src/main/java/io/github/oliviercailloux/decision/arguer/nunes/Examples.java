package io.github.oliviercailloux.decision.arguer.nunes;

import java.util.ArrayList;
import java.util.LinkedHashSet;

import com.google.common.collect.ImmutableList;

import io.github.oliviercailloux.decision.arguer.AlternativesComparison;
import io.github.oliviercailloux.decision.arguer.AlternativesComparisonBuilder;
import io.github.oliviercailloux.decision.arguer.nunes.output.DominationOutput;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class Examples {

	private Examples() {
		throw new IllegalStateException("Nunes Examples class");
	}

	public static NunesComputer getExampleCRITICAL() {
		final AlternativesComparisonBuilder builder = new AlternativesComparisonBuilder();

		builder.setX(ImmutableList.of(0.54, 0.67, 0.89, 0.90));
		builder.setY(ImmutableList.of(0.54, 0.67, 0.89, 0.76));
		builder.setW(ImmutableList.of(0.54, 0.67, 0.89, 0.76));

		final AlternativesComparison altsComp = builder.build();
		return new NunesComputer(altsComp, new LinkedHashSet<Constraint>());
	}

	public static NunesComputer getExampleDOMINATION() {
		final AlternativesComparisonBuilder builder = new AlternativesComparisonBuilder();

		builder.setX(ImmutableList.of(0.60, 0.69, 0.95, 0.91));
		builder.setY(ImmutableList.of(0.54, 0.67, 0.89, 0.76));
		builder.setW(ImmutableList.of(0.54, 0.67, 0.89, 0.76));

		final AlternativesComparison altsComp = builder.build();
		return new NunesComputer(altsComp, new LinkedHashSet<Constraint>());
	}

	public static DominationOutput getExampleCRITICALOutput() {
		NunesComputer nm = getExampleCRITICAL();

		Criterion critical = new Criterion(4, "c4", new ArrayList<Double>());

		return new DominationOutput(nm.getAlternativesComparison(), critical);
	}

	public static DominationOutput getExampleDOMINATIONOutput() {
		NunesComputer nm = getExampleDOMINATION();

		return new DominationOutput(nm.getAlternativesComparison());
	}

}
