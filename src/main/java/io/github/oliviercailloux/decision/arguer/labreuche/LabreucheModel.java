package io.github.oliviercailloux.decision.arguer.labreuche;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.github.oliviercailloux.decision.arguer.labreuche.output.ALLOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.IVTOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.LabreucheOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.NOAOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.RMGAVGOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.RMGCOMPOutput;
import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class LabreucheModel {

	private Alternative x;
	private Alternative y;
	private Map<Criterion, Double> weight;
	private Map<Criterion, Double> weightsReferences;
	private Map<Double, Alternative> scoreboard;

	private AlternativesComparison alternativesComparison;

	private Double epsilon;
	private List<List<Criterion>> c_set;

	private ALLOutput phi_all;
	private NOAOutput phi_noa;
	private IVTOutput phi_ivt;
	private RMGAVGOutput phi_rmgavg;
	private RMGCOMPOutput phi_rmgcomp;
	private LabreucheOutput lop;

	public LabreucheModel(Alternative x, Alternative y, Map<Criterion, Double> weight) {
		this.weight = requireNonNull(weight);
		this.x = requireNonNull(x);
		this.y = requireNonNull(y);
		this.scoreboard = new HashMap<>();
		this.c_set = new ArrayList<>();
		this.epsilon = 0.2 / this.weight.keySet().size();
		this.weightsReferences = new HashMap<>();

		for (Criterion c : this.weight.keySet()) {
			this.weightsReferences.put(c, 1.0 / this.weight.keySet().size());
		}

		System.out.println("youpi!");

		(this).startproblem(false);
	}

	/***************************************************************************************
	 * * GETTERS & SETTERS * *
	 *************************************************************************************/

	public Map<Criterion, Double> getWeight() {
		return this.weight;
	}

	public Map<Double, Alternative> getScoreboard() {
		return this.scoreboard;
	}

	public List<List<Criterion>> getC_set() {
		return this.c_set;
	}

	public Double getEpsilon() {
		return this.epsilon;
	}

	public Alternative getX() {
		return this.x;
	}

	public Alternative getY() {
		return this.y;
	}

	/****************************************************************************************
	 * * METHODS * *
	 ****************************************************************************************/

	// associate the score for all the alternatives and stock them on the
	// scoreboard.
	public void scoringAlternatives() {
		this.scoreboard.put(Tools.score(this.x, this.weight), this.x);
		this.scoreboard.put(Tools.score(this.y, this.weight), this.y);
	}

	/**
	 * @return the minimal set of permutation that inverse the decision between x
	 *         and y.
	 */
	public List<List<Criterion>> algo_EU(List<List<Criterion>> a, List<List<Criterion>> b, int k) {
		// System.out.println(" \n #### Calling ALGO_EU : " + Tools.showSet(a) + " and k
		// = " + k);

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
						sum += Tools
								.d_eu(l, 5, this.alternativesComparison.getWeight(), alternativesComparison.getDelta())
								.getLeft();
					}
				}
				sum += Tools.d_eu(this.c_set.get(i), 5, this.alternativesComparison.getWeight(),
						this.alternativesComparison.getDelta()).getLeft();

				Double hache = Tools.score(this.alternativesComparison.getX(), this.alternativesComparison.getWeight())
						- Tools.score(this.alternativesComparison.getY(), this.alternativesComparison.getWeight());
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
				if (!f.isEmpty() && (b_copy.isEmpty() || Tools.includeDiscri(f, b_copy,
						this.alternativesComparison.getWeight(), this.alternativesComparison.getDelta()))) { // L8
					// System.out.println(k+" " + i +" UPDATE");
					b_copy = new ArrayList<>(f); // L8
				}
				System.out.println(k + " " + i + " A" + Tools.showSet(a)); // System.out.println(k+" " + i +" B" +
																			// this.showSet(b));//System.out.println(k+"
																			// " + i +" Test for return B :" +
																			// !b.isEmpty()+" and
																			// "+!this.includeDiscri(a_copy,b));
				if (!b_copy.isEmpty() && !Tools.includeDiscri(a_copy, b_copy, this.alternativesComparison.getWeight(),
						this.alternativesComparison.getDelta())) { // L10
					return b_copy;
				}
			}

			a_copy.clear();
		}
		List<List<Criterion>> empty = new ArrayList<>();

		// System.out.println("#### END ALGO EU : k = "+k);
		return empty;
	}

	/*
	 * * * * * * * * * * * * * * * * * * * * * Resolution problem * * * * * * * * *
	 * * * * * * * * * * * *
	 */

	public void startproblem(boolean show) {

		String display = "****************************************************************";
		display += "\n" + "*                                                              *";
		display += "\n" + "*         Recommender system based on Labreuche Model          *";
		display += "\n" + "*                                                              *";
		display += "\n" + "****************************************************************" + "\n";

		display += "\n    Criteria    <-   Weight : \n";

		for (Criterion c : this.weight.keySet())
			display += "\n" + "	" + c.getName() + "  <-  w_" + c.getId() + " = " + this.weight.get(c);

		display += "\n \n Alternatives : ";

		display += "\n" + "	" + this.x.getName() + " " + " : " + Tools.displayAsVector(this.x);
		display += "\n" + "	" + this.y.getName() + " " + " : " + Tools.displayAsVector(this.y);

		if (show)
			System.out.println(display);

		// long start = System.currentTimeMillis();

		this.scoringAlternatives();

		display = "\n" + "Top of alternatives : ";

		ArrayList<Double> scores = new ArrayList<>(this.scoreboard.keySet());
		Collections.sort(scores);

		for (int i = scores.size() - 1; i >= 0; i--)
			display += "\n " + " " + this.scoreboard.get(scores.get(i)).getName() + " : " + scores.get(i);

		this.x = this.scoreboard.get(scores.get(scores.size() - 1));
		this.y = this.scoreboard.get(scores.get(scores.size() - 2));

		this.alternativesComparison = new AlternativesComparison(this.x, this.y, this.weight);

		if (show)
			System.out.println(display + "\n");

		display = "Explanation why " + this.x.getName() + " is better than " + this.y.getName() + " :";

		// getExplanation();

		// String explanation = arguer();

		// display = "\n" + explanation;
		// long time = System.currentTimeMillis() - start;
		// display += "\n \n" + "Problem solved in : " + time + " milliseconds";

		if (show)
			System.out.println(display);
	}

	public LabreucheOutput getExplanation() {

		if (isAnchorALLApplicable()) {
			lop = phi_all;
		} else {
			System.out.println("noa");
			if (isAnchorNOAApplicable()) {
				lop = phi_noa;
			} else {
				System.out.println("ivt");
				if (isAnchorIVTApplicable()) {
					lop = phi_ivt;
				} else {
					System.out.println("rmg");
					if (isAnchorRMGAVGApplicable()) {
						lop = phi_rmgavg;
					} else {
						lop = phi_rmgcomp;
					}
				}
			}
		}

		return lop;
	}

	public boolean isAnchorALLApplicable() {

		for (Map.Entry<Criterion, Double> d : this.alternativesComparison.getDelta().entrySet()) {
			if (d.getValue() < 0.0) {
				return false;
			}
		}

		phi_all = new ALLOutput(this.alternativesComparison);

		return true;
	}

	public boolean isAnchorNOAApplicable() {

		if (Tools.score(this.alternativesComparison.getX(), this.weightsReferences) > Tools
				.score(this.alternativesComparison.getY(), this.weightsReferences)) {
			return false;
		}

		Map<Double, Criterion> temp = new HashMap<>();
		Set<Criterion> setC = new LinkedHashSet<>();

		for (Map.Entry<Criterion, Double> w_i : this.alternativesComparison.getWeight().entrySet()) {
			temp.put((w_i.getValue() - 1.0 / this.alternativesComparison.getCriteria().size())
					* this.alternativesComparison.getDelta().get(w_i.getKey()), w_i.getKey());
		}

		ArrayList<Double> keys = new ArrayList<>(temp.keySet());
		Collections.sort(keys);

		Map<Criterion, Double> wcomposed = this.weightsReferences;
		int p = keys.size() - 1;

		do {
			wcomposed.put(temp.get(keys.get(p)), this.alternativesComparison.getWeight().get((temp.get(keys.get(p)))));
			setC.add(temp.get(keys.get(p)));
			p--;
		} while (Tools.score(this.alternativesComparison.getX(), wcomposed) < Tools
				.score(this.alternativesComparison.getY(), wcomposed));

		phi_noa = new NOAOutput(this.alternativesComparison, setC);

		return true;
	}

	public boolean isAnchorIVTApplicable() {

		int size = 2;
		List<List<Criterion>> subsets = new ArrayList<>();
		List<List<Criterion>> big_a = new ArrayList<>(); // -> first variable for algo_eu calls
		List<List<Criterion>> big_b = new ArrayList<>(); // -> second variable for algo_eu calls
		List<List<Criterion>> big_c = new ArrayList<>(); // result of algo_eu
		Double d_eu = null;
		List<Criterion> pi = new ArrayList<>();

		// System.out.println("Delta " + this.alternativesComparison.getX().getName() +
		// " > " + this.alternativesComparison.getY().getName() + " : "
		// + Tools.showVector(alternativesComparison.getDelta()) + "\n");

		do {
			d_eu = null;
			pi.clear();
			big_a.clear();
			big_b.clear();
			big_c.clear();
			subsets.clear();
			this.c_set.clear();

			subsets = Tools.allSubset(new ArrayList<>(this.alternativesComparison.getCriteria()));

			for (List<Criterion> subset : subsets) {
				d_eu = Tools.d_eu(subset, 5, this.alternativesComparison.getWeight(),
						this.alternativesComparison.getDelta()).getLeft();
				pi = Tools.pi_min(subset, this.alternativesComparison.getWeight(),
						this.alternativesComparison.getDelta());
				if (d_eu > 0 && pi.containsAll(subset) && subset.containsAll(pi) && pi.containsAll(subset)) {
					this.c_set.add(subset);
				}

				pi.clear();
			}

			this.c_set = Tools.sortLexi(this.c_set, this.alternativesComparison.getWeight(),
					alternativesComparison.getDelta());

			// System.out.println("C_set : ");
			// for(List<Criterion> l : this.c_set)
			// System.out.println(Tools.showCriteria(l)); // + " " + this.d_eu(l,0) + " " +
			// this.d_eu(l,1) + " " + this.d_eu(l,5) + " " +
			// this.showCriteria(this.pi_min(l)));

			big_c = this.algo_EU(big_a, big_b, 0);
			size++;

		} while (big_c.isEmpty() && size <= this.alternativesComparison.getCriteria().size());

		if (big_c.isEmpty()) {
			return false;
		}

		// Start to determine R*

		List<Couple<Criterion, Criterion>> cpls = new ArrayList<>();
		List<Couple<Criterion, Criterion>> r_s = new ArrayList<>();

		System.out.println("Minimal permutation : " + Tools.showSet(big_c) + "\n");

		for (List<Criterion> l : big_c) {
			r_s = Tools.couples_of(l, this.alternativesComparison.getWeight(), this.alternativesComparison.getDelta());

			for (Couple<Criterion, Criterion> c : r_s) {
				if (!cpls.contains(c)) {
					cpls.add(c);
				}
			}

			r_s.clear();
		}

		List<Couple<Criterion, Criterion>> r_star = Tools.r_star(cpls);

		// R* determined

		phi_ivt = new IVTOutput(this.alternativesComparison, r_star, this.epsilon);

		return true;
	}

	public boolean isAnchorRMGAVGApplicable() {
		Double max_w = Double.MIN_VALUE;

		for (Map.Entry<Criterion, Double> c : this.alternativesComparison.getWeight().entrySet()) {
			Double v = Math.abs(c.getValue() - (1.0 / this.alternativesComparison.getCriteria().size()));

			if (v > max_w) {
				max_w = v;
			}
		}

		if (max_w <= this.epsilon) {
			phi_rmgavg = new RMGAVGOutput(this.alternativesComparison);
			return true;
		}

		return false;
	}

	public boolean isAnchorRMGCOMPApplicable() {
		Double max_w = Double.MIN_VALUE;

		for (Map.Entry<Criterion, Double> c : this.alternativesComparison.getWeight().entrySet()) {
			Double v = Math.abs(c.getValue() - (1.0 / this.alternativesComparison.getCriteria().size()));

			if (v > max_w) {
				max_w = v;
			}
		}

		if (max_w > this.epsilon) {
			phi_rmgcomp = new RMGCOMPOutput(this.alternativesComparison, this.epsilon);
			return true;
		}

		return false;
	}

	public ALLOutput getALLExplanation() {
		return phi_all;
	}

	public NOAOutput getNOAExplanation() {
		return phi_noa;
	}

	public IVTOutput getIVTExplanation() {
		return phi_ivt;
	}

	public RMGAVGOutput getRMGAVGExplanation() {
		return phi_rmgavg;
	}

	public RMGCOMPOutput getRMGCOMPExplanation() {
		return phi_rmgcomp;
	}

	public String arguer() {
		String explanation = "";

		switch (lop.getAnchor()) {
		case ALL:

			AlternativesComparison alcoALL = phi_all.getAlternativesComparison();

			explanation = alcoALL.getX().getName() + " is preferred to " + alcoALL.getY().getName() + " since "
					+ alcoALL.getX().getName() + " is better then " + alcoALL.getY().getName() + " on ALL criteria.";

			return explanation;

		case NOA:

			AlternativesComparison alcoNOA = phi_noa.getAlternativesComparison();
			Set<Criterion> ConPA = phi_noa.getPositiveNoaCriteria();
			Set<Criterion> ConNA = phi_noa.getNegativeNoaCriteria();

			explanation = "Even though " + alcoNOA.getY().getName() + " is better than " + alcoNOA.getX().getName()
					+ " on average, " + alcoNOA.getX().getName() + " is preferred to " + alcoNOA.getY().getName()
					+ " since \n" + alcoNOA.getX().getName() + " is better than " + alcoNOA.getY().getName()
					+ " on the criteria " + Tools.showCriteria(ConPA) + " that are important whereas \n"
					+ alcoNOA.getX().getName() + " is worse than " + alcoNOA.getY().getName() + " on the criteria "
					+ Tools.showCriteria(ConNA) + " that are not important.";

			Double addSentence = 0.0;

			for (Criterion c : alcoNOA.getCriteria()) {
				if (!phi_noa.getNoaCriteria().contains(c)) {
					addSentence += alcoNOA.getDelta().get(c) * alcoNOA.getWeight().get(c);
				}
			}

			if (addSentence > 0) {
				explanation += "\n" + "Moroever, " + alcoNOA.getX().getName() + " is on average better than "
						+ alcoNOA.getY().getName() + " on the other criteria.";
			}

			return explanation;

		case IVT:

			AlternativesComparison alcoIVT = phi_ivt.getAlternativesComparison();

			Set<Criterion> k_nrw = phi_ivt.getK_nrw();
			Set<Criterion> k_nw = phi_ivt.getK_nw();
			Set<Criterion> k_prs = phi_ivt.getK_prs();
			Set<Criterion> k_ps = phi_ivt.getK_ps();
			Set<Couple<Criterion, Criterion>> k_pn = phi_ivt.getK_pn();

			explanation = alcoIVT.getX().getName() + " is preferred to " + alcoIVT.getY().getName() + " since "
					+ alcoIVT.getX().getName() + " is better than " + alcoIVT.getY().getName() + " on the criteria "
					+ Tools.showCriteria(k_ps) + " that are important " + "\n" + "and on the criteria "
					+ Tools.showCriteria(k_prs) + " that are relativily important, " + alcoIVT.getY().getName()
					+ " is better than " + alcoIVT.getX().getName() + " on the criteria " + Tools.showCriteria(k_nw)
					+ "\n" + "that are not important and on the criteria " + Tools.showCriteria(k_nrw)
					+ " that are not really important.";

			if (!k_pn.isEmpty()) {
				explanation += "And ";

				for (Couple<Criterion, Criterion> couple : k_pn) {

					explanation += couple.getRight().getName() + " for wich " + alcoIVT.getX().getName()
							+ " is better than " + alcoIVT.getY().getName() + " is more important than criterion "
							+ couple.getLeft().getName() + " for wich " + alcoIVT.getX().getName() + " is worse than "
							+ alcoIVT.getY().getName() + " ";
				}

				explanation += ".";
			}

			return explanation;

		case RMGAVG:

			AlternativesComparison alcoRMGAVG = phi_rmgavg.getAlternativesComparison();

			explanation = alcoRMGAVG.getX().getName() + " is preferred to " + alcoRMGAVG.getY().getName() + " since "
					+ alcoRMGAVG.getX().getName() + " is on average better than " + alcoRMGAVG.getY().getName() + "\n"
					+ " and all the criteria have almost the same weights.";

			return explanation;

		case RMGCOMP:

			AlternativesComparison alcoRMGCOMP = phi_rmgcomp.getAlternativesComparison();

			Double maxW = phi_rmgcomp.getMaxW();
			Double eps = phi_rmgcomp.getEpsilon();
			if (maxW > eps) {
				if (maxW <= eps * 2) {
					explanation = alcoRMGCOMP.getX().getName() + " is preferred to " + alcoRMGCOMP.getY().getName()
							+ " since the intensity of the preference " + alcoRMGCOMP.getX().getName() + " over "
							+ alcoRMGCOMP.getY().getName() + " on \n"
							+ Tools.showCriteria(alcoRMGCOMP.getPositiveCriteria())
							+ " is significantly larger than the intensity of " + alcoRMGCOMP.getY().getName()
							+ " over " + alcoRMGCOMP.getX().getName() + " on \n"
							+ Tools.showCriteria(alcoRMGCOMP.getNegativeCriteria())
							+ ", and all the criteria have more or less the same weights.";

					return explanation;
				}
			}

			explanation = alcoRMGCOMP.getX().getName() + " is preferred to " + alcoRMGCOMP.getY().getName()
					+ " since the intensity of the preference " + alcoRMGCOMP.getX().getName() + " over "
					+ alcoRMGCOMP.getX().getName() + " on " + Tools.showCriteria(alcoRMGCOMP.getPositiveCriteria())
					+ " is much larger than the intensity of " + alcoRMGCOMP.getY().getName() + " over "
					+ alcoRMGCOMP.getX().getName() + " on " + Tools.showCriteria(alcoRMGCOMP.getNegativeCriteria())
					+ ".";

			return explanation;

		default:
			return "No possible argumentation found";
		}
	}

}