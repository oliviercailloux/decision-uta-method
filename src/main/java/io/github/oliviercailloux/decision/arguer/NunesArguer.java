package io.github.oliviercailloux.decision.arguer;

import io.github.oliviercailloux.decision.arguer.nunes.output.CutOffOutput;
import io.github.oliviercailloux.decision.arguer.nunes.output.DominationOutput;
import io.github.oliviercailloux.decision.arguer.nunes.output.NunesOutput;

public class NunesArguer {

	public String argue(NunesOutput output) {
		String explanation = "";

		switch (output.getPattern()) {

		case DOMINATION:
			return argueDomination((DominationOutput) output);

		case CUTOFF:
			return argueCutOff((CutOffOutput) output);

		case MINREQM:
			return explanation;

		case MINREQP:
			return explanation;

		case DECISIVE:
			return explanation;

		case PROSCONS:
			return explanation;

		default:
			return "No possible argumentation found";

		}
	}

	private String argueDomination(DominationOutput output) {
		String explanation = "";

		AlternativesComparison alcoDOM = output.getAlternativesComparison();

		if (output.getCritical() != null) {
			explanation = alcoDOM.getX().getName() + " is recommended because it got the best value on "
					+ output.getCritical().getName();
		}

		explanation = "There is no reason to choose " + alcoDOM.getY().getName() + ", as " + alcoDOM.getX().getName()
				+ "is better on all criteria.";

		return explanation;
	}

	private String argueCutOff(CutOffOutput output) {

		AlternativesComparison alcoCutoff = output.getAlternativesComparison();

		String explanation = alcoCutoff.getX().getName()
				+ " is rejected because she doesn't satisfied the constrain on the criteria "
				+ output.getConstraint().getCriterion().getName();

		return explanation;
	}

}
