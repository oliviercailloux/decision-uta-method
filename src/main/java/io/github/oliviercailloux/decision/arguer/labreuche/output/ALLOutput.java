package io.github.oliviercailloux.decision.arguer.labreuche.output;

import static java.util.Objects.requireNonNull;

import io.github.oliviercailloux.decision.arguer.labreuche.AlternativesComparison;

/**
 *
 * Immutable.
 *
 */
public class ALLOutput implements LabreucheOutput {

	private AlternativesComparison alternativesComparison;

	public ALLOutput(AlternativesComparison alternativesComparison) {
		this.alternativesComparison = requireNonNull(alternativesComparison);
	}

	@Override
	public AlternativesComparison getAlternativesComparison() {
		return alternativesComparison;
	}

	@Override
	public Anchor getAnchor() {
		return Anchor.ALL;
	}

}
