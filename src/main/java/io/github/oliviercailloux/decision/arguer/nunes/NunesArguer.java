package io.github.oliviercailloux.decision.arguer.nunes;

import io.github.oliviercailloux.decision.Utils;
import io.github.oliviercailloux.decision.arguer.AlternativesComparison;
import io.github.oliviercailloux.decision.arguer.nunes.output.CriticalAttributeOutput;
import io.github.oliviercailloux.decision.arguer.nunes.output.CutOffOutput;
import io.github.oliviercailloux.decision.arguer.nunes.output.DecisiveOutput;
import io.github.oliviercailloux.decision.arguer.nunes.output.DominationOutput;
import io.github.oliviercailloux.decision.arguer.nunes.output.NunesOutput;
import io.github.oliviercailloux.decision.arguer.nunes.output.TradeOffOutput;

public class NunesArguer {

	public String argue(NunesOutput output) {

		switch (output.getPattern()) {

		case CRIT:
			return argueCriticalAttribut((CriticalAttributeOutput) output);
			
		case DOM:
			return argueDomination((DominationOutput) output);

		case CUTOFF:
			return argueCutOff((CutOffOutput) output);

		case MINREQM:
			throw new UnsupportedOperationException();

		case MINREQP:
			throw new UnsupportedOperationException();

		case DECISIVE:
			return argueDecisive((DecisiveOutput) output);

		case TRADEOFF:
			return argueTradeOff((TradeOffOutput) output);

		default:
			throw new IllegalStateException();
		}
	}

	private String argueCriticalAttribut(CriticalAttributeOutput output) {
		AlternativesComparison<NunesModel> alcoCRIT = output.getAlternativesComparison();
		
		return alcoCRIT.getX().getName() + " is recommended because it got the best value on "
				+ output.getCritical().getName();
	}

	private String argueDomination(DominationOutput output) {
		AlternativesComparison<NunesModel> alcoDOM = output.getAlternativesComparison();

		return "There is no reason to choose " + alcoDOM.getY().getName() + ", as " + alcoDOM.getX().getName()
				+ "is better on all criteria.";
	}

	private String argueCutOff(CutOffOutput output) {
		AlternativesComparison<NunesModel> alcoCutoff = output.getAlternativesComparison();

		return alcoCutoff.getX().getName() + " is rejected because she doesn't satisfied the constrain on the criteria "
				+ output.getConstraint().getCriterion().getName();
	}

	private String argueDecisive(DecisiveOutput output) {
		AlternativesComparison<NunesModel> alco = output.getAlternativesComparison();

		return alco.getX() + "was chosen because of its " + Utils.showCriteria(alco.getCriteria());
	}

	private String argueTradeOff(TradeOffOutput output) {

		AlternativesComparison<NunesModel> alco = output.getAlternativesComparison();

		return "Even though " + alco.getY() + " rovides better pros than " + alco.getX() + " on "
				+ Utils.showCriteria(alco.getPositiveCriteria()) + ", it has worse cons on "
				+ Utils.showCriteria(alco.getNegativeCriteria()) + ".";
	}

}
