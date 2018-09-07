package io.github.oliviercailloux.decision.arguer.nunes;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import io.github.oliviercailloux.decision.arguer.labreuche.AlternativesComparison;
import io.github.oliviercailloux.decision.arguer.labreuche.Couple;
import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class NunesModel {

	public Set<Alternative> alternatives;
	public Set<Criterion> criteria;
	public Set<Constraint> constrains;
	public Map<Criterion, Double> weights;
	public Set<Criterion> positiveArguments;
	public Set<Criterion> negativeArguments;
	public Set<Criterion> nullArguments;
	public Alternative recommended;
	public AlternativesComparison alternativesComparison;
	public Map<Couple<Alternative, Alternative>, Double> tradoffs;
	public Set<Alternative> alt_rejected;

	public NunesModel(Set<Alternative> x, Set<Criterion> criteria, Set<Constraint> c, Map<Criterion, Double> weights) {
		this.alternatives = x;
		this.criteria = criteria;
		this.constrains = c;
		this.weights = weights;
		this.positiveArguments = new LinkedHashSet<>();
		this.negativeArguments = new LinkedHashSet<>();
		this.nullArguments = new LinkedHashSet<>();
		this.alternativesComparison = null;
		this.tradoffs = new LinkedHashMap<>();
		this.alt_rejected = new LinkedHashSet<>();
	}

	public double score_d(Alternative x, Alternative y) {
		return cost(x, y) - extAversion(x, y) - toContrast(x, y);
	}

	public double cost(Alternative x, Alternative y) {
		double res = 0.0;

		for (Criterion c : criteria)
			res += attCost(x, y, c) * weights.get(c);

		return res;
	}

	public double attCost(Alternative x, Alternative y, Criterion c) {
		if (y.getEvaluations().get(c) > x.getEvaluations().get(c))
			return y.getEvaluations().get(c) - x.getEvaluations().get(c);

		return 0.0;
	}

	public double extAversion(Alternative x, Alternative y) {
		double ext_x = standardDeviation(x);
		double ext_y = standardDeviation(y);

		if (ext_x < ext_y)
			return ext_y - ext_x;

		return 0.0;
	}

	public double standardDeviation(Alternative x) {
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

	public double toContrast(Alternative x, Alternative y) {
		Couple<Alternative, Alternative> toxy = new Couple<>(x, y);
		Couple<Alternative, Alternative> toyx = new Couple<>(y, x);

		computeTos();
		double avgTo = avgTo();

		if (tradoffs.containsKey(toxy) && tradoffs.get(toxy) <= avgTo)
			return avgTo - tradoffs.get(toxy);

		if (tradoffs.containsKey(toyx) && tradoffs.get(toyx) > avgTo)
			return tradoffs.get(toyx) - avgTo;

		return 0.0;
	}

	public double avgTo() {
		double res = 0.0;

		for (Couple<Alternative, Alternative> c : tradoffs.keySet())
			res += tradoffs.get(c);

		return res / tradoffs.keySet().size();
	}

	public void computeTos() {
		Set<Alternative> list = new LinkedHashSet<>(alternatives);

		for (Alternative x : alternatives) {
			list.remove(x);
			for (Alternative y : list) {
				Double valx = cost(x, y);
				Double valy = cost(y, x);
				if (valx > valy) {
					Couple<Alternative, Alternative> to = new Couple<>(x, y);
					tradoffs.put(to, valy / valx);
				}
			}
		}
	}

	public void resolved() {

		// todo
	}

}
