package io.github.oliviercailloux.labreuche.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.graph.Graph;

import io.github.oliviercailloux.decision.arguer.labreuche.Couple;
import io.github.oliviercailloux.decision.arguer.labreuche.LabreucheModel;
import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class Testouille {

	public void main(String[] args) {

		Graph<Couple<Criterion, Criterion>> graph;

		Double[] x = { 0.50, 0.06, 0.03, 0.95, 0.87, 0.20, 0.95 };
		Double[] y = { 0.99, 0.35, 0.31, 0.51, 0.62, 0.57, 0.52 };
		Double[] w15 = { 0.11, 0.14, 0.13, 0.02, 0.27, 0.25, 0.08 }; // example 15 IVT
		List<Criterion> criteriaEx15 = new ArrayList<>();

		for (int i = 0; i < w15.length; i++) {
			criteriaEx15.add(new Criterion(i + 1, "c" + (i + 1), new ArrayList<Double>()));
		}

		Map<Criterion, Double> x_perfEx15 = new LinkedHashMap<>();
		Map<Criterion, Double> y_perfEx15 = new LinkedHashMap<>();

		for (int i = 0; i < x.length; i++) {
			x_perfEx15.put(criteriaEx15.get(i), x[i]);
			y_perfEx15.put(criteriaEx15.get(i), y[i]);
		}

		Alternative x_15 = new Alternative(1, "X", x_perfEx15);
		Alternative y_15 = new Alternative(2, "Y", y_perfEx15);

		Map<Criterion, Double> weightsEx15 = new LinkedHashMap<>();

		for (int i = 0; i < criteriaEx15.size(); i++) {
			weightsEx15.put(criteriaEx15.get(i), w15[i]);
		}

		LabreucheModel lm15 = new LabreucheModel(x_15, y_15, weightsEx15);

		System.out.println("is IVT anchor applicable? " + lm15.isAnchorIVTApplicable());

		System.out.println(lm15.arguer());

	}
}
