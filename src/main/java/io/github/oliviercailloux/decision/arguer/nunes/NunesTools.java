package io.github.oliviercailloux.decision.arguer.nunes;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;
import com.google.common.math.Stats;

import io.github.oliviercailloux.decision.model.Criterion;
import io.github.oliviercailloux.decision.model.EvaluatedAlternative;

public class NunesTools {

	/* * methods for decision function d(x,y) * */

	public static double score(EvaluatedAlternative evaluatedAlternative, EvaluatedAlternative evaluatedAlternative2,
			Set<Criterion> set, Map<Criterion, Double> weights,
			Table<EvaluatedAlternative, EvaluatedAlternative, Double> tradeoffs) {
		return cost(evaluatedAlternative, evaluatedAlternative2, set, weights)
				+ (0.25 * extAversion(evaluatedAlternative, evaluatedAlternative2))
				+ (0.15 * toContrast(evaluatedAlternative, evaluatedAlternative2, tradeoffs));
	}

	public static double cost(EvaluatedAlternative evaluatedAlternative, EvaluatedAlternative evaluatedAlternative2,
			Set<Criterion> set, Map<Criterion, Double> weights) {
		double res = 0.0;

		for (Criterion c : set)
			res += attCost(evaluatedAlternative, evaluatedAlternative2, c) * weights.get(c);

		return res;
	}

	public static double attCost(EvaluatedAlternative x, EvaluatedAlternative y, Criterion c) {
		if (y.getEvaluations().get(c) > x.getEvaluations().get(c))
			return y.getEvaluations().get(c) - x.getEvaluations().get(c);

		return 0.0;
	}

	public static double extAversion(EvaluatedAlternative evaluatedAlternative,
			EvaluatedAlternative evaluatedAlternative2) {
		double extX = standardDeviation(evaluatedAlternative);
		double extY = standardDeviation(evaluatedAlternative2);

		if (extX < extY) {
			return extY - extX;
		}

		return 0.0;
	}

	/**
	 * @param x
	 * @return Standard deviation of the costs performances (1 - x_i) of the
	 *         alternative x.
	 */
	public static double standardDeviation(EvaluatedAlternative x) {
		Set<Double> dv = new LinkedHashSet<>();

		for (Criterion c : x.getEvaluations().keySet()) {
			dv.add(1 - x.getEvaluations().get(c));
		}

		return Stats.of(dv).populationStandardDeviation();
	}

	public static double toContrast(EvaluatedAlternative evaluatedAlternative,
			EvaluatedAlternative evaluatedAlternative2,
			Table<EvaluatedAlternative, EvaluatedAlternative, Double> tradeoffs) {

		double avgTo = avgTo(tradeoffs);

		if (tradeoffs.contains(evaluatedAlternative, evaluatedAlternative2)
				&& tradeoffs.get(evaluatedAlternative, evaluatedAlternative2) <= avgTo)
			return avgTo - tradeoffs.get(evaluatedAlternative, evaluatedAlternative2);

		if (tradeoffs.contains(evaluatedAlternative2, evaluatedAlternative)
				&& tradeoffs.get(evaluatedAlternative2, evaluatedAlternative) > avgTo)
			return tradeoffs.get(evaluatedAlternative2, evaluatedAlternative) - avgTo;

		return 0.0;
	}

	public static double avgTo(Table<EvaluatedAlternative, EvaluatedAlternative, Double> tradoffs) {
		double res = 0.0;

		for (Cell<EvaluatedAlternative, EvaluatedAlternative, Double> c : tradoffs.cellSet())
			res += c.getValue();

		return res / tradoffs.cellSet().size();
	}

	public static Table<EvaluatedAlternative, EvaluatedAlternative, Double> computeTO(Set<EvaluatedAlternative> set,
			Map<Criterion, Double> weights) {
		Table<EvaluatedAlternative, EvaluatedAlternative, Double> tradoffs = HashBasedTable.create();

		double costA;
		double costB;

		for (EvaluatedAlternative a : set) {
			for (EvaluatedAlternative b : set) {
				if (!a.equals(b)) {
					costA = cost(a, b, weights.keySet(), weights);
					costB = cost(b, a, weights.keySet(), weights);

					if (costA > costB && costA != 0.0) {
						tradoffs.put(a, b, costB / costA);
					}

					if (costB > costA && costB != 0.0) {
						tradoffs.put(b, a, costA / costB);
					}
				}
			}
		}

		return tradoffs;
	}

	/* Cutoff methods */

	public boolean isSatisfied(EvaluatedAlternative alt, Constraint c) {
		if (c.isFlagMin()) {
			return alt.getEvaluations().get(c.getCriterion()) > c.getTreshold();
		}

		return alt.getEvaluations().get(c.getCriterion()) < c.getTreshold();
	}

	public boolean lpv(EvaluatedAlternative alt, Constraint c) {
		return (isSatisfied(alt, c) && c.getValuePref() < 0.0) || (!isSatisfied(alt, c) && c.getValuePref() > 0.0);
	}

	public Constraint strongestConstraint() {
		@SuppressWarnings("unused")
		Map<Double, Constraint> cons = new HashMap<>();

		return null;
	}

	public static double getPros(EvaluatedAlternative x, EvaluatedAlternative y, Map<Criterion, Double> weights) {
		double sum = 0.0;
		double tmp;
		for (Entry<Criterion, Double> entry : weights.entrySet()) {
			tmp = entry.getValue() * attCost(y, x, entry.getKey());
			if (tmp > 0) {
				sum += tmp;
			}
		}
		return sum;
	}

	public static double getCons(EvaluatedAlternative x, EvaluatedAlternative y, Map<Criterion, Double> weights) {
		double sum = 0.0;
		double tmp;
		for (Entry<Criterion, Double> entry : weights.entrySet()) {
			tmp = entry.getValue() * attCost(x, y, entry.getKey());
			if (tmp > 0) {
				sum += tmp;
			}
		}
		return sum;
	}
}
