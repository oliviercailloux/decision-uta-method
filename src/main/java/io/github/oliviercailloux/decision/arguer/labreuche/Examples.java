package io.github.oliviercailloux.decision.arguer.labreuche;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import com.google.common.graph.MutableGraph;

import io.github.oliviercailloux.decision.arguer.labreuche.output.ALLOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.IVTOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.NOAOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.RMGAVGOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.RMGCOMPOutput;
import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class Examples {

	public static LabreucheModel getExampleAll() { // ALL example
		Double[] w = { 0.41, 0.06, 0.24, 0.29 };
		Double[] x = { 0.20, 0.60, 0.60, 0.50 };
		Double[] y = { 0.54, 0.67, 0.89, 0.76 };

		Set<Criterion> criteria = new LinkedHashSet<>();

		for (int i = 0; i < w.length; i++) {
			criteria.add(new Criterion(i + 1, "c" + (i + 1), new ArrayList<Double>()));
		}

		Map<Criterion, Double> x_perf = new LinkedHashMap<>();
		Map<Criterion, Double> y_perf = new LinkedHashMap<>();
		Map<Criterion, Double> weights = new LinkedHashMap<>();

		int ind = 0;
		for (Criterion c : criteria) {
			x_perf.put(c, x[ind]);
			y_perf.put(c, y[ind]);
			weights.put(c, w[ind]);
			ind++;
		}

		Alternative altX = new Alternative(1, "X", x_perf);
		Alternative altY = new Alternative(2, "Y", y_perf);
		AlternativesComparison altsComp = new AlternativesComparison(altX, altY, weights);

		return new LabreucheModel(altsComp);
	}

	public static LabreucheModel getExample5() { // NOA example

		Double[] w5 = { 0.41, 0.06, 0.24, 0.29 };
		Double[] x5 = { 0.42, 0.66, 0.66, 0.57 };
		Double[] y5 = { 0.54, 0.04, 0.89, 0.76 };

		Set<Criterion> criteriaEx5 = new LinkedHashSet<>();

		for (int i = 0; i < w5.length; i++) {
			criteriaEx5.add(new Criterion(i + 1, "c" + (i + 1), new ArrayList<Double>()));
		}

		Map<Criterion, Double> x_perfEx5 = new LinkedHashMap<>();
		Map<Criterion, Double> y_perfEx5 = new LinkedHashMap<>();
		Map<Criterion, Double> weightsEx5 = new LinkedHashMap<>();

		int ind = 0;
		for (Criterion c : criteriaEx5) {
			x_perfEx5.put(c, x5[ind]);
			y_perfEx5.put(c, y5[ind]);
			weightsEx5.put(c, w5[ind]);
			ind++;
		}

		Alternative x_5 = new Alternative(1, "X", x_perfEx5);
		Alternative y_5 = new Alternative(2, "Y", y_perfEx5);
		AlternativesComparison altsEx5 = new AlternativesComparison(x_5, y_5, weightsEx5);

		return new LabreucheModel(altsEx5);
	}

	public static LabreucheModel getExample6() { // NOA example

		Double[] w6 = { 0.18, 0.11, 0.12, 0.24, 0.35 };
		Double[] x6 = { 0.95, 0.67, 0.64, 0.27, 0.39 };
		Double[] y6 = { 0.30, 0.37, 0.41, 0.94, 0.49 };

		Set<Criterion> criteriaEx6 = new LinkedHashSet<>();

		for (int i1 = 0; i1 < w6.length; i1++) {
			criteriaEx6.add(new Criterion(i1 + 1, "c" + (i1 + 1), new ArrayList<Double>()));
		}

		Map<Criterion, Double> x_perfEx6 = new LinkedHashMap<>();
		Map<Criterion, Double> y_perfEx6 = new LinkedHashMap<>();
		Map<Criterion, Double> weightsEx6 = new LinkedHashMap<>();

		int ind = 0;
		for (Criterion c : criteriaEx6) {
			x_perfEx6.put(c, x6[ind]);
			y_perfEx6.put(c, y6[ind]);
			weightsEx6.put(c, w6[ind]);
			ind++;
		}

		Alternative x_6 = new Alternative(1, "X", x_perfEx6);
		Alternative y_6 = new Alternative(2, "Y", y_perfEx6);
		AlternativesComparison altsEx6 = new AlternativesComparison(x_6, y_6, weightsEx6);

		return new LabreucheModel(altsEx6);

	}

	public static LabreucheModel getExample9() { // IVT example

		Double[] w9 = { 0.06, 0.11, 0.21, 0.29, 0.33 };
		Double[] x9 = { 0.89, 0.03, 0.07, 0.32, 0.38 };
		Double[] y9 = { 0.36, 0.76, 0.60, 0.25, 0.75 };

		Set<Criterion> criteriaEx9 = new LinkedHashSet<>();

		for (int i1 = 0; i1 < w9.length; i1++) {
			criteriaEx9.add(new Criterion(i1 + 1, "c" + (i1 + 1), new ArrayList<Double>()));
		}

		Map<Criterion, Double> x_perfEx9 = new LinkedHashMap<>();
		Map<Criterion, Double> y_perfEx9 = new LinkedHashMap<>();
		Map<Criterion, Double> weightsEx9 = new LinkedHashMap<>();

		int ind = 0;
		for (Criterion c : criteriaEx9) {
			x_perfEx9.put(c, x9[ind]);
			y_perfEx9.put(c, y9[ind]);
			weightsEx9.put(c, w9[ind]);
			ind++;
		}

		Alternative x_9 = new Alternative(1, "X", x_perfEx9);
		Alternative y_9 = new Alternative(2, "Y", y_perfEx9);
		AlternativesComparison altsEx9 = new AlternativesComparison(x_9, y_9, weightsEx9);

		return new LabreucheModel(altsEx9);

	}

	public static LabreucheModel getExample10() { // IVT example

		Double[] w10 = { 0.13, 0.04, 0.12, 0.10, 0.07, 0.19, 0.15, 0.03, 0.01, 0.16 };
		Double[] x10 = { 0.61, 0.28, 0.08, 0.02, 0.81, 0.15, 0.16, 0.38, 0.24, 0.75 };
		Double[] y10 = { 0.45, 0.64, 0.86, 0.76, 0.87, 0.54, 0.17, 0.04, 0.55, 0.05 };

		Set<Criterion> criteriaEx10 = new LinkedHashSet<>();

		for (int i1 = 0; i1 < w10.length; i1++) {
			criteriaEx10.add(new Criterion(i1 + 1, "c" + (i1 + 1), new ArrayList<Double>()));
		}

		Map<Criterion, Double> x_perfEx10 = new LinkedHashMap<>();
		Map<Criterion, Double> y_perfEx10 = new LinkedHashMap<>();
		Map<Criterion, Double> weightsEx10 = new LinkedHashMap<>();

		int ind = 0;
		for (Criterion c : criteriaEx10) {
			x_perfEx10.put(c, x10[ind]);
			y_perfEx10.put(c, y10[ind]);
			weightsEx10.put(c, w10[ind]);
			ind++;
		}

		Alternative x_10 = new Alternative(1, "X", x_perfEx10);
		Alternative y_10 = new Alternative(2, "Y", y_perfEx10);
		AlternativesComparison altsEx10 = new AlternativesComparison(x_10, y_10, weightsEx10);

		return new LabreucheModel(altsEx10);

	}

	public static LabreucheModel getExample13() { // NOA example

		Double[] x13 = { 0.50, 0.06, 0.03, 0.95, 0.87, 0.20, 0.95 };
		Double[] y13 = { 0.99, 0.35, 0.31, 0.51, 0.62, 0.57, 0.52 };
		Double[] w13 = { 0.06, 0.11, 0.19, 0.11, 0.31, 0.08, 0.14 };

		Set<Criterion> criteriaEx13 = new LinkedHashSet<>();

		for (int i1 = 0; i1 < w13.length; i1++) {
			criteriaEx13.add(new Criterion(i1 + 1, "c" + (i1 + 1), new ArrayList<Double>()));
		}

		Map<Criterion, Double> x_perfEx13 = new LinkedHashMap<>();
		Map<Criterion, Double> y_perfEx13 = new LinkedHashMap<>();
		Map<Criterion, Double> weightsEx13 = new LinkedHashMap<>();

		int ind = 0;
		for (Criterion c : criteriaEx13) {
			x_perfEx13.put(c, x13[ind]);
			y_perfEx13.put(c, y13[ind]);
			weightsEx13.put(c, w13[ind]);
			ind++;
		}

		Alternative x_13 = new Alternative(1, "X", x_perfEx13);
		Alternative y_13 = new Alternative(2, "Y", y_perfEx13);
		AlternativesComparison altsEx13 = new AlternativesComparison(x_13, y_13, weightsEx13);

		return new LabreucheModel(altsEx13);
	}

	public static LabreucheModel getExample14() { // NOA example

		Double[] x14 = { 0.50, 0.06, 0.03, 0.95, 0.87, 0.20, 0.95 };
		Double[] y14 = { 0.99, 0.35, 0.31, 0.51, 0.62, 0.57, 0.52 };
		Double[] w14 = { 0.14, 0.05, 0.17, 0.23, 0.17, 0.11, 0.13 };

		Set<Criterion> criteriaEx14 = new LinkedHashSet<>();

		for (int i1 = 0; i1 < w14.length; i1++) {
			criteriaEx14.add(new Criterion(i1 + 1, "c" + (i1 + 1), new ArrayList<Double>()));
		}

		Map<Criterion, Double> x_perfEx14 = new LinkedHashMap<>();
		Map<Criterion, Double> y_perfEx14 = new LinkedHashMap<>();
		Map<Criterion, Double> weightsEx14 = new LinkedHashMap<>();

		int ind = 0;
		for (Criterion c : criteriaEx14) {
			x_perfEx14.put(c, x14[ind]);
			y_perfEx14.put(c, y14[ind]);
			weightsEx14.put(c, w14[ind]);
			ind++;
		}

		Alternative x_14 = new Alternative(1, "X", x_perfEx14);
		Alternative y_14 = new Alternative(2, "Y", y_perfEx14);
		AlternativesComparison altsEx14 = new AlternativesComparison(x_14, y_14, weightsEx14);

		return new LabreucheModel(altsEx14);
	}

	public static LabreucheModel getExample15() { // IVT example
		Double[] x15 = { 0.50, 0.06, 0.03, 0.95, 0.87, 0.20, 0.95 };
		Double[] y15 = { 0.99, 0.35, 0.31, 0.51, 0.62, 0.57, 0.52 };
		Double[] w15 = { 0.11, 0.14, 0.13, 0.02, 0.27, 0.25, 0.08 };

		Set<Criterion> criteriaEx15 = new LinkedHashSet<>();

		for (int i1 = 0; i1 < w15.length; i1++) {
			criteriaEx15.add(new Criterion(i1 + 1, "c" + (i1 + 1), new ArrayList<Double>()));
		}

		Map<Criterion, Double> x_perfEx15 = new LinkedHashMap<>();
		Map<Criterion, Double> y_perfEx15 = new LinkedHashMap<>();
		Map<Criterion, Double> weightsEx15 = new LinkedHashMap<>();

		int ind = 0;
		for (Criterion c : criteriaEx15) {
			x_perfEx15.put(c, x15[ind]);
			y_perfEx15.put(c, y15[ind]);
			weightsEx15.put(c, w15[ind]);
			ind++;
		}

		Alternative x_15 = new Alternative(1, "X", x_perfEx15);
		Alternative y_15 = new Alternative(2, "Y", y_perfEx15);
		AlternativesComparison altsEx15 = new AlternativesComparison(x_15, y_15, weightsEx15);

		return new LabreucheModel(altsEx15);
	}

	public static LabreucheModel getExample16() { // IVT example

		Double[] x16 = { 0.50, 0.06, 0.03, 0.95, 0.87, 0.20, 0.95 };
		Double[] y16 = { 0.99, 0.35, 0.31, 0.51, 0.62, 0.57, 0.52 };
		Double[] w16 = { 0.24, 0.20, 0.25, 0.06, 0.02, 0.19, 0.04 };

		Set<Criterion> criteriaEx16 = new LinkedHashSet<>();

		for (int i1 = 0; i1 < w16.length; i1++) {
			criteriaEx16.add(new Criterion(i1 + 1, "c" + (i1 + 1), new ArrayList<Double>()));
		}

		Map<Criterion, Double> x_perfEx16 = new LinkedHashMap<>();
		Map<Criterion, Double> y_perfEx16 = new LinkedHashMap<>();
		Map<Criterion, Double> weightsEx16 = new LinkedHashMap<>();

		int ind = 0;
		for (Criterion c : criteriaEx16) {
			x_perfEx16.put(c, x16[ind]);
			y_perfEx16.put(c, y16[ind]);
			weightsEx16.put(c, w16[ind]);
			ind++;
		}

		Alternative x_16 = new Alternative(1, "X", x_perfEx16);
		Alternative y_16 = new Alternative(2, "Y", y_perfEx16);
		AlternativesComparison altsEx16 = new AlternativesComparison(x_16, y_16, weightsEx16);

		return new LabreucheModel(altsEx16);
	}

	public static LabreucheModel getExample17() { // RMGCOMP example

		Double[] x17 = { 0.50, 0.06, 0.03, 0.95, 0.87, 0.20, 0.95 };
		Double[] y17 = { 0.99, 0.35, 0.31, 0.51, 0.62, 0.57, 0.52 };
		Double[] w17 = { 0.16, 0.14, 0.15, 0.10, 0.16, 0.15, 0.14 };

		Set<Criterion> criteriaEx17 = new LinkedHashSet<>();

		for (int i1 = 0; i1 < w17.length; i1++) {
			criteriaEx17.add(new Criterion(i1 + 1, "c" + (i1 + 1), new ArrayList<Double>()));
		}

		Map<Criterion, Double> x_perfEx17 = new LinkedHashMap<>();
		Map<Criterion, Double> y_perfEx17 = new LinkedHashMap<>();
		Map<Criterion, Double> weightsEx17 = new LinkedHashMap<>();

		int ind = 0;
		for (Criterion c : criteriaEx17) {
			x_perfEx17.put(c, x17[ind]);
			y_perfEx17.put(c, y17[ind]);
			weightsEx17.put(c, w17[ind]);
			ind++;
		}

		Alternative x_17 = new Alternative(1, "X", x_perfEx17);
		Alternative y_17 = new Alternative(2, "Y", y_perfEx17);
		AlternativesComparison altsEx17 = new AlternativesComparison(x_17, y_17, weightsEx17);

		return new LabreucheModel(altsEx17);

	}

	public static LabreucheModel getExample18() { // RMGAVG example

		Double[] x18 = { 0.50, 0.06, 0.03, 0.95, 0.87, 0.20, 0.95 };
		Double[] y18 = { 0.99, 0.35, 0.31, 0.51, 0.62, 0.57, 0.52 };
		Double[] w18 = { 0.12, 0.16, 0.15, 0.16, 0.15, 0.14, 0.12 };

		Set<Criterion> criteriaEx18 = new LinkedHashSet<>();

		for (int i1 = 0; i1 < w18.length; i1++) {
			criteriaEx18.add(new Criterion(i1 + 1, "c" + (i1 + 1), new ArrayList<Double>()));
		}

		Map<Criterion, Double> x_perfEx18 = new LinkedHashMap<>();
		Map<Criterion, Double> y_perfEx18 = new LinkedHashMap<>();
		Map<Criterion, Double> weightsEx18 = new LinkedHashMap<>();

		int ind = 0;
		for (Criterion c : criteriaEx18) {
			x_perfEx18.put(c, x18[ind]);
			y_perfEx18.put(c, y18[ind]);
			weightsEx18.put(c, w18[ind]);
			ind++;
		}

		Alternative x_18 = new Alternative(1, "X", x_perfEx18);
		Alternative y_18 = new Alternative(2, "Y", y_perfEx18);
		AlternativesComparison altsEx18 = new AlternativesComparison(x_18, y_18, weightsEx18);

		return new LabreucheModel(altsEx18);

	}

	public static ALLOutput getExampleAllOutput() {
		LabreucheModel lm = getExampleAll();

		ALLOutput all = new ALLOutput(lm.getAlternativesComparison());

		return all;
	}

	public static NOAOutput getExample5Output() {
		LabreucheModel lm = getExample5();

		Builder<Criterion> resultShouldBe = new ImmutableSet.Builder<>();
		resultShouldBe.add(new Criterion(2, "c2", new ArrayList<Double>()));

		NOAOutput noa = new NOAOutput(lm.getAlternativesComparison(), resultShouldBe.build());

		return noa;
	}

	public static NOAOutput getExample6Output() {
		LabreucheModel lm = getExample6();

		Builder<Criterion> resultShouldBe = new ImmutableSet.Builder<>();
		resultShouldBe.add(new Criterion(2, "c2", new ArrayList<Double>()));
		resultShouldBe.add(new Criterion(3, "c3", new ArrayList<Double>()));
		resultShouldBe.add(new Criterion(4, "c4", new ArrayList<Double>()));
		resultShouldBe.add(new Criterion(5, "c5", new ArrayList<Double>()));

		NOAOutput noa = new NOAOutput(lm.getAlternativesComparison(), resultShouldBe.build());

		return noa;
	}

	public static IVTOutput getExample9Output() {
		LabreucheModel lm = getExample9();

		Criterion c1 = new Criterion(1, "c1", new ArrayList<Double>());
		Criterion c3 = new Criterion(3, "c5", new ArrayList<Double>());
		Criterion c5 = new Criterion(5, "c5", new ArrayList<Double>());

		MutableGraph<Criterion> graphShouldBe = GraphBuilder.directed().build();

		graphShouldBe.putEdge(c1, c3);
		graphShouldBe.putEdge(c1, c5);

		double epsilon = 0.2 / lm.getAlternativesComparison().getWeight().keySet().size();

		IVTOutput ivt = new IVTOutput(lm.getAlternativesComparison(), ImmutableGraph.copyOf(graphShouldBe), epsilon);

		return ivt;
	}

	public static List<List<Criterion>> getExample10Permutation() {

		List<List<Criterion>> permutationsExpected = new ArrayList<>();

		List<Criterion> perm1 = new ArrayList<>();
		List<Criterion> perm2 = new ArrayList<>();

		Criterion c6 = new Criterion(6, "c6", new ArrayList<Double>());
		Criterion c8 = new Criterion(8, "c8", new ArrayList<Double>());

		Criterion c3 = new Criterion(3, "c3", new ArrayList<Double>());
		Criterion c9 = new Criterion(9, "c9", new ArrayList<Double>());

		perm1.add(c6);
		perm1.add(c8);
		perm2.add(c3);
		perm2.add(c9);

		permutationsExpected.add(perm1);
		permutationsExpected.add(perm2);

		return permutationsExpected;
	}

	public static NOAOutput getExample13Output() {
		LabreucheModel lm = getExample13();

		Builder<Criterion> resultShouldBe = new ImmutableSet.Builder<>();
		resultShouldBe.add(new Criterion(1, "c1", new ArrayList<Double>()));
		resultShouldBe.add(new Criterion(5, "c5", new ArrayList<Double>()));

		NOAOutput noa = new NOAOutput(lm.getAlternativesComparison(), resultShouldBe.build());

		return noa;
	}

	public static NOAOutput getExample14Output() {
		LabreucheModel lm = getExample13();

		Builder<Criterion> resultShouldBe = new ImmutableSet.Builder<>();
		resultShouldBe.add(new Criterion(2, "c2", new ArrayList<Double>()));
		resultShouldBe.add(new Criterion(4, "c4", new ArrayList<Double>()));

		NOAOutput noa = new NOAOutput(lm.getAlternativesComparison(), resultShouldBe.build());

		return noa;
	}

	public static List<List<Criterion>> getExample15Permutation() {

		List<List<Criterion>> permutationsExpected = new ArrayList<>();

		List<Criterion> perm1 = new ArrayList<>();

		Criterion c4 = new Criterion(4, "c4", new ArrayList<Double>());
		Criterion c6 = new Criterion(6, "c6", new ArrayList<Double>());

		perm1.add(c4);
		perm1.add(c6);

		permutationsExpected.add(perm1);

		return permutationsExpected;
	}

	public static List<List<Criterion>> getExample16Permutation() {
		List<List<Criterion>> permutationsExpected = new ArrayList<>();

		List<Criterion> perm1 = new ArrayList<>();
		List<Criterion> perm2 = new ArrayList<>();

		Criterion c1 = new Criterion(1, "c1", new ArrayList<Double>());
		Criterion c7 = new Criterion(7, "c7", new ArrayList<Double>());

		Criterion c3 = new Criterion(3, "c3", new ArrayList<Double>());
		Criterion c4 = new Criterion(4, "c4", new ArrayList<Double>());

		perm1.add(c1);
		perm1.add(c7);
		perm2.add(c3);
		perm2.add(c4);

		permutationsExpected.add(perm1);
		permutationsExpected.add(perm2);

		return permutationsExpected;
	}

	public static RMGCOMPOutput getExample17Output() {
		LabreucheModel lm = getExample17();

		double epsilon = 0.2 / lm.getAlternativesComparison().getWeight().keySet().size();

		RMGCOMPOutput rmgcomp = new RMGCOMPOutput(lm.getAlternativesComparison(), epsilon);

		return rmgcomp;
	}

	public static RMGAVGOutput getExample18Output() {
		LabreucheModel lm = getExample18();

		RMGAVGOutput rmgavg = new RMGAVGOutput(lm.getAlternativesComparison());

		return rmgavg;
	}

}
