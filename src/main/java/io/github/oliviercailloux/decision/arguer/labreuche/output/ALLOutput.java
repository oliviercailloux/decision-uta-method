package io.github.oliviercailloux.decision.arguer.labreuche.output;

import static java.util.Objects.requireNonNull;

import io.github.oliviercailloux.decision.arguer.AlternativesComparison;
import io.github.oliviercailloux.decision.arguer.labreuche.LabreucheModel;

/**
 *
 * Immutable.
 *
 */
public class ALLOutput implements LabreucheOutput {

	private AlternativesComparison<LabreucheModel> alternativesComparison;

	public ALLOutput(AlternativesComparison<LabreucheModel> alternativesComparison) {
		this.alternativesComparison = requireNonNull(alternativesComparison);
	}

	@Override
	public AlternativesComparison<LabreucheModel> getAlternativesComparison() {
		return alternativesComparison;
	}

	@Override
	public Anchor getAnchor() {
		return Anchor.ALL;
	}

}
