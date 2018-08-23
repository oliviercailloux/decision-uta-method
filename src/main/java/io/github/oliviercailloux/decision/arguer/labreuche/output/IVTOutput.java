package io.github.oliviercailloux.decision.arguer.labreuche.output;

import static java.util.Objects.requireNonNull;

import java.util.List;

import io.github.oliviercailloux.decision.arguer.labreuche.AlternativesComparison;
import io.github.oliviercailloux.decision.arguer.labreuche.Couple;
import io.github.oliviercailloux.decision.arguer.labreuche.Tools;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class IVTOutput implements LabreucheOutput {

	private AlternativesComparison alternativesComparison;
	private List<Couple<Criterion, Criterion>> r_star;
	private Double epsilon;
	
	private List<Criterion> k_nrw;
	private List<Criterion> k_nw;
	private List<Criterion> k_prs;
	private List<Criterion> k_ps;
	private List<Couple<Criterion, Criterion>> k_pn;

	
	private boolean firstcall;


	public IVTOutput(AlternativesComparison alternativesComparison, List<Couple<Criterion,Criterion>> r_star, Double epsilon) {
		this.alternativesComparison = requireNonNull(alternativesComparison);
		this.r_star = requireNonNull(r_star);
		this.epsilon = requireNonNull(epsilon);
		this.firstcall = false;
	}
	
	
	@Override
	public Anchor getAnchor() {
		return Anchor.IVT;
	}
	
	
	@Override
	public AlternativesComparison getAlternativesComparison() {
		return this.alternativesComparison;
	}
	
	
	public List<Criterion> getK_nrw() {
		
		if(!this.firstcall) {
			buildKset();
			this.firstcall = true;
		}
	
		return k_nrw;
	}

	
	public List<Criterion> getK_nw() {
		
		if(!this.firstcall) {
			buildKset();
			this.firstcall = true;
		}
		return k_nw;
	}

	
	public List<Couple<Criterion, Criterion>> getK_pn() {
		
		if(!this.firstcall) {
			buildKset();
			this.firstcall = true;
		}
		
		return k_pn;
	}

	
	public List<Criterion> getK_prs() {
		
		if(!this.firstcall) {
			buildKset();
			this.firstcall = true;
		}
		
		return k_prs;
	}

	
	public List<Criterion> getK_ps() {
		
		if(!this.firstcall) {
			buildKset();
			this.firstcall = true;
		}
		
		return k_ps;
	}

	
	public List<Couple<Criterion, Criterion>> getR_star() {
		return this.r_star;
	}

	private void buildKset() {
		for (Couple<Criterion, Criterion> c : r_star) { // c = (i,j) where i and j are criteria

			// j in S+ and i in S- and w_j > w_i
			if (this.alternativesComparison.getCriteriaInFavor().contains(c.getRight()) && this.alternativesComparison.getCriteriaAgainst().contains(c.getLeft())
					&& this.alternativesComparison.getWeight().get(c.getRight()) > this.alternativesComparison.getWeight().get(c.getLeft())) {

				// w_j >= 1/n and w_i <= 1/n
				if (this.alternativesComparison.getWeight().get(c.getRight()) >= (1.0 / this.alternativesComparison.getCriteria().size())
						&& this.alternativesComparison.getWeight().get(c.getLeft()) <= (1.0 / this.alternativesComparison.getCriteria().size())) {
					if (!k_ps.contains(c.getRight())) {
						k_ps.add(c.getRight()); // adding j
					}
					if (!k_nw.contains(c.getLeft())) {
						k_nw.add(c.getLeft()); // adding i
					}
				} else {
					// w_j >> w_i
					if (this.alternativesComparison.getWeight().get(c.getRight()) > this.alternativesComparison.getWeight().get(c.getLeft()) + this.epsilon) {
						if (!k_pn.contains(c)) {
							k_pn.add(c); // adding (i,j)
						}
					} else {
						// w_j >= 1/n
						if (this.alternativesComparison.getWeight().get(c.getRight()) >= (1.0 / this.alternativesComparison.getCriteria().size())) {
							if (!k_ps.contains(c.getRight())) {
								k_ps.add(c.getRight()); // adding j
							}
						} else {
							// w_i <= 1/n
							if (this.alternativesComparison.getWeight().get(c.getLeft()) <= (1.0 / this.alternativesComparison.getCriteria().size())) {
								if (!k_nw.contains(c.getLeft())) {
									k_nw.add(c.getLeft()); // adding i
								}
							}
						}
					}
				}
			}

			// i and j in S+ and delta_j > delta_i and w_j > w_i
			if (this.alternativesComparison.getCriteriaInFavor().contains(c.getLeft()) && this.alternativesComparison.getCriteriaInFavor().contains(c.getRight())
					&& this.alternativesComparison.getDelta().get(c.getRight()) > this.alternativesComparison.getDelta().get(c.getLeft())
					&& this.alternativesComparison.getWeight().get(c.getRight()) > this.alternativesComparison.getWeight().get(c.getLeft())) {

				// w_j >= 1/n
				if (this.alternativesComparison.getWeight().get(c.getRight()) >= (1.0 / this.alternativesComparison.getCriteria().size())) {
					if (!k_ps.contains(c.getRight())) {
						k_ps.add(c.getRight()); // adding j
					}
				} else {
					if (!k_prs.contains(c.getRight())) {
						k_prs.add(c.getRight()); // adding j
					}
				}
			}

			if (this.alternativesComparison.getCriteriaAgainst().contains(c.getLeft()) && this.alternativesComparison.getCriteriaAgainst().contains(c.getRight())
					&& this.alternativesComparison.getDelta().get(c.getRight()) > this.alternativesComparison.getDelta().get(c.getLeft())
					&& this.alternativesComparison.getWeight().get(c.getRight()) > this.alternativesComparison.getWeight().get(c.getLeft())) {

				// w_j <= 1/n
				if (this.alternativesComparison.getWeight().get(c.getRight()) <= (1.0 / this.alternativesComparison.getCriteria().size())) {
					if (!k_nw.contains(c.getRight())) {
						k_nw.add(c.getRight()); // adding j
					}
				} else {
					if (!k_nrw.contains(c.getRight())) {
						k_nrw.add(c.getRight()); // adding j
					}
				}
			}
		}
		
		
		
	}

	
	public String argue() {
		
		System.out.println("R* : " + Tools.showCouples(r_star) + "\n");

		if (!k_ps.isEmpty()) {
			System.out.print("K_ps = " + Tools.showCriteria(k_ps) + "  ");
		}

		if (!k_prs.isEmpty()) {
			System.out.print("K_prs = " + Tools.showCriteria(k_prs) + "  ");
		}

		if (!k_nw.isEmpty()) {
			System.out.print("K_nw = " + Tools.showCriteria(k_nw) + "  ");
		}

		if (!k_nrw.isEmpty()) {
			System.out.print("K_nrw = " + Tools.showCriteria(k_nrw) + "  ");
		}

		if (!k_pn.isEmpty()) {
			System.out.print("K_pn = " + Tools.showCouples(k_pn) + " ");
		}

		System.out.println("\n");

		

		return "";
	}

	

}
