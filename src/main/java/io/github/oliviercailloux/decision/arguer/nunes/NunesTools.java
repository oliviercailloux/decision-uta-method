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

import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class NunesTools {

	/* * methods for decision function d(x,y) * */
	
	public static double score(Alternative x, Alternative y, Set<Criterion> criteria, Map<Criterion, Double> weight,
			Table<Alternative, Alternative, Double> tradoffs) {
		return cost(x, y, criteria, weight) + (0.25 * extAversion(x, y)) + (0.15 * toContrast(x, y, tradoffs));
	}

	public static double cost(Alternative x, Alternative y, Set<Criterion> criteria, Map<Criterion, Double> weight) {
		double res = 0.0;

		for (Criterion c : criteria)
			res += attCost(x, y, c) * weight.get(c);

		return res;
	}

	public static double attCost(Alternative x, Alternative y, Criterion c) {
		if (y.getEvaluations().get(c) > x.getEvaluations().get(c))
			return y.getEvaluations().get(c) - x.getEvaluations().get(c);

		return 0.0;
	}

	public static double extAversion(Alternative x, Alternative y) {
		double extX = standardDeviation(x);
		double extY = standardDeviation(y);

		if (extX < extY) {
			return extY - extX;
		}

		return 0.0;
	}
	
	/**
	 * @param x
	 * @return Standard deviation of the costs performances (1 - x_i) of the
	 * alternative x.
	 */
	public static double standardDeviation(Alternative x) {
		Set<Double> dv = new LinkedHashSet<>();

		for (Criterion c : x.getEvaluations().keySet()) {
			dv.add(1 - x.getEvaluations().get(c));
		}

		return Stats.of(dv).populationStandardDeviation();
	}

	public static double toContrast(Alternative x, Alternative y, Table<Alternative, Alternative, Double> tradoffs) {

		double avgTo = avgTo(tradoffs);

		if (tradoffs.contains(x, y) && tradoffs.get(x, y) <= avgTo)
			return avgTo - tradoffs.get(x, y);

		if (tradoffs.contains(y, x) && tradoffs.get(y, x) > avgTo)
			return tradoffs.get(y, x) - avgTo;

		return 0.0;
	}

	public static double avgTo(Table<Alternative, Alternative, Double> tradoffs) {
		double res = 0.0;

		for (Cell<Alternative, Alternative, Double> c : tradoffs.cellSet())
			res += c.getValue();

		return res / tradoffs.cellSet().size();
	}

	public static Table<Alternative, Alternative, Double> computeTO(Set<Alternative> alternatives,
			Map<Criterion, Double> weights) {
		Table<Alternative, Alternative, Double> tradoffs = HashBasedTable.create();

		double costA;
		double costB;

		for (Alternative a : alternatives) {
			for (Alternative b : alternatives) {
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

	public boolean isSatisfied(Alternative alt, Constraint c) {
		if (c.isFlagMin())
			return alt.getEvaluations().get(c.getCriterion()) > c.getTreshold();
		else
			return alt.getEvaluations().get(c.getCriterion()) < c.getTreshold();
	}

	public boolean lpv(Alternative alt, Constraint c) {
		return (isSatisfied(alt, c) && c.getValuePref() < 0.0) || (!isSatisfied(alt, c) && c.getValuePref() > 0.0);
	}

	public Constraint strongestConstraint() {
		@SuppressWarnings("unused")
		Map<Double, Constraint> cons = new HashMap<>();

		return null;
	}

	public static double getPros(Alternative x, Alternative y, Map<Criterion, Double> weights) {
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

	public static double getCons(Alternative x, Alternative y, Map<Criterion, Double> weights) {
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
