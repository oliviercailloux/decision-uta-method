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
import com.google.common.graph.ImmutableGraph;

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
	private ImmutableSet<Criterion> kNRW;
	private ImmutableSet<Criterion> kNW;
	private ImmutableSet<Criterion> kPRS;
	private ImmutableSet<Criterion> kPS;
	private ImmutableSet<Couple<Criterion, Criterion>> kPN;

	/**
	 * @param rStar2
	 *            may not be empty.
	 * @param epsilon
	 *            > 0.
	 */
	public IVTOutput(AlternativesComparison alternativesComparison, ImmutableGraph<Criterion> rStar2, double epsilon) {
		this.alternativesComparison = requireNonNull(alternativesComparison);
		this.rStar = requireNonNull(rStar2);
		checkArgument(!rStar2.edges().isEmpty());
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

	boolean isJustSmaller(double w1, double w2) {
		return w1 < w2 && w2 - w1 < epsilon;
	}

	@Override
	public AlternativesComparison getAlternativesComparison() {
		return this.alternativesComparison;
	}

	public Set<Criterion> getkNRW() {

		if (this.kNRW == null) {
			computeKset();
		}

		return kNRW;
	}

	public Set<Criterion> getkNW() {

		if (this.kNRW == null) {
			computeKset();
		}

		return kNW;
	}

	public Set<Couple<Criterion, Criterion>> getkPN() {

		if (this.kNRW == null) {
			computeKset();
		}

		return kPN;
	}

	public Set<Criterion> getkPRS() {

		if (this.kNRW == null) {
			computeKset();
		}

		return kPRS;
	}

	public Set<Criterion> getkPS() {

		if (this.kNRW == null) {
			computeKset();
		}

		return kPS;
	}

	public ImmutableGraph<Criterion> getrStar() {
		return this.rStar;
	}

	private void computeKset() {
		Builder<Criterion> buildkPS = new Builder<>();
		Builder<Criterion> buildkPRS = new Builder<>();
		Builder<Criterion> buildkNW = new Builder<>();
		Builder<Criterion> buildkNRW = new Builder<>();
		Builder<Couple<Criterion, Criterion>> buildkPN = new Builder<>();

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
					buildkPS.add(j);
					buildkNW.add(i);
				}
				if (isMuchSmaller(wI, wJ) && wJ < 1.0 / n) {
					Couple<Criterion, Criterion> c = new Couple<>(i, j);
					buildkPN.add(c);
				}
				if (1.0 / n < wI && isMuchSmaller(wI, wJ)) {
					Couple<Criterion, Criterion> c = new Couple<>(i, j);
					buildkPN.add(c);
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
		kPN = buildkPN.build();
	}

	@SuppressWarnings({ "unused", "deprecation" })
	private void buildKset() {
		for (EndpointPair<Criterion> c : rStar.edges()) { // c = (i,j) where i and j are criteria

			// j in S+ and i in S- and w_j > w_i
			if (this.alternativesComparison.getPositiveCriteria().contains(c.nodeV())
					&& this.alternativesComparison.getNegativeCriteria().contains(c.nodeU())
					&& this.alternativesComparison.getWeight().get(c.nodeV()) > this.alternativesComparison.getWeight()
							.get(c.nodeU())) {

				// w_j >= 1/n and w_i <= 1/n
				if (this.alternativesComparison.getWeight()
						.get(c.nodeV()) >= (1.0 / this.alternativesComparison.getCriteria().size())
						&& this.alternativesComparison.getWeight()
								.get(c.nodeU()) <= (1.0 / this.alternativesComparison.getCriteria().size())) {
					kPS.add(c.nodeV()); // adding j
					kNW.add(c.nodeU()); // adding i
				} else {
					// w_j >> w_i
					if (this.alternativesComparison.getWeight()
							.get(c.nodeV()) > this.alternativesComparison.getWeight().get(c.nodeU()) + this.epsilon) {
						Couple<Criterion, Criterion> cp = new Couple<>(c.nodeU(), c.nodeV());
						kPN.add(cp); // adding (i,j)
					} else {
						// w_j >= 1/n
						if (this.alternativesComparison.getWeight()
								.get(c.nodeV()) >= (1.0 / this.alternativesComparison.getCriteria().size())) {
							kPS.add(c.nodeV()); // adding j
						} else {
							// w_i <= 1/n
							if (this.alternativesComparison.getWeight()
									.get(c.nodeU()) <= (1.0 / this.alternativesComparison.getCriteria().size())) {
								kNW.add(c.nodeU()); // adding i
							}
						}
					}
				}
			}

			// i and j in S+ and delta_j > delta_i and w_j > w_i
			if (this.alternativesComparison.getPositiveCriteria().contains(c.nodeU())
					&& this.alternativesComparison.getPositiveCriteria().contains(c.nodeV())
					&& this.alternativesComparison.getDelta().get(c.nodeV()) > this.alternativesComparison.getDelta()
							.get(c.nodeU())
					&& this.alternativesComparison.getWeight().get(c.nodeV()) > this.alternativesComparison.getWeight()
							.get(c.nodeU())) {

				// w_j >= 1/n
				if (this.alternativesComparison.getWeight()
						.get(c.nodeV()) >= (1.0 / this.alternativesComparison.getCriteria().size())) {
					kPS.add(c.nodeV()); // adding j
				} else {
					kPRS.add(c.nodeV()); // adding j
				}
			}

			if (this.alternativesComparison.getNegativeCriteria().contains(c.nodeU())
					&& this.alternativesComparison.getNegativeCriteria().contains(c.nodeV())
					&& this.alternativesComparison.getDelta().get(c.nodeV()) > this.alternativesComparison.getDelta()
							.get(c.nodeU())
					&& this.alternativesComparison.getWeight().get(c.nodeV()) > this.alternativesComparison.getWeight()
							.get(c.nodeU())) {

				// w_j <= 1/n
				if (this.alternativesComparison.getWeight()
						.get(c.nodeV()) <= (1.0 / this.alternativesComparison.getCriteria().size())) {
					kNW.add(c.nodeV()); // adding j
				} else {
					kNRW.add(c.nodeV()); // adding j
				}
			}
		}
	}

	boolean isMuchSmaller(double w1, double w2) {
		return w2 - w1 > epsilon;
	}

}
