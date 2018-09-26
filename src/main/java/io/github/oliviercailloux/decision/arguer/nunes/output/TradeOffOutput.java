package io.github.oliviercailloux.decision.arguer.nunes.output;

import io.github.oliviercailloux.decision.arguer.AlternativesComparison;

public class TradeOffOutput implements NunesOutput {

	private AlternativesComparison alternativesComparison;

	public TradeOffOutput(AlternativesComparison alternativesComparison) {
		this.alternativesComparison = alternativesComparison;
	}

	@Override
	public Pattern getPattern() {
		return Pattern.TRADEOFF;
	}

	@Override
	public AlternativesComparison getAlternativesComparison() {
		return this.alternativesComparison;
	}

}
