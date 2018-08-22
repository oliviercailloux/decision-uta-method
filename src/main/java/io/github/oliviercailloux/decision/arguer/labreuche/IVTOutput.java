package io.github.oliviercailloux.decision.arguer.labreuche;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class IVTOutput implements LabreucheOutput {

	public List<Alternative> alternatives;
	public Alternative best_choice;
	public List<List<Criterion>> c_set;
	public List<Criterion> criteria;
	public Map<Criterion, Double> deltas;
	public Double epsilon;
	public List<Criterion> k_nrw;
	public List<Criterion> k_nw;
	public List<Couple<Criterion, Criterion>> k_pn;
	public List<Criterion> k_prs;
	public List<Criterion> k_ps;
	public Set<Criterion> negativeArguments;
	public Set<Criterion> nullArguments;
	public Set<Criterion> positiveArguments;
	public List<Couple<Criterion, Criterion>> r_star;
	public Alternative second_choice;
	public Map<Criterion, Double> weights;
	private Anchor anchor;

	public IVTOutput(AlternativesComparison alt) {
		this.best_choice = alt.getX();
		this.second_choice = alt.getY();
		this.criteria = new ArrayList<>();
		for (Criterion c : alt.getWeight().keySet()) {
			this.criteria.add(c);
		}

		this.weights = alt.getWeight();
		this.deltas = alt.getDelta();
		this.positiveArguments = alt.getCriteriaInFavor();
		this.negativeArguments = alt.getCriteriaAgainst();
		this.nullArguments = new HashSet<>(criteria);
		this.nullArguments.removeAll(positiveArguments);
		this.nullArguments.removeAll(negativeArguments);
		this.epsilon = 0.2 / this.criteria.size();
		this.c_set = new ArrayList<>();
		this.k_ps = new ArrayList<>();
		this.k_prs = new ArrayList<>();
		this.k_nw = new ArrayList<>();
		this.k_nrw = new ArrayList<>();
		this.k_pn = new ArrayList<>();
		this.r_star = null;
		this.anchor = Anchor.IVT;
	}

	// return the minimal set of permutation that inverse the decison between x and
	// y.
	public List<List<Criterion>> algo_EU(List<List<Criterion>> a, List<List<Criterion>> b, int k) {
		System.out.println(" \n #### Calling ALGO_EU : " + Tools.showSet(a) + "  and   k = " + k);

		List<List<Criterion>> a_copy = new ArrayList<>(a);
		List<List<Criterion>> b_copy = new ArrayList<>(b);
		List<List<Criterion>> f = new ArrayList<>();

		for (int i = k; i < this.c_set.size(); i++) { // L1
			System.out.println(k + " " + i + " T_i = " + Tools.showCriteria(this.c_set.get(i)) + " i = " + i);
			System.out.println(k + " " + i + " Is " + Tools.showSet(a) + " CAP " + Tools.showCriteria(this.c_set.get(i))
					+ " empty  : " + Tools.isCapEmpty(a, this.c_set.get(i)));

			if (Tools.isCapEmpty(a, this.c_set.get(i))) { // L2
				Double sum = 0.0;

				if (!a.isEmpty()) {
					for (List<Criterion> l : a) {
						sum += Tools.d_eu(l, 5, weights, deltas).getLeft();
					}
				}
				sum += Tools.d_eu(this.c_set.get(i), 5, weights, deltas).getLeft();

				Double hache = Tools.score(this.best_choice, this.weights)
						- Tools.score(this.second_choice, this.weights);
				// System.out.println(k +" " + i +" sum better than hache? "+sum +" >= "+
				// hache);
				if (sum >= hache) { // L3
					// System.out.println(k+" " + i +" Adding to F"+
					// this.showCriteria(this.c_set.get(i)));
					a_copy.add(this.c_set.get(i));
					f = new ArrayList<>(a_copy); // L4
				} else { // L5
					a_copy.add(this.c_set.get(i));
					// System.out.println(k+" " + i +" Calling algo_eu"+this.showSet(a_copy)+"
					// "+this.showSet(b)+" , "+ (i+1));
					f = algo_EU(a_copy, b_copy, (i + 1)); // L6

				}
				// System.out.println(k+" " + i +" Test for update :"+ !f.isEmpty() + " and [ "+
				// b_copy.isEmpty()+" or "+ this.includeDiscri(f,b) +" ]");
				if (!f.isEmpty() && (b_copy.isEmpty() || Tools.includeDiscri(f, b_copy, weights, deltas))) { // L8
					// System.out.println(k+" " + i +" UPDATE");
					b_copy = new ArrayList<>(f); // L8
				}
				System.out.println(k + " " + i + " A" + Tools.showSet(a)); // System.out.println(k+" " + i +" B" +
																			// this.showSet(b));//System.out.println(k+"
																			// " + i +" Test for return B :" +
																			// !b.isEmpty()+" and
																			// "+!this.includeDiscri(a_copy,b));
				if (!b_copy.isEmpty() && !Tools.includeDiscri(a_copy, b_copy, weights, deltas)) { // L10
					return b_copy;
				}
			}

			a_copy.clear();
		}
		List<List<Criterion>> empty = new ArrayList<>();

		// System.out.println("#### END ALGO EU : k = "+k);
		return empty;
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

		String explanation = this.best_choice.getName() + " is preferred to " + this.second_choice.getName() + " since "
				+ this.best_choice.getName() + " is better than " + this.second_choice.getName() + " on the criteria "
				+ Tools.showCriteria(k_ps) + " that are important " + "\n" + "and on the criteria "
				+ Tools.showCriteria(k_prs) + " that are relativily important, " + this.second_choice.getName()
				+ " is better than " + this.best_choice.getName() + " on the criteria " + Tools.showCriteria(k_nw)
				+ "\n" + "that are not important and on the criteria " + Tools.showCriteria(k_nrw)
				+ " that are not really important.";

		if (!k_pn.isEmpty()) {
			explanation += "And ";
			for (Couple<Criterion, Criterion> couple : k_pn) {
				explanation += couple.getRight().getName() + " for wich " + this.best_choice.getName()
						+ " is better than " + this.second_choice.getName() + " is more important than criterion "
						+ couple.getLeft().getName() + " for wich " + this.best_choice.getName() + " is worse than "
						+ this.second_choice.getName() + " ";
			}
			explanation += ".";
		}

		return explanation;
	}

	public List<Alternative> getAlternatives() {
		return alternatives;
	}

	public Anchor getAnchor() {
		return anchor;
	}

	public Alternative getBest_choice() {
		return best_choice;
	}

	public List<List<Criterion>> getC_set() {
		return c_set;
	}

	public List<Criterion> getCriteria() {
		return criteria;
	}

	public Map<Criterion, Double> getDeltas() {
		return deltas;
	}

	public Double getEpsilon() {
		return epsilon;
	}

	public List<Criterion> getK_nrw() {
		return k_nrw;
	}

	public List<Criterion> getK_nw() {
		return k_nw;
	}

	public List<Couple<Criterion, Criterion>> getK_pn() {
		return k_pn;
	}

	public List<Criterion> getK_prs() {
		return k_prs;
	}

	public List<Criterion> getK_ps() {
		return k_ps;
	}

	public Set<Criterion> getNegativeArguments() {
		return negativeArguments;
	}

	public Set<Criterion> getNullArguments() {
		return nullArguments;
	}

	public Set<Criterion> getPositiveArguments() {
		return positiveArguments;
	}

	public List<Couple<Criterion, Criterion>> getR_star() {
		return r_star;
	}

	public Alternative getSecond_choice() {
		return second_choice;
	}

	public Map<Criterion, Double> getWeights() {
		return weights;
	}

	@Override
	public Boolean isApplicable() {

		if (!c_set.isEmpty()) {
			return true;
		}

		int size = 2;
		size = this.criteria.size();
		List<List<Criterion>> subsets = new ArrayList<>();
		List<List<Criterion>> big_a = new ArrayList<>(); // -> first variable for algo_eu calls
		List<List<Criterion>> big_b = new ArrayList<>(); // -> second variable for algo_eu calls
		List<List<Criterion>> big_c = new ArrayList<>(); // result of algo_eu
		Double d_eu = null;
		List<Criterion> pi = new ArrayList<>();

		System.out.println("Delta " + this.best_choice.getName() + " > " + this.second_choice.getName() + " : "
				+ Tools.showVector(deltas) + "\n");

		do {
			d_eu = null;
			pi.clear();
			big_a.clear();
			big_b.clear();
			big_c.clear();
			subsets.clear();
			this.c_set.clear();

			subsets = Tools.allSubset(this.criteria);

			for (List<Criterion> subset : subsets) {
				d_eu = Tools.d_eu(subset, 5, this.weights, this.deltas).getLeft();
				pi = Tools.pi_min(subset, this.weights, this.deltas);
				if (d_eu > 0 && pi.containsAll(subset) && subset.containsAll(pi) && pi.containsAll(subset)) {
					this.c_set.add(subset);
				}

				pi.clear();
			}

			this.c_set = Tools.sortLexi(this.c_set, weights, deltas);

			// System.out.println("C_set : ");
			// for(List<Criterion> l : this.c_set)
			// System.out.println(Tools.showCriteria(l)); // + " " + this.d_eu(l,0) + " " +
			// this.d_eu(l,1) + " " + this.d_eu(l,5) + " " +
			// this.showCriteria(this.pi_min(l)));

			big_c = this.algo_EU(big_a, big_b, 0);
			size++;

		} while (big_c.isEmpty() && size <= this.criteria.size());

		if (big_c.isEmpty()) {
			return false;
		}

		// Start to determine R*

		List<Couple<Criterion, Criterion>> cpls = new ArrayList<>();
		List<Couple<Criterion, Criterion>> r_s = new ArrayList<>();

		System.out.println("Minimal permutation : " + Tools.showSet(big_c) + "\n");

		for (List<Criterion> l : big_c) {
			r_s = Tools.couples_of(l, weights, deltas);

			for (Couple<Criterion, Criterion> c : r_s) {
				if (!cpls.contains(c)) {
					cpls.add(c);
				}
			}

			r_s.clear();
		}

		this.r_star = Tools.r_star(cpls);

		// R* determined

		for (Couple<Criterion, Criterion> c : r_star) { // c = (i,j) where i and j are criteria

			// j in S+ and i in S- and w_j > w_i
			if (this.positiveArguments.contains(c.getRight()) && this.negativeArguments.contains(c.getLeft())
					&& this.weights.get(c.getRight()) > this.weights.get(c.getLeft())) {

				// w_j >= 1/n and w_i <= 1/n
				if (this.weights.get(c.getRight()) >= (1.0 / this.criteria.size())
						&& this.weights.get(c.getLeft()) <= (1.0 / this.criteria.size())) {
					if (!k_ps.contains(c.getRight())) {
						k_ps.add(c.getRight()); // adding j
					}
					if (!k_nw.contains(c.getLeft())) {
						k_nw.add(c.getLeft()); // adding i
					}
				} else {
					// w_j >> w_i
					if (this.weights.get(c.getRight()) > this.weights.get(c.getLeft()) + this.epsilon) {
						if (!k_pn.contains(c)) {
							k_pn.add(c); // adding (i,j)
						}
					} else {
						// w_j >= 1/n
						if (this.weights.get(c.getRight()) >= (1.0 / this.criteria.size())) {
							if (!k_ps.contains(c.getRight())) {
								k_ps.add(c.getRight()); // adding j
							}
						} else {
							// w_i <= 1/n
							if (this.weights.get(c.getLeft()) <= (1.0 / this.criteria.size())) {
								if (!k_nw.contains(c.getLeft())) {
									k_nw.add(c.getLeft()); // adding i
								}
							}
						}
					}
				}
			}

			// i and j in S+ and delta_j > delta_i and w_j > w_i
			if (this.positiveArguments.contains(c.getLeft()) && this.positiveArguments.contains(c.getRight())
					&& this.deltas.get(c.getRight()) > this.deltas.get(c.getLeft())
					&& this.weights.get(c.getRight()) > this.weights.get(c.getLeft())) {

				// w_j >= 1/n
				if (this.weights.get(c.getRight()) >= (1.0 / this.criteria.size())) {
					if (!k_ps.contains(c.getRight())) {
						k_ps.add(c.getRight()); // adding j
					}
				} else {
					if (!k_prs.contains(c.getRight())) {
						k_prs.add(c.getRight()); // adding j
					}
				}
			}

			if (this.negativeArguments.contains(c.getLeft()) && this.negativeArguments.contains(c.getRight())
					&& this.deltas.get(c.getRight()) > this.deltas.get(c.getLeft())
					&& this.weights.get(c.getRight()) > this.weights.get(c.getLeft())) {

				// w_j <= 1/n
				if (this.weights.get(c.getRight()) <= (1.0 / this.criteria.size())) {
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

		return true;
	}

}
