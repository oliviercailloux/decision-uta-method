package io.github.oliviercailloux.decision.arguer.nunes.output;

import java.util.Map;

import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class DominationOutPut implements NunesOutPut {

	public Alternative recommended;
	public Alternative rejected;
	public Map<Criterion, Double> deltas;
	public boolean particular_case_domination;
	public Criterion c_best;

	public DominationOutPut(Alternative x, Alternative y, Map<Criterion, Double> d) {
		recommended = x;
		rejected = y;
		deltas = d;
	}

	public String argue() {

		if (particular_case_domination)
			return recommended.getName() + " is recommended because it got the best value on " + c_best.getName();

		return "There is no reason to choose " + rejected.getName() + ", as " + recommended.getName()
				+ "is better on all criteria.";
	}

	@Override
	public Boolean isApplicable() {
		int count = 0;
		Criterion critical = null;

		for (Criterion c : deltas.keySet()) {
			if (deltas.get(c) > 0.0) {
				count++;
				critical = c;
			}

			if (deltas.get(c) < 0.0)
				return false;
		}

		if (count == 1) {
			particular_case_domination = true;
			c_best = critical;
		}

		return true;
	}

	@Override
	public Pattern getPattern() {
		return Pattern.DOMINATION;
	}

}
