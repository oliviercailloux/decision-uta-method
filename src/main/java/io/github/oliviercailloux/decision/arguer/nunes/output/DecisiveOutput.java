package io.github.oliviercailloux.decision.arguer.nunes.output;

import java.util.Set;

import io.github.oliviercailloux.decision.arguer.AlternativesComparison;
import io.github.oliviercailloux.decision.arguer.nunes.NunesModel;
import io.github.oliviercailloux.decision.model.Criterion;

public class DecisiveOutput implements NunesOutput {

	private AlternativesComparison<NunesModel> alternativesComparison;
	private Set<Criterion> decisiveCriteria;

	public DecisiveOutput(AlternativesComparison<NunesModel> alternativesComparison, Set<Criterion> criteria) {
		this.alternativesComparison = alternativesComparison;
		this.decisiveCriteria = criteria;
	}

	@Override
	public Pattern getPattern() {
		return Pattern.DECISIVE;
	}

	@Override
	public AlternativesComparison<NunesModel> getAlternativesComparison() {
		return this.alternativesComparison;
	}

	public Set<Criterion> getDecisiveCriteria() {
		return this.decisiveCriteria;
	}

}
