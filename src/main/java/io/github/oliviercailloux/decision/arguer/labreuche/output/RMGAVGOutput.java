package io.github.oliviercailloux.decision.arguer.labreuche.output;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.github.oliviercailloux.decision.arguer.labreuche.AlternativesComparison;
import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class RMGAVGOutput implements LabreucheOutput {

	private AlternativesComparison alternativesComparison;
	private Double epsilon;
	private Double max_w;

	public RMGAVGOutput(AlternativesComparison alternativesComparison) {
		this.alternativesComparison = requireNonNull(alternativesComparison);
		this.epsilon = 0.2 / this.alternativesComparison.getCriteria().size();
		this.max_w = null;
	}
	
	
	@Override
	public Anchor getAnchor() {
		return Anchor.RMGAVG;
	}
	
	
	@Override
	public AlternativesComparison getAlternativesComparison() {
		return this.alternativesComparison;
	}
	
	
	public Double getEpsilon() {
		return epsilon;
	}

	
	public Double getMax_w() {
		return max_w;
	}

}
