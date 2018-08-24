package io.github.oliviercailloux.decision.arguer.labreuche;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.ImmutableGraph;

import io.github.oliviercailloux.decision.arguer.labreuche.output.ALLOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.Anchor;
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

	private AlternativesComparison alternativesComparison;

	private Double epsilon;
	private List<List<Criterion>> setCIVT;

	private ALLOutput phiALL;
	private NOAOutput phiNOA;
	private IVTOutput phiIVT;
	private RMGAVGOutput phiRMGAVG;
	private RMGCOMPOutput phiRMGCOMP;
	private LabreucheOutput labreucheOutput;

	public LabreucheModel(Alternative x, Alternative y, Map<Criterion, Double> weight) {
		this.weight = requireNonNull(weight);
		this.x = requireNonNull(x);
		this.y = requireNonNull(y);
		this.setCIVT = new ArrayList<>();
		this.epsilon = 0.2 / this.weight.keySet().size();
		this.weightsReferences = new HashMap<>();

		for (Criterion c : this.weight.keySet()) {
			this.weightsReferences.put(c, 1.0 / this.weight.keySet().size());
		}

		this.phiALL = null;
		this.phiNOA = null;
		this.phiIVT = null;
		this.phiRMGAVG = null;
		this.phiRMGCOMP = null;
		this.labreucheOutput = null;

		(this).startproblem(false);
	}

	/***************************************************************************************
	 * * GETTERS & SETTERS * *
	 *************************************************************************************/

	public Map<Criterion, Double> getWeight() {
		return this.weight;
	}

	public List<List<Criterion>> getsetCIVT() {
		return this.setCIVT;
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

	/**
	 * @return the minimal set of permutation that inverse the decision between x
	 *         and y.
	 */
	private List<List<Criterion>> algoEU(List<List<Criterion>> a, List<List<Criterion>> b, int k) {
		// System.out.println(" \n #### Calling ALGO_EU : " + Tools.showSet(a) + " and k
		// = " + k);

		List<List<Criterion>> a_copy = new ArrayList<>(a);
		List<List<Criterion>> b_copy = new ArrayList<>(b);
		List<List<Criterion>> f = new ArrayList<>();

		for (int i = k; i < this.setCIVT.size(); i++) { // L1
			System.out.println(k + " " + i + " T_i = " + Tools.showCriteria(this.setCIVT.get(i)) + " i = " + i);
			System.out
					.println(k + " " + i + " Is " + Tools.showSet(a) + " CAP " + Tools.showCriteria(this.setCIVT.get(i))
							+ " empty  : " + Tools.isCapEmpty(a, this.setCIVT.get(i)));

			if (Tools.isCapEmpty(a, this.setCIVT.get(i))) { // L2
				Double sum = 0.0;

				if (!a.isEmpty()) {
					for (List<Criterion> l : a) {
						sum += Tools
								.d_eu(l, 5, this.alternativesComparison.getWeight(), alternativesComparison.getDelta())
								.getLeft();
					}
				}
				sum += Tools.d_eu(this.setCIVT.get(i), 5, this.alternativesComparison.getWeight(),
						this.alternativesComparison.getDelta()).getLeft();

				Double hache = Tools.score(this.alternativesComparison.getX(), this.alternativesComparison.getWeight())
						- Tools.score(this.alternativesComparison.getY(), this.alternativesComparison.getWeight());
				// System.out.println(k +" " + i +" sum better than hache? "+sum +" >= "+
				// hache);
				if (sum >= hache) { // L3
					// System.out.println(k+" " + i +" Adding to F"+
					// this.showCriteria(this.setCIVT.get(i)));
					a_copy.add(this.setCIVT.get(i));
					f = new ArrayList<>(a_copy); // L4
				} else { // L5
					a_copy.add(this.setCIVT.get(i));
					// System.out.println(k+" " + i +" Calling algo_eu"+this.showSet(a_copy)+"
					// "+this.showSet(b)+" , "+ (i+1));
					f = algoEU(a_copy, b_copy, (i + 1)); // L6

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

	private void startproblem(boolean show) {

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

		double scoreX = Tools.score(x, weight);
		double scoreY = Tools.score(y, weight);
				
		if(scoreX >= scoreY) {
			alternativesComparison = new AlternativesComparison(x, y, weight);
		}
		else {
			alternativesComparison = new AlternativesComparison(y, x, weight);

		}	
		
		display = "\n" + "			Alternatives ranked";
		display += "\n" + alternativesComparison.getX().getName() + " = " + Tools.score(alternativesComparison.getX(), weight);
		display += "\n" + alternativesComparison.getY().getName() + " = " + Tools.score(alternativesComparison.getY(), weight);


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
			labreucheOutput = phiALL;
		} else {
			System.out.println("noa");
			if (isAnchorNOAApplicable()) {
				labreucheOutput = phiNOA;
			} else {
				System.out.println("ivt");
				if (isAnchorIVTApplicable()) {
					labreucheOutput = phiIVT;
				} else {
					System.out.println("rmg");
					if (isAnchorRMGAVGApplicable()) {
						labreucheOutput = phiRMGAVG;
					} else {
						labreucheOutput = phiRMGCOMP;
					}
				}
			}
		}

		return labreucheOutput;
	}

	public boolean isAnchorALLApplicable() {

		for (Map.Entry<Criterion, Double> d : this.alternativesComparison.getDelta().entrySet()) {
			if (d.getValue() < 0.0) {
				return false;
			}
		}

		phiALL = new ALLOutput(this.alternativesComparison);

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

		phiNOA = new NOAOutput(this.alternativesComparison, setC);

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
			this.setCIVT.clear();

			subsets = Tools.allSubset(new ArrayList<>(this.alternativesComparison.getCriteria()));

			for (List<Criterion> subset : subsets) {
				d_eu = Tools.d_eu(subset, 5, this.alternativesComparison.getWeight(),
						this.alternativesComparison.getDelta()).getLeft();
				pi = Tools.pi_min(subset, this.alternativesComparison.getWeight(),
						this.alternativesComparison.getDelta());
				if (d_eu > 0 && pi.containsAll(subset) && subset.containsAll(pi) && pi.containsAll(subset)) {
					this.setCIVT.add(subset);
				}

				pi.clear();
			}

			this.setCIVT = Tools.sortLexi(this.setCIVT, this.alternativesComparison.getWeight(),
					alternativesComparison.getDelta());

			// System.out.println("setCIVT : ");
			// for(List<Criterion> l : this.setCIVT)
			// System.out.println(Tools.showCriteria(l)); // + " " + this.d_eu(l,0) + " " +
			// this.d_eu(l,1) + " " + this.d_eu(l,5) + " " +
			// this.showCriteria(this.pi_min(l)));

			big_c = this.algoEU(big_a, big_b, 0);
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

		ImmutableGraph<Criterion> rStar = Tools.buildRStar(cpls);

		// R* determined

		phiIVT = new IVTOutput(this.alternativesComparison, rStar, this.epsilon);

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
			phiRMGAVG = new RMGAVGOutput(this.alternativesComparison);
			return true;
		}

		return false;
	}

	public boolean isApplicable(Anchor anchor) {
		switch (anchor) {

		case ALL:
			return isAnchorALLApplicable();

		case NOA:
			return isAnchorNOAApplicable();

		case IVT:
			return isAnchorIVTApplicable();

		case RMGAVG:
			return isAnchorRMGAVGApplicable();

		case RMGCOMP:
			return isAnchorRMGCOMPApplicable();

		default:
			System.out.println("Anchor given unknown");
			return false;
		}
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
			phiRMGCOMP = new RMGCOMPOutput(this.alternativesComparison, this.epsilon);
			return true;
		}

		return false;
	}

	
	/**
	 * @return elements of argumentation if the anchor ALL is applicable
	 * or throw IllegalStateException with a the message mess otherwise.
	 * */
	public ALLOutput getALLExplanation() {
		String mess = "This anchor is not the one applicable";
		Preconditions.checkState(isAnchorALLApplicable(), mess);
		return phiALL;
	}

	/**
	 * @return elements of argumentation if the anchor NOA is applicable
	 * or throw IllegalStateException with a the message mess otherwise.
	 * */
	public NOAOutput getNOAExplanation() {
		String message = "This anchor is not the one applicable";
		Preconditions.checkState(isAnchorNOAApplicable(), message);
		return phiNOA;
	}
	
	/**
	 * @return elements of argumentation if the anchor IVT is applicable
	 * or throw IllegalStateException with a the message mess otherwise.
	 * */
	public IVTOutput getIVTExplanation() {
		String message = "This anchor is not the one applicable";
		Preconditions.checkState(isAnchorIVTApplicable(), message);
		return phiIVT;
	}
	
	/**
	 * @return elements of argumentation if the anchor RMGAVG is applicable
	 * or throw IllegalStateException with a the message mess otherwise.
	 * */
	public RMGAVGOutput getRMGAVGExplanation() {
		String message = "This anchor is not the one applicable";
		Preconditions.checkState(isAnchorRMGAVGApplicable(), message);
		return phiRMGAVG;
	}

	/**
	 * @return elements of argumentation if the anchor RMGCOMP is applicable
	 * or throw IllegalStateException with a the message mess otherwise.
	 * */
	public RMGCOMPOutput getRMGCOMPExplanation() {
		String message = "This anchor is not the one applicable";
		Preconditions.checkState(isAnchorRMGCOMPApplicable(), message);
		return phiRMGCOMP;
	}
	

	public String arguer() {
		String explanation = "";

		switch (labreucheOutput.getAnchor()) {
		case ALL:

			AlternativesComparison alcoALL = phiALL.getAlternativesComparison();

			explanation = alcoALL.getX().getName() + " is preferred to " + alcoALL.getY().getName() + " since "
					+ alcoALL.getX().getName() + " is better then " + alcoALL.getY().getName() + " on ALL criteria.";

			return explanation;

		case NOA:

			AlternativesComparison alcoNOA = phiNOA.getAlternativesComparison();
			Set<Criterion> ConPA = phiNOA.getPositiveNoaCriteria();
			Set<Criterion> ConNA = phiNOA.getNegativeNoaCriteria();

			explanation = "Even though " + alcoNOA.getY().getName() + " is better than " + alcoNOA.getX().getName()
					+ " on average, " + alcoNOA.getX().getName() + " is preferred to " + alcoNOA.getY().getName()
					+ " since \n" + alcoNOA.getX().getName() + " is better than " + alcoNOA.getY().getName()
					+ " on the criteria " + Tools.showCriteria(ConPA) + " that are important whereas \n"
					+ alcoNOA.getX().getName() + " is worse than " + alcoNOA.getY().getName() + " on the criteria "
					+ Tools.showCriteria(ConNA) + " that are not important.";

			Double addSentence = 0.0;

			for (Criterion c : alcoNOA.getCriteria()) {
				if (!phiNOA.getNoaCriteria().contains(c)) {
					addSentence += alcoNOA.getDelta().get(c) * alcoNOA.getWeight().get(c);
				}
			}

			if (addSentence > 0) {
				explanation += "\n" + "Moroever, " + alcoNOA.getX().getName() + " is on average better than "
						+ alcoNOA.getY().getName() + " on the other criteria.";
			}

			return explanation;

		case IVT:

			AlternativesComparison alcoIVT = phiIVT.getAlternativesComparison();

			ImmutableGraph<Criterion> k_nrw = phiIVT.getNegativeRelativelyWeak();
			ImmutableGraph<Criterion> k_nw = phiIVT.getNegativeWeak();
			ImmutableGraph<Criterion> k_prs = phiIVT.getPositiveRelativelyStrong();
			ImmutableGraph<Criterion> k_ps = phiIVT.getPositiveStrong();
			ImmutableGraph<Criterion> k_pn = phiIVT.getPositiveNegative();

			explanation = alcoIVT.getX().getName() + " is preferred to " + alcoIVT.getY().getName() + " since "
					+ alcoIVT.getX().getName() + " is better than " + alcoIVT.getY().getName() + " on the criteria "
					+ Tools.showGraph(k_ps) + " that are important " + "\n" + "and on the criteria "
					+ Tools.showGraph(k_prs) + " that are relativily important, " + alcoIVT.getY().getName()
					+ " is better than " + alcoIVT.getX().getName() + " on the criteria " + Tools.showGraph(k_nw)
					+ "\n" + "that are not important and on the criteria " + Tools.showGraph(k_nrw)
					+ " that are not really important.";

			if (!k_pn.edges().isEmpty()) {
				explanation += "And ";

				for (EndpointPair<Criterion> couple : k_pn.edges()) {

					explanation += couple.nodeV().getName() + " for wich " + alcoIVT.getX().getName()
							+ " is better than " + alcoIVT.getY().getName() + " is more important than criterion "
							+ couple.nodeU().getName() + " for wich " + alcoIVT.getX().getName() + " is worse than "
							+ alcoIVT.getY().getName() + " ";
				}

				explanation += ".";
			}

			return explanation;

		case RMGAVG:

			AlternativesComparison alcoRMGAVG = phiRMGAVG.getAlternativesComparison();

			explanation = alcoRMGAVG.getX().getName() + " is preferred to " + alcoRMGAVG.getY().getName() + " since "
					+ alcoRMGAVG.getX().getName() + " is on average better than " + alcoRMGAVG.getY().getName() + "\n"
					+ " and all the criteria have almost the same weights.";

			return explanation;

		case RMGCOMP:

			AlternativesComparison alcoRMGCOMP = phiRMGCOMP.getAlternativesComparison();
			Double maxW = phiRMGCOMP.getMaxW();
			Double eps = phiRMGCOMP.getEpsilon();
			
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