package io.github.oliviercailloux.decision.arguer.nunes.output;

import static java.util.Objects.requireNonNull;

import io.github.oliviercailloux.decision.arguer.AlternativesComparison;
import io.github.oliviercailloux.decision.arguer.nunes.NunesModel;
import io.github.oliviercailloux.decision.model.Criterion;

public class DominationOutput implements NunesOutput {

	private AlternativesComparison<NunesModel> alternativesComparison;
	private Criterion critical;

	public DominationOutput(AlternativesComparison<NunesModel> alternativesComparison) {
		this.alternativesComparison = alternativesComparison;
		this.critical = null;
	}

	public DominationOutput(AlternativesComparison<NunesModel> alternativesComparison, Criterion critical) {
		this.alternativesComparison = alternativesComparison;
		this.critical = requireNonNull(critical);
	}

	public String argue() {

		if (critical != null)
			return alternativesComparison.getX().getName() + " is recommended because it got the best value on "
					+ critical.getName();

		return "There is no reason to choose " + alternativesComparison.getY().getName() + ", as "
				+ alternativesComparison.getX().getName()

				+ "is better on all criteria.";
	}

	public Criterion getCritical() {
		return this.critical;
	}

	@Override
	public Pattern getPattern() {
		return Pattern.DOMINATION;
	}

	@Override
	public AlternativesComparison<NunesModel> getAlternativesComparison() {
		return this.alternativesComparison;
	}

}
