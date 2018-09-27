package io.github.oliviercailloux.decision.arguer.labreuche.output;

import static java.util.Objects.requireNonNull;

import io.github.oliviercailloux.decision.arguer.AlternativesComparison;
import io.github.oliviercailloux.decision.arguer.labreuche.LabreucheModel;

/**
 *
 * Immutable.
 *
 */
public class RMGAVGOutput implements LabreucheOutput {

	private AlternativesComparison<LabreucheModel> alternativesComparison;

	public RMGAVGOutput(AlternativesComparison<LabreucheModel> alternativesComparison) {
		this.alternativesComparison = requireNonNull(alternativesComparison);
	}

	@Override
	public Anchor getAnchor() {
		return Anchor.RMGAVG;
	}

	@Override
	public AlternativesComparison<LabreucheModel> getAlternativesComparison() {
		return this.alternativesComparison;
	}

}
