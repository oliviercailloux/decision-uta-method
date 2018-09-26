package io.github.oliviercailloux.decision.arguer.nunes;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

import io.github.oliviercailloux.decision.arguer.AlternativesComparison;
import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class NunesTools {

	public static double score_d(Alternative x, Alternative y, Set<Criterion> criteria, Map<Criterion, Double> weight,
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
		double ext_x = standardDeviation(x);
		double ext_y = standardDeviation(y);

		if (ext_x < ext_y)
			return ext_y - ext_x;

		return 0.0;
	}

	public static double standardDeviation(Alternative x) {
		Set<Double> dv = new LinkedHashSet<>();
		double mean = 0.0;

		for (Criterion c : x.getEvaluations().keySet()) {
			dv.add(1 - x.getEvaluations().get(c));
		}

		for (Double v : dv)
			mean += v;

		mean *= 1.0 / x.getEvaluations().keySet().size();

		double sum = 0.0;

		for (Double v : dv)
			sum += Math.pow(v - mean, 2);

		return Math.sqrt((1.0 / (dv.size() - 1)) * sum);
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

	public static Table<Alternative, Alternative, Double> computeTO(AlternativesComparison alternativesComparison) {
		Table<Alternative, Alternative, Double> tradoffs = HashBasedTable.create();
		Alternative x = alternativesComparison.getX();
		Alternative y = alternativesComparison.getY();

		double costX = cost(x, y, alternativesComparison.getCriteria(), alternativesComparison.getWeight());
		double costY = cost(y, x, alternativesComparison.getCriteria(), alternativesComparison.getWeight());
		
		if (costX > costY && costX != 0.0) {
			tradoffs.put(x, y, costY / costX);
		}
		
		if(costY > costX && costY != 0.0) {
			tradoffs.put(y, x, costX / costY);
		}

		return tradoffs;
	}

	/* Cutoff methods */

	public boolean isSatisfied(Alternative alt, Constraint c) {
		if (c.isFlagMin()) {
			if (alt.getEvaluations().get(c.getCriterion()) > c.getTreshold())
				return false;
		} else {
			if (alt.getEvaluations().get(c.getCriterion()) < c.getTreshold())
				return false;
		}

		return true;
	}

	public boolean lpv(Alternative alt, Constraint c) {
		if ((isSatisfied(alt, c) && c.getValuePref() < 0.0) || (!isSatisfied(alt, c) && c.getValuePref() > 0.0))
			return true;

		return false;
	}

	public Constraint strongestConstraint() {
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
