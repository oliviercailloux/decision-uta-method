package io.github.oliviercailloux.decision.arguer.nunes.output;

import io.github.oliviercailloux.decision.arguer.labreuche.AlternativesComparison;
import io.github.oliviercailloux.decision.arguer.nunes.Constraint;

public class CutOffOutput implements NunesOutput {

	private Constraint constraint;
	private AlternativesComparison alternativesComparison;

	public CutOffOutput(AlternativesComparison alternativesComparison, Constraint c) {
		this.constraint = c;
		this.alternativesComparison = alternativesComparison;
	}

	@Override
	public Pattern getPattern() {
		return Pattern.CUTOFF;
	}

	@Override
	public AlternativesComparison getAlternativesComparison() {
		return null;
	}

	public Constraint getConstraint() {
		return this.constraint;
	}

}
