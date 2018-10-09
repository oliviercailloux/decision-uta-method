package io.github.oliviercailloux.decision.arguer.nunes.output;

import io.github.oliviercailloux.decision.arguer.AlternativesComparison;
import io.github.oliviercailloux.decision.arguer.nunes.NunesModel;

public class TradeOffOutput implements NunesOutput {

	private AlternativesComparison<NunesModel> alternativesComparison;

	public TradeOffOutput(AlternativesComparison<NunesModel> alternativesComparison) {
		this.alternativesComparison = alternativesComparison;
	}

	@Override
	public Pattern getPattern() {
		return Pattern.TRADEOFF;
	}

	@Override
	public AlternativesComparison<NunesModel> getAlternativesComparison() {
		return this.alternativesComparison;
	}

}
