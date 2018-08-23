package io.github.oliviercailloux.decision.arguer.labreuche.output;

import static java.util.Objects.requireNonNull;

import io.github.oliviercailloux.decision.arguer.labreuche.AlternativesComparison;

public class RMGAVGOutput implements LabreucheOutput {

	private AlternativesComparison alternativesComparison;

	public RMGAVGOutput(AlternativesComparison alternativesComparison) {
		this.alternativesComparison = requireNonNull(alternativesComparison);
	}

	@Override
	public Anchor getAnchor() {
		return Anchor.RMGAVG;
	}

	@Override
	public AlternativesComparison getAlternativesComparison() {
		return this.alternativesComparison;
	}

}
