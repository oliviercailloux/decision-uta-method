package io.github.oliviercailloux.decision.arguer.labreuche.output;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		ALLOutput other = (ALLOutput) obj;

		assert other.getAnchor() == getAnchor();
		return other.getAlternativesComparison().equals(alternativesComparison);
	}

	@Override
	public int hashCode() {
		return Objects.hash(alternativesComparison, getAnchor());
	}

}
