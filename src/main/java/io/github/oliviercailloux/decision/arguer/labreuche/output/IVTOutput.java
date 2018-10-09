package io.github.oliviercailloux.decision.arguer.labreuche.output;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.Objects;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.Graph;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import com.google.common.graph.MutableGraph;

import io.github.oliviercailloux.decision.arguer.AlternativesComparison;
import io.github.oliviercailloux.decision.arguer.labreuche.LabreucheModel;
import io.github.oliviercailloux.decision.model.Criterion;

/**
 * Immutable.
 */
public class IVTOutput implements LabreucheOutput {

	private AlternativesComparison<LabreucheModel> alternativesComparison;
	private ImmutableGraph<Criterion> rStar;
	private ImmutableSet<Criterion> kNRW;
	private ImmutableSet<Criterion> kNW;
	private ImmutableSet<Criterion> kPRS;
	private ImmutableSet<Criterion> kPS;
	private ImmutableGraph<Criterion> kPN;

	/**
	 * @param rStar
	 *            may not be empty.
	 * @param epsilon
	 *            > 0.
	 */
	public IVTOutput(AlternativesComparison<LabreucheModel> alternativesComparison, Graph<Criterion> rStar) {
		this.alternativesComparison = requireNonNull(alternativesComparison);
		this.rStar = ImmutableGraph.copyOf(requireNonNull(rStar));
		checkArgument(!rStar.edges().isEmpty());
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
	public AlternativesComparison<LabreucheModel> getAlternativesComparison() {
		return this.alternativesComparison;
	}

	public ImmutableSet<Criterion> getNegativeRelativelyWeak() {

		if (this.kNRW == null) {
			computeKset();
		}

		return kNRW;
	}

	public ImmutableSet<Criterion> getNegativeWeak() {

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

	public ImmutableSet<Criterion> getPositiveRelativelyStrong() {

		if (this.kNRW == null) {
			computeKset();
		}

		return kPRS;
	}

	public ImmutableSet<Criterion> getPositiveStrong() {

		if (this.kNRW == null) {
			computeKset();
		}

		return kPS;
	}

	public ImmutableGraph<Criterion> getRStar() {
		return this.rStar;
	}

	private void computeKset() {
		Builder<Criterion> buildkPS = ImmutableSet.builder();
		Builder<Criterion> buildkPRS = ImmutableSet.builder();
		Builder<Criterion> buildkNW = ImmutableSet.builder();
		Builder<Criterion> buildkNRW = ImmutableSet.builder();
		MutableGraph<Criterion> buildkPN = GraphBuilder.directed().build();

		ImmutableMap<Criterion, Double> w = alternativesComparison.getPreferenceModel().getWeights();

		for (EndpointPair<Criterion> e : rStar.edges()) {
			Criterion i = e.nodeU();
			Criterion j = e.nodeV();
			double deltaI = alternativesComparison.getPreferenceModel()
					.getDelta(alternativesComparison.getX(), alternativesComparison.getY()).get(i);
			double deltaJ = alternativesComparison.getPreferenceModel()
					.getDelta(alternativesComparison.getX(), alternativesComparison.getY()).get(j);
			double wI = w.get(i);
			double wJ = w.get(j);
			int n = w.keySet().size();

			if (deltaJ > 0 && deltaI < 0) {
				if (wI <= 1.0 / n && 1.0 / n < wJ) {
					buildkPS.add(j);
					buildkNW.add(i);
				}
				if (isMuchSmaller(wI, wJ) && wJ < 1.0 / n) {
					buildkPN.putEdge(i, j);
				}
				if (1.0 / n < wI && isMuchSmaller(wI, wJ)) {
					buildkPN.putEdge(i, j);
				}
				if (isJustSmaller(wI, wJ) && wJ < 1.0 / n) {
					buildkNW.add(i);
				}
				if (1.0 / n < wI && isJustSmaller(wI, wJ)) {
					buildkPS.add(j);
				}
			}

			if (deltaJ > 0 && deltaI > 0) {
				if (wJ >= 1.0 / n) {
					buildkPS.add(j);
				}
				if (1.0 / n > wJ) {
					buildkPRS.add(j);
				}
			}

			if (deltaJ < 0 && deltaI < 0) {
				if (wI > 1.0 / n) {
					buildkNRW.add(i);
				}
				if (1.0 / n >= wI) {
					buildkNW.add(i);
				}
			}
		}

		kPS = buildkPS.build();
		kPRS = buildkPRS.build();
		kNW = buildkNW.build();
		kNRW = buildkNRW.build();
		kPN = ImmutableGraph.copyOf(buildkPN);
	}

	boolean isJustSmaller(double w1, double w2) {
		return w1 < w2 && w2 - w1 < alternativesComparison.getPreferenceModel().getEpsilon();
	}

	boolean isMuchSmaller(double w1, double w2) {
		return w2 - w1 > alternativesComparison.getPreferenceModel().getEpsilon();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		IVTOutput other = (IVTOutput) obj;

		assert other.getAnchor() == getAnchor();

		if (!other.getAlternativesComparison().equals(alternativesComparison))
			return false;

		return other.getRStar().equals(rStar);
	}

	@Override
	public int hashCode() {
		return Objects.hash(alternativesComparison, getAnchor(), rStar);
	}

}
