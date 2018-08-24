package io.github.oliviercailloux.decision.arguer.labreuche.output;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import com.google.common.graph.MutableGraph;

import io.github.oliviercailloux.decision.arguer.labreuche.AlternativesComparison;
import io.github.oliviercailloux.decision.arguer.labreuche.Couple;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

/**
 *
 * Immutable.
 *
 */
public class IVTOutput implements LabreucheOutput {

	private AlternativesComparison alternativesComparison;
	private ImmutableGraph<Criterion> rStar;
	private double epsilon;
	private ImmutableGraph<Criterion> kNRW;
	private ImmutableGraph<Criterion> kNW;
	private ImmutableGraph<Criterion> kPRS;
	private ImmutableGraph<Criterion> kPS;
	private ImmutableGraph<Criterion> kPN;

	/**
	 * @param rStar
	 *            may not be empty.
	 * @param epsilon
	 *            > 0.
	 */
	public IVTOutput(AlternativesComparison alternativesComparison, Graph<Criterion> rStar, double epsilon) {
		this.alternativesComparison = requireNonNull(alternativesComparison);
		this.rStar = ImmutableGraph.copyOf(requireNonNull(rStar));
		checkArgument(!rStar.edges().isEmpty());
		this.epsilon = requireNonNull(epsilon);
		checkArgument(Double.isFinite(epsilon));
		checkArgument(epsilon > 0);
		this.kNRW = null;
		this.kNW = null;
		this.kPRS = null;
		this.kPS = null;
		this.kPN = null;
	}

	@Override
	public Anchor getAnchor() {
		return Anchor.IVT;
	}

	@Override
	public AlternativesComparison getAlternativesComparison() {
		return this.alternativesComparison;
	}

	public ImmutableGraph<Criterion> getNegativeRelativelyWeak() {

		if (this.kNRW == null) {
			computeKset();
		}

		return kNRW;
	}

	public ImmutableGraph<Criterion> getNegativeWeak() {

		if (this.kNRW == null) {
			computeKset();
		}

		return kNW;
	}

	public ImmutableGraph<Criterion> getPositiveNegative() {

		if (this.kNRW == null) {
			computeKset();
		}

		return kPN;
	}

	public ImmutableGraph<Criterion> getPositiveRelativelyStrong() {

		if (this.kNRW == null) {
			computeKset();
		}

		return kPRS;
	}

	public ImmutableGraph<Criterion> getPositiveStrong() {

		if (this.kNRW == null) {
			computeKset();
		}

		return kPS;
	}

	public ImmutableGraph<Criterion> getRStar() {
		return this.rStar;
	}

	private void computeKset() {
		MutableGraph<Criterion> buildkPS = GraphBuilder.directed().build();
		MutableGraph<Criterion> buildkPRS = GraphBuilder.directed().build();
		MutableGraph<Criterion> buildkNW = GraphBuilder.directed().build();
		MutableGraph<Criterion> buildkNRW = GraphBuilder.directed().build();
		MutableGraph<Criterion> buildkPN = GraphBuilder.directed().build();

		ImmutableMap<Criterion, Double> w = alternativesComparison.getWeight();

		for (EndpointPair<Criterion> e : rStar.edges()) {
			Criterion i = e.nodeU();
			Criterion j = e.nodeV();
			double deltaI = alternativesComparison.getDelta().get(i);
			double deltaJ = alternativesComparison.getDelta().get(j);
			double wI = w.get(i);
			double wJ = w.get(j);
			int n = w.keySet().size();

			if (deltaJ > 0 && deltaI < 0) {
				if (wI <= 1.0 / n && 1.0 / n < wJ) {
					buildkPS.addNode(j);
					buildkNW.addNode(i);
				}
				if (isMuchSmaller(wI, wJ) && wJ < 1.0 / n) {
					buildkPN.putEdge(i,j);
				}
				if (1.0 / n < wI && isMuchSmaller(wI, wJ)) {
					buildkPN.putEdge(i,j);
				}
				if (isJustSmaller(wI, wJ) && wJ < 1.0 / n) {
					buildkNW.addNode(i);
				}
				if (1.0 / n < wI && isJustSmaller(wI, wJ)) {
					buildkPS.addNode(j);
				}
			}

			if (deltaJ > 0 && deltaI > 0) {
				if (wJ >= 1.0 / n) {
					buildkPS.addNode(j);
				}
				if (1.0 / n > wJ) {
					buildkPRS.addNode(j);
				}
			}

			if (deltaJ < 0 && deltaI < 0) {
				if (wI > 1.0 / n) {
					buildkNRW.addNode(i);
				}
				if (1.0 / n >= wI) {
					buildkNW.addNode(i);
				}
			}
		}

		kPS = ImmutableGraph.copyOf(buildkPS);
		kPRS = ImmutableGraph.copyOf(buildkPRS);
		kNW = ImmutableGraph.copyOf(buildkNW);
		kNRW = ImmutableGraph.copyOf(buildkNRW);
		kPN = ImmutableGraph.copyOf(buildkPN);
	}

	boolean isJustSmaller(double w1, double w2) {
		return w1 < w2 && w2 - w1 < epsilon;
	}

	boolean isMuchSmaller(double w1, double w2) {
		return w2 - w1 > epsilon;
	}

}
