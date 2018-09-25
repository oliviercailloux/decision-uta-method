package io.github.oliviercailloux.decision.arguer.nunes.output;

import java.util.Set;

import io.github.oliviercailloux.decision.arguer.AlternativesComparison;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class DecisiveOutput implements NunesOutput {

	private AlternativesComparison alternativesComparison;
	private Set<Criterion> decisiveCriteria;

	public DecisiveOutput(AlternativesComparison alternativesComparison, Set<Criterion> criteria) {
		this.alternativesComparison = alternativesComparison;
		this.decisiveCriteria = criteria;
	}

	@Override
	public Pattern getPattern() {
		return Pattern.DECISIVE;
	}

	@Override
	public AlternativesComparison getAlternativesComparison() {
		return this.alternativesComparison;
	}

	public Set<Criterion> getDecisiveCriteria() {
		return this.decisiveCriteria;
	}

}
