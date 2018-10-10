package io.github.oliviercailloux.decision.arguer.nunes.output;

import static java.util.Objects.requireNonNull;

import io.github.oliviercailloux.decision.arguer.AlternativesComparison;
import io.github.oliviercailloux.decision.arguer.nunes.NunesModel;
import io.github.oliviercailloux.decision.model.Criterion;

public class CriticalAttributeOutput implements NunesOutput {
	
	private AlternativesComparison<NunesModel> alternativesComparison;
	private Criterion critical;

	public CriticalAttributeOutput(AlternativesComparison<NunesModel> alternativesComparison, Criterion critical) {
		this.alternativesComparison = alternativesComparison;
		this.critical = requireNonNull(critical);
	}

	public String argue() {

			return alternativesComparison.getX().getName() + " is recommended because it got the best value on "
					+ critical.getName();
	}

	public Criterion getCritical() {
		return this.critical;
	}

	@Override
	public Pattern getPattern() {
		return Pattern.CRIT;
	}

	@Override
	public AlternativesComparison<NunesModel> getAlternativesComparison() {
		return this.alternativesComparison;
	}

}
