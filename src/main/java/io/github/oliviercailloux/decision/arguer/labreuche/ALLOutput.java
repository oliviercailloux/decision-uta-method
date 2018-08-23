package io.github.oliviercailloux.decision.arguer.labreuche;

import static java.util.Objects.requireNonNull;

import java.util.Map;

import io.github.oliviercailloux.uta_calculator.model.Criterion;

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
