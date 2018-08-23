package io.github.oliviercailloux.decision.arguer.labreuche.output;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.oliviercailloux.decision.arguer.labreuche.AlternativesComparison;
import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class RMGCOMPOutput implements LabreucheOutput {

	private AlternativesComparison alternativesComparison;
	private Double epsilon;

	public RMGCOMPOutput(AlternativesComparison alternativesComparison, Double epsilon) {
		this.alternativesComparison = requireNonNull(alternativesComparison);
		this.epsilon = requireNonNull(epsilon);
	}

	
	@Override
	public Anchor getAnchor() {
		return Anchor.RMGCOMP;
	}
	
	
	@Override
	public AlternativesComparison getAlternativesComparison() {
		return this.alternativesComparison;
	}


	public Double getEpsilon() {
		return epsilon;
	}

	
	public Double getMax_w() {
		Double max_w = Double.MIN_VALUE;

		for (Map.Entry<Criterion, Double> c : this.alternativesComparison.getWeight().entrySet()) {
			Double v = Math.abs(c.getValue() - (1.0 / this.alternativesComparison.getCriteria().size()));

			if (v > max_w) {
				max_w = v;
			}
		}
		
		return max_w;
	}
}
