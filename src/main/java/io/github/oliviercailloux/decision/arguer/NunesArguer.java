package io.github.oliviercailloux.decision.arguer;

import io.github.oliviercailloux.decision.Utils;
import io.github.oliviercailloux.decision.arguer.nunes.output.CutOffOutput;
import io.github.oliviercailloux.decision.arguer.nunes.output.DecisiveOutput;
import io.github.oliviercailloux.decision.arguer.nunes.output.DominationOutput;
import io.github.oliviercailloux.decision.arguer.nunes.output.NunesOutput;
import io.github.oliviercailloux.decision.arguer.nunes.output.TradeOffOutput;

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
			return argueDecisive((DecisiveOutput) output);

		case TRADEOFF:
			return argueTradeOff((TradeOffOutput) output);

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

		return alcoCutoff.getX().getName() + " is rejected because she doesn't satisfied the constrain on the criteria "
				+ output.getConstraint().getCriterion().getName();
	}

	private String argueDecisive(DecisiveOutput output) {
		AlternativesComparison alco = output.getAlternativesComparison();

		return alco.getX() + "was chosen because of its " + Utils.showCriteria(alco.getCriteria());
	}

	private String argueTradeOff(TradeOffOutput output) {
		AlternativesComparison alco = output.getAlternativesComparison();

		return "Even though " + alco.getY() + " rovides better pros than " + alco.getX() + " on "
				+ Utils.showCriteria(alco.getPositiveCriteria()) + ", it has worse cons on "
				+ Utils.showCriteria(alco.getNegativeCriteria()) + ".";

	}

}
