package io.github.oliviercailloux.decision.arguer.labreuche;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import com.google.common.graph.MutableGraph;

import io.github.oliviercailloux.decision.arguer.AlternativesComparison;
import io.github.oliviercailloux.decision.arguer.AlternativesComparisonLabreucheBuilder;
import io.github.oliviercailloux.decision.arguer.labreuche.output.ALLOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.IVTOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.NOAOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.RMGAVGOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.RMGCOMPOutput;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class Examples {

	/**
	 * In the example X and Y are invert from the paper of Labreuche (2011).
	 */

	private Examples() {
		throw new IllegalStateException("Examples Class");
	}

	public static LabreucheComputer getExampleAll() {
		final AlternativesComparisonLabreucheBuilder builder = new AlternativesComparisonLabreucheBuilder();

		builder.setY(ImmutableList.of(0.20, 0.60, 0.60, 0.50));
		builder.setX(ImmutableList.of(0.54, 0.67, 0.89, 0.76));
		builder.setW(ImmutableList.of(0.54, 0.67, 0.89, 0.76));

		final AlternativesComparison<LabreucheModel> altsComp = builder.build();
		return new LabreucheComputer(altsComp);
	}

	public static LabreucheComputer getExample5() {
		final AlternativesComparisonLabreucheBuilder builder = new AlternativesComparisonLabreucheBuilder();

		builder.setY(ImmutableList.of(0.42, 0.66, 0.66, 0.57));
		builder.setX(ImmutableList.of(0.54, 0.04, 0.89, 0.76));
		builder.setW(ImmutableList.of(0.41, 0.06, 0.24, 0.29));

		final AlternativesComparison<LabreucheModel> altsComp = builder.build();
		return new LabreucheComputer(altsComp);
	}

	public static LabreucheComputer getExample6() {
		final AlternativesComparisonLabreucheBuilder builder = new AlternativesComparisonLabreucheBuilder();

		builder.setY(ImmutableList.of(0.95, 0.67, 0.64, 0.27, 0.39));
		builder.setX(ImmutableList.of(0.30, 0.37, 0.41, 0.94, 0.49));
		builder.setW(ImmutableList.of(0.18, 0.11, 0.12, 0.24, 0.35));

		final AlternativesComparison<LabreucheModel> altsComp = builder.build();
		return new LabreucheComputer(altsComp);
	}

	public static LabreucheComputer getExample9() {
		final AlternativesComparisonLabreucheBuilder builder = new AlternativesComparisonLabreucheBuilder();

		builder.setY(ImmutableList.of(0.89, 0.03, 0.07, 0.32, 0.38));
		builder.setX(ImmutableList.of(0.36, 0.76, 0.60, 0.25, 0.75));
		builder.setW(ImmutableList.of(0.06, 0.11, 0.21, 0.29, 0.33));

		final AlternativesComparison<LabreucheModel> altsComp = builder.build();
		return new LabreucheComputer(altsComp);
	}

	public static LabreucheComputer getExample10() {
		final AlternativesComparisonLabreucheBuilder builder = new AlternativesComparisonLabreucheBuilder();

		builder.setY(ImmutableList.of(0.61, 0.28, 0.08, 0.02, 0.81, 0.15, 0.16, 0.38, 0.24, 0.75));
		builder.setX(ImmutableList.of(0.45, 0.64, 0.86, 0.76, 0.87, 0.54, 0.17, 0.04, 0.55, 0.05));
		builder.setW(ImmutableList.of(0.13, 0.04, 0.12, 0.10, 0.07, 0.19, 0.15, 0.03, 0.01, 0.16));

		final AlternativesComparison<LabreucheModel> altsComp = builder.build();
		return new LabreucheComputer(altsComp);
	}

	public static LabreucheComputer getExample13() {
		final AlternativesComparisonLabreucheBuilder builder = new AlternativesComparisonLabreucheBuilder();

		builder.setX(ImmutableList.of(0.50, 0.06, 0.03, 0.95, 0.87, 0.20, 0.95));
		builder.setY(ImmutableList.of(0.99, 0.35, 0.31, 0.51, 0.62, 0.57, 0.52));
		builder.setW(ImmutableList.of(0.06, 0.11, 0.19, 0.11, 0.31, 0.08, 0.14));

		final AlternativesComparison<LabreucheModel> altsComp = builder.build();
		return new LabreucheComputer(altsComp);
	}

	public static LabreucheComputer getExample14() {
		final AlternativesComparisonLabreucheBuilder builder = new AlternativesComparisonLabreucheBuilder();

		builder.setX(ImmutableList.of(0.50, 0.06, 0.03, 0.95, 0.87, 0.20, 0.95));
		builder.setY(ImmutableList.of(0.99, 0.35, 0.31, 0.51, 0.62, 0.57, 0.52));
		builder.setW(ImmutableList.of(0.14, 0.05, 0.17, 0.23, 0.17, 0.11, 0.13));

		final AlternativesComparison<LabreucheModel> altsComp = builder.build();
		return new LabreucheComputer(altsComp);
	}

	public static LabreucheComputer getExample15() {
		final AlternativesComparisonLabreucheBuilder builder = new AlternativesComparisonLabreucheBuilder();

		builder.setY(ImmutableList.of(0.50, 0.06, 0.03, 0.95, 0.87, 0.20, 0.95));
		builder.setX(ImmutableList.of(0.99, 0.35, 0.31, 0.51, 0.62, 0.57, 0.52));
		builder.setW(ImmutableList.of(0.11, 0.14, 0.13, 0.02, 0.27, 0.25, 0.08));

		final AlternativesComparison<LabreucheModel> altsComp = builder.build();
		return new LabreucheComputer(altsComp);
	}

	public static LabreucheComputer getExample16() {
		final AlternativesComparisonLabreucheBuilder builder = new AlternativesComparisonLabreucheBuilder();

		builder.setY(ImmutableList.of(0.50, 0.06, 0.03, 0.95, 0.87, 0.20, 0.95));
		builder.setX(ImmutableList.of(0.99, 0.35, 0.31, 0.51, 0.62, 0.57, 0.52));
		builder.setW(ImmutableList.of(0.24, 0.20, 0.25, 0.06, 0.02, 0.19, 0.04));

		final AlternativesComparison<LabreucheModel> altsComp = builder.build();
		return new LabreucheComputer(altsComp);
	}

	public static LabreucheComputer getExample17() {
		final AlternativesComparisonLabreucheBuilder builder = new AlternativesComparisonLabreucheBuilder();

		builder.setY(ImmutableList.of(0.50, 0.06, 0.03, 0.95, 0.87, 0.20, 0.95));
		builder.setX(ImmutableList.of(0.99, 0.35, 0.31, 0.51, 0.62, 0.57, 0.52));
		builder.setW(ImmutableList.of(0.16, 0.14, 0.15, 0.10, 0.16, 0.15, 0.14));

		final AlternativesComparison<LabreucheModel> altsComp = builder.build();
		return new LabreucheComputer(altsComp);
	}

	public static LabreucheComputer getExample18() {
		final AlternativesComparisonLabreucheBuilder builder = new AlternativesComparisonLabreucheBuilder();

		builder.setY(ImmutableList.of(0.50, 0.06, 0.03, 0.95, 0.87, 0.20, 0.95));
		builder.setX(ImmutableList.of(0.99, 0.35, 0.31, 0.51, 0.62, 0.57, 0.52));
		builder.setW(ImmutableList.of(0.12, 0.16, 0.15, 0.16, 0.15, 0.14, 0.12));

		final AlternativesComparison<LabreucheModel> altsComp = builder.build();
		return new LabreucheComputer(altsComp);
	}

	public static ALLOutput getExampleAllOutput() {
		LabreucheComputer lm = getExampleAll();

		return new ALLOutput(lm.getAlternativesComparison());
	}

	public static NOAOutput getExample5Output() {
		LabreucheComputer lm = getExample5();

		Builder<Criterion> resultShouldBe = new ImmutableSet.Builder<>();
		resultShouldBe.add(new Criterion(2, "c2", new ArrayList<Double>()));

		return new NOAOutput(lm.getAlternativesComparison(), resultShouldBe.build());
	}

	public static NOAOutput getExample6Output() {
		LabreucheComputer lm = getExample6();

		Builder<Criterion> resultShouldBe = new ImmutableSet.Builder<>();
		resultShouldBe.add(new Criterion(2, "c2", new ArrayList<Double>()));
		resultShouldBe.add(new Criterion(3, "c3", new ArrayList<Double>()));
		resultShouldBe.add(new Criterion(4, "c4", new ArrayList<Double>()));
		resultShouldBe.add(new Criterion(5, "c5", new ArrayList<Double>()));

		return new NOAOutput(lm.getAlternativesComparison(), resultShouldBe.build());
	}

	public static IVTOutput getExample9Output() {
		LabreucheComputer lm = getExample9();

		Criterion c1 = new Criterion(1, "c1", new ArrayList<Double>());
		Criterion c3 = new Criterion(3, "c3", new ArrayList<Double>());
		Criterion c5 = new Criterion(5, "c5", new ArrayList<Double>());

		MutableGraph<Criterion> graphShouldBe = GraphBuilder.directed().build();

		graphShouldBe.putEdge(c1, c3);
		graphShouldBe.putEdge(c1, c5);

		return new IVTOutput(lm.getAlternativesComparison(), ImmutableGraph.copyOf(graphShouldBe));
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
		LabreucheComputer lm = getExample13();

		Builder<Criterion> resultShouldBe = new ImmutableSet.Builder<>();
		resultShouldBe.add(new Criterion(1, "c1", new ArrayList<Double>()));
		resultShouldBe.add(new Criterion(5, "c5", new ArrayList<Double>()));

		return new NOAOutput(lm.getAlternativesComparison(), resultShouldBe.build());
	}

	public static NOAOutput getExample14Output() {
		LabreucheComputer lm = getExample14();

		Builder<Criterion> resultShouldBe = new ImmutableSet.Builder<>();
		resultShouldBe.add(new Criterion(2, "c2", new ArrayList<Double>()));
		resultShouldBe.add(new Criterion(4, "c4", new ArrayList<Double>()));

		return new NOAOutput(lm.getAlternativesComparison(), resultShouldBe.build());
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
		LabreucheComputer lm = getExample17();

		return new RMGCOMPOutput(lm.getAlternativesComparison());
	}

	public static RMGAVGOutput getExample18Output() {
		LabreucheComputer lm = getExample18();

		return new RMGAVGOutput(lm.getAlternativesComparison());
	}

}
