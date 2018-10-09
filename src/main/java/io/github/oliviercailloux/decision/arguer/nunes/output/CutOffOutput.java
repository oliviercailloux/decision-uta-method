package io.github.oliviercailloux.decision.arguer.nunes.output;

import io.github.oliviercailloux.decision.arguer.AlternativesComparison;
import io.github.oliviercailloux.decision.arguer.nunes.Constraint;
import io.github.oliviercailloux.decision.arguer.nunes.NunesModel;

public class CutOffOutput implements NunesOutput {

	private Constraint constraint;
	private AlternativesComparison<NunesModel> alternativesComparison;

	public CutOffOutput(AlternativesComparison<NunesModel> alternativesComparison, Constraint c) {
		this.constraint = c;
		this.alternativesComparison = alternativesComparison;
	}

	@Override
	public Pattern getPattern() {
		return Pattern.CUTOFF;
	}

	@Override
	public AlternativesComparison<NunesModel> getAlternativesComparison() {
		return this.alternativesComparison;
	}

	public Constraint getConstraint() {
		return this.constraint;
	}

}
