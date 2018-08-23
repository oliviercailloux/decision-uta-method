package io.github.oliviercailloux.decision.arguer.labreuche.output;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.Map;

import io.github.oliviercailloux.decision.arguer.labreuche.AlternativesComparison;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class RMGCOMPOutput implements LabreucheOutput {

	private AlternativesComparison alternativesComparison;
	private double epsilon;

	public RMGCOMPOutput(AlternativesComparison alternativesComparison, double epsilon) {
		this.alternativesComparison = requireNonNull(alternativesComparison);
		this.epsilon = requireNonNull(epsilon);
		checkArgument(Double.isFinite(epsilon));
	}

	@Override
	public Anchor getAnchor() {
		return Anchor.RMGCOMP;
	}

	@Override
	public AlternativesComparison getAlternativesComparison() {
		return this.alternativesComparison;
	}

	public double getEpsilon() {
		return epsilon;
	}

	/**
	 * @return the value corresponding to W calligraphic.
	 */
	public double getMaxW() {
		double maxW = Double.MIN_VALUE;

		for (Map.Entry<Criterion, Double> c : this.alternativesComparison.getWeight().entrySet()) {
			double v = Math.abs(c.getValue() - (1.0 / this.alternativesComparison.getCriteria().size()));

			if (v > maxW) {
				maxW = v;
			}
		}

		return maxW;
	}
}
