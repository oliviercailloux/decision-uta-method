package io.github.oliviercailloux.decision.arguer.nunes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.github.oliviercailloux.decision.arguer.labreuche.Couple;
import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class NunesModel {

	public List<Criterion> criteria;
	public List<Alternative> alternatives;
	public Map<Criterion, Double> weights;
	public List<Constraint> constrains;
	public Map<Criterion, Double> deltas;
	public Map<Double, Alternative> scoreboard;
	public List<Criterion> positiveArguments;
	public List<Criterion> negativeArguments;
	public List<Criterion> nullArguments;
	public Alternative best_choice;
	public Alternative second_choice;
	public Map<Couple<Alternative, Alternative>, Double> tos;
	public List<Alternative> alt_rejected;

	public NunesModel(List<Criterion> criteria, List<Alternative> x, List<Constraint> c,
			Map<Criterion, Double> weights) {
		this.criteria = criteria;
		this.weights = weights;
		this.constrains = c;
		this.alternatives = x;
		this.deltas = new LinkedHashMap<>();
		this.scoreboard = new HashMap<>();
		this.positiveArguments = new ArrayList<>();
		this.negativeArguments = new ArrayList<>();
		this.nullArguments = new ArrayList<>();
		this.tos = new HashMap<>();
		this.alt_rejected = new ArrayList<>();
	}

	public Double score_d(Alternative x, Alternative y) {
		return cost(x, y) - extAversion(x, y) - toContrast(x, y);
	}

	public Double cost(Alternative x, Alternative y) {
		Double res = 0.0;

		for (Criterion c : criteria)
			res += attCost(x, y, c) * weights.get(c);

		return res;
	}

	public Double attCost(Alternative x, Alternative y, Criterion c) {
		if (y.getEvaluations().get(c) > x.getEvaluations().get(c))
			return y.getEvaluations().get(c) - x.getEvaluations().get(c);

		return 0.0;
	}

	public Double extAversion(Alternative x, Alternative y) {
		Double ext_x = standardDeviation(x);
		Double ext_y = standardDeviation(y);

		if (ext_x < ext_y)
			return ext_y - ext_x;

		return 0.0;
	}

	public Double standardDeviation(Alternative x) {
		List<Double> dv = new ArrayList<>();
		Double mean = 0.0;

		for (Criterion c : x.getEvaluations().keySet()) {
			dv.add(1 - x.getEvaluations().get(c));
		}

		for (Double v : dv)
			mean += v;

		mean *= 1.0 / x.getEvaluations().keySet().size();

		Double sum = 0.0;

		for (Double v : dv)
			sum += Math.pow(v - mean, 2);

		return Math.sqrt((1.0 / (dv.size() - 1)) * sum);
	}

	public Double toContrast(Alternative x, Alternative y) {
		Couple<Alternative, Alternative> toxy = new Couple<>(x, y);
		Couple<Alternative, Alternative> toyx = new Couple<>(y, x);

		computeTos();
		Double avgTo = avgTo();

		if (tos.containsKey(toxy) && tos.get(toxy) <= avgTo)
			return avgTo - tos.get(toxy);

		if (tos.containsKey(toyx) && tos.get(toyx) > avgTo)
			return tos.get(toyx) - avgTo;

		return 0.0;
	}

	public Double avgTo() {
		Double res = 0.0;

		for (Couple<Alternative, Alternative> c : tos.keySet())
			res += tos.get(c);

		return res / tos.keySet().size();
	}

	public void computeTos() {
		List<Alternative> list = new ArrayList<>(alternatives);

		for (Alternative x : alternatives) {
			list.remove(x);
			for (Alternative y : list) {
				Double valx = cost(x, y);
				Double valy = cost(y, x);
				if (valx > valy) {
					Couple<Alternative, Alternative> to = new Couple<>(x, y);
					tos.put(to, valy / valx);
				}
			}
		}
	}

	public void resolved() {

		// todo
	}

}
