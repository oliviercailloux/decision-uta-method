package io.github.oliviercailloux.decision.arguer.labreuche;

import static java.util.Objects.requireNonNull;

import java.util.Map;

import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class ALLOutput implements LabreucheOutput {

	private AlternativesComparison alternativesComparison;

	public ALLOutput(AlternativesComparison alternativesComparison) {
		this.alternativesComparison = requireNonNull(alternativesComparison);
	}

	public String argue() {

		return this.best_choice.getName() + " is preferred to " + this.second_choice.getName() + " since "
				+ this.best_choice.getName() + " is better then " + this.second_choice.getName() + " on ALL criteria.";
	}

	public AlternativesComparison getAlternativesComparison() {
		return alternativesComparison;
	}

	@Override
	public Anchor getAnchor() {
		return Anchor.ALL;
	}

	@Override
	public Boolean isApplicable() {

		for (Map.Entry<Criterion, Double> d : deltas.entrySet()) {
			if (d.getValue() < 0.0) {
				return false;
			}
		}

		return true;
	}

}
