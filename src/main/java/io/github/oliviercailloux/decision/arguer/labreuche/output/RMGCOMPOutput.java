package io.github.oliviercailloux.decision.arguer.labreuche.output;

import static java.util.Objects.requireNonNull;

import java.util.Map;
import java.util.Objects;

import io.github.oliviercailloux.decision.arguer.AlternativesComparison;
import io.github.oliviercailloux.decision.arguer.labreuche.LabreucheModel;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class RMGCOMPOutput implements LabreucheOutput {

	private AlternativesComparison<LabreucheModel> alternativesComparison;

	public RMGCOMPOutput(AlternativesComparison<LabreucheModel> alternativesComparison) {
		this.alternativesComparison = requireNonNull(alternativesComparison);
	}

	@Override
	public Anchor getAnchor() {
		return Anchor.RMGCOMP;
	}

	@Override
	public AlternativesComparison<LabreucheModel> getAlternativesComparison() {
		return this.alternativesComparison;
	}

	/**
	 * @return the value corresponding to W calligraphic.
	 */
	public double getMaxW() {
		double maxW = Double.MIN_VALUE;
		double n = alternativesComparison.getCriteria().size();

		for (Map.Entry<Criterion, Double> entry : alternativesComparison.getPreferenceModel().getWeights().entrySet()) {
			double v = Math.abs(entry.getValue() - (1.0 / n));

			if (v > maxW) {
				maxW = v;
			}
		}

		return maxW;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		RMGCOMPOutput other = (RMGCOMPOutput) obj;

		assert other.getAnchor() == getAnchor();
		return other.getAlternativesComparison().equals(alternativesComparison);
	}

	@Override
	public int hashCode() {
		return Objects.hash(alternativesComparison, getAnchor());
	}
}
