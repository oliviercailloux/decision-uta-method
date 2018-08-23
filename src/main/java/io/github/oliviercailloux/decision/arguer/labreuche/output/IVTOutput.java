package io.github.oliviercailloux.decision.arguer.labreuche.output;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import io.github.oliviercailloux.decision.arguer.labreuche.AlternativesComparison;
import io.github.oliviercailloux.decision.arguer.labreuche.Couple;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class IVTOutput implements LabreucheOutput {

	private AlternativesComparison alternativesComparison;
	private ImmutableSet<Couple<Criterion, Criterion>> r_star;
	private double epsilon;
	private Set<Criterion> k_nrw;
	private Set<Criterion> k_nw;
	private Set<Criterion> k_prs;
	private Set<Criterion> k_ps;
	private Set<Couple<Criterion, Criterion>> k_pn;
	private boolean firstcall;

	/**
	 * @param r_star2 may not be empty.
	 * @param epsilon > 0.
	 */
	public IVTOutput(AlternativesComparison alternativesComparison, List<Couple<Criterion, Criterion>> r_star2,
			double epsilon) {
		this.alternativesComparison = requireNonNull(alternativesComparison);
		this.r_star = ImmutableSet.copyOf(requireNonNull(r_star2));
		checkArgument(!r_star2.isEmpty());
		this.epsilon = requireNonNull(epsilon);
		checkArgument(Double.isFinite(epsilon));
		checkArgument(epsilon > 0);
		this.firstcall = false;
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

	public Set<Criterion> getK_nrw() {

		if (!this.firstcall) {
			buildKset();
			this.firstcall = true;
		}

		return k_nrw;
	}

	public Set<Criterion> getK_nw() {

		if (!this.firstcall) {
			buildKset();
			this.firstcall = true;
		}
		return k_nw;
	}

	public Set<Couple<Criterion, Criterion>> getK_pn() {

		if (!this.firstcall) {
			buildKset();
			this.firstcall = true;
		}

		return k_pn;
	}

	public Set<Criterion> getK_prs() {

		if (!this.firstcall) {
			buildKset();
			this.firstcall = true;
		}

		return k_prs;
	}

	public Set<Criterion> getK_ps() {

		if (!this.firstcall) {
			buildKset();
			this.firstcall = true;
		}

		return k_ps;
	}

	public Set<Couple<Criterion, Criterion>> getR_star() {
		return this.r_star;
	}

	private void buildKset() {
		for (Couple<Criterion, Criterion> c : r_star) { // c = (i,j) where i and j are criteria

			// j in S+ and i in S- and w_j > w_i
			if (this.alternativesComparison.getPositiveCriteria().contains(c.getRight())
					&& this.alternativesComparison.getNegativeCriteria().contains(c.getLeft())
					&& this.alternativesComparison.getWeight().get(c.getRight()) > this.alternativesComparison
							.getWeight().get(c.getLeft())) {

				// w_j >= 1/n and w_i <= 1/n
				if (this.alternativesComparison.getWeight()
						.get(c.getRight()) >= (1.0 / this.alternativesComparison.getCriteria().size())
						&& this.alternativesComparison.getWeight()
								.get(c.getLeft()) <= (1.0 / this.alternativesComparison.getCriteria().size())) {
					k_ps.add(c.getRight()); // adding j
					k_nw.add(c.getLeft()); // adding i
				} else {
					// w_j >> w_i
					if (this.alternativesComparison.getWeight().get(
							c.getRight()) > this.alternativesComparison.getWeight().get(c.getLeft()) + this.epsilon) {
						k_pn.add(c); // adding (i,j)
					} else {
						// w_j >= 1/n
						if (this.alternativesComparison.getWeight()
								.get(c.getRight()) >= (1.0 / this.alternativesComparison.getCriteria().size())) {
							k_ps.add(c.getRight()); // adding j
						} else {
							// w_i <= 1/n
							if (this.alternativesComparison.getWeight()
									.get(c.getLeft()) <= (1.0 / this.alternativesComparison.getCriteria().size())) {
								k_nw.add(c.getLeft()); // adding i
							}
						}
					}
				}
			}

			// i and j in S+ and delta_j > delta_i and w_j > w_i
			if (this.alternativesComparison.getPositiveCriteria().contains(c.getLeft())
					&& this.alternativesComparison.getPositiveCriteria().contains(c.getRight())
					&& this.alternativesComparison.getDelta().get(c.getRight()) > this.alternativesComparison.getDelta()
							.get(c.getLeft())
					&& this.alternativesComparison.getWeight().get(c.getRight()) > this.alternativesComparison
							.getWeight().get(c.getLeft())) {

				// w_j >= 1/n
				if (this.alternativesComparison.getWeight()
						.get(c.getRight()) >= (1.0 / this.alternativesComparison.getCriteria().size())) {
					k_ps.add(c.getRight()); // adding j
				} else {
					k_prs.add(c.getRight()); // adding j
				}
			}

			if (this.alternativesComparison.getNegativeCriteria().contains(c.getLeft())
					&& this.alternativesComparison.getNegativeCriteria().contains(c.getRight())
					&& this.alternativesComparison.getDelta().get(c.getRight()) > this.alternativesComparison.getDelta()
							.get(c.getLeft())
					&& this.alternativesComparison.getWeight().get(c.getRight()) > this.alternativesComparison
							.getWeight().get(c.getLeft())) {

				// w_j <= 1/n
				if (this.alternativesComparison.getWeight()
						.get(c.getRight()) <= (1.0 / this.alternativesComparison.getCriteria().size())) {
					k_nw.add(c.getRight()); // adding j
				} else {
					k_nrw.add(c.getRight()); // adding j
				}
			}
		}
	}

	boolean isMuchSmaller(double w1, double w2) {
		return w2 - w1 > epsilon;
	}

}
