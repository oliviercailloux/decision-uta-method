package io.github.oliviercailloux.decision.arguer.labreuche;

import static java.util.Objects.requireNonNull;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.ImmutableGraph;

import io.github.oliviercailloux.decision.arguer.labreuche.output.ALLOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.Anchor;
import io.github.oliviercailloux.decision.arguer.labreuche.output.IVTOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.LabreucheOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.NOAOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.RMGAVGOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.RMGCOMPOutput;
import io.github.oliviercailloux.uta_calculator.App;
import io.github.oliviercailloux.uta_calculator.model.Alternative;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class LabreucheModel {

	/**
	 * @param labreuchOutput equal null iff it is not initialized.
	 * */
	
	private AlternativesComparison alternativesComparison;
	private double epsilon;
	private List<List<Criterion>> setCIVT;
	private LabreucheOutput labreucheOutput;
	private Logger logger;

	public LabreucheModel(AlternativesComparison alternativesComparaison) {
		this.alternativesComparison = requireNonNull(alternativesComparaison);

		this.setCIVT = new ArrayList<>();
		this.epsilon = 0.2 / this.alternativesComparison.getWeight().keySet().size();

		this.labreucheOutput = null;
		this.logger = LoggerFactory.getLogger(LabreucheModel.class);
	}

	public LabreucheModel(AlternativesComparison alternativesComparaison, double epsilon) {
		this.alternativesComparison = requireNonNull(alternativesComparaison);
		this.epsilon = requireNonNull(epsilon);

		this.setCIVT = new ArrayList<>();

		this.labreucheOutput = null;
		this.logger = LoggerFactory.getLogger(LabreucheModel.class);
	}

	/***************************************************************************************
	 * * GETTERS & SETTERS * *
	 *************************************************************************************/

	public List<List<Criterion>> getSetCIVT() {
		return this.setCIVT;
	}

	public Double getEpsilon() {
		return this.epsilon;
	}

	public AlternativesComparison getAlternativesComparison() {
		return this.alternativesComparison;
	}

	/****************************************************************************************
	 * * METHODS * *
	 ****************************************************************************************/

	/**
	 * @return the minimal set of permutation that inverse the decision between x
	 *         and y.
	 */
	private List<List<Criterion>> algoEU(List<List<Criterion>> a, List<List<Criterion>> b, int k) {
		// logger.info(" \n #### Calling ALGO_EU : " + Tools.showSet(a) + " and k
		// = " + k);

		List<List<Criterion>> a_copy = new ArrayList<>(a);
		List<List<Criterion>> b_copy = new ArrayList<>(b);
		List<List<Criterion>> f = new ArrayList<>();

		for (int i = k; i < this.setCIVT.size(); i++) { // L1
			logger.info(k + " " + i + " T_i = " + Tools.showCriteria(this.setCIVT.get(i)) + " i = " + i);
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
				// logger.info(k +" " + i +" sum better than hache? "+sum +" >= "+
				// hache);
				if (sum >= hache) { // L3
					// logger.info(k+" " + i +" Adding to F"+
					// this.showCriteria(this.setCIVT.get(i)));
					a_copy.add(this.setCIVT.get(i));
					f = new ArrayList<>(a_copy); // L4
				} else { // L5
					a_copy.add(this.setCIVT.get(i));
					// logger.info(k+" " + i +" Calling algo_eu"+this.showSet(a_copy)+"
					// "+this.showSet(b)+" , "+ (i+1));
					f = algoEU(a_copy, b_copy, (i + 1)); // L6

				}
				// logger.info(k+" " + i +" Test for update :"+ !f.isEmpty() + " and [ "+
				// b_copy.isEmpty()+" or "+ this.includeDiscri(f,b) +" ]");
				if (!f.isEmpty() && (b_copy.isEmpty() || Tools.includeDiscri(f, b_copy,
						this.alternativesComparison.getWeight(), this.alternativesComparison.getDelta()))) { // L8
					// logger.info(k+" " + i +" UPDATE");
					b_copy = new ArrayList<>(f); // L8
				}
				logger.info(k + " " + i + " A" + Tools.showSet(a)); // logger.info(k+" " + i +" B" +
																	// this.showSet(b));//logger.info(k+"
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

		// logger.info("#### END ALGO EU : k = "+k);
		return empty;
	}

	/*
	 * * * * * * * * * * * * * * * * * * * * * Resolution problem * * * * * * * * *
	 * * * * * * * * * * * *
	 */

	public LabreucheOutput getExplanation() {

		if (labreucheOutput != null) {
			return labreucheOutput;
		}

		if (isApplicable(Anchor.ALL)) {
			return labreucheOutput;
		}

		logger.info("noa");
		if (isApplicable(Anchor.NOA)) {
			return labreucheOutput;
		}

		logger.info("ivt");
		if (isApplicable(Anchor.IVT)) {
			return labreucheOutput;
		}

		logger.info("rmgavg");
		if (isApplicable(Anchor.RMGAVG)) {
			return labreucheOutput;
		}

		logger.info("rmgcomp");
		if (isApplicable(Anchor.RMGCOMP)) {
			return labreucheOutput;
		}

		return null;
	}

	public boolean isApplicable(Anchor anchor) {

		if (labreucheOutput != null) {
			if (labreucheOutput.getAnchor() == anchor) {
				return true;
			}
			return false;
		}

		switch (anchor) {

		case ALL:
			for (Map.Entry<Criterion, Double> d : alternativesComparison.getDelta().entrySet()) {
				if (d.getValue() < 0.0) {
					return false;
				}
			}

			labreucheOutput = new ALLOutput(alternativesComparison);

			return true;

		case NOA:
			if (Tools.score(alternativesComparison.getX(), alternativesComparison.getWeightReference()) >= Tools
					.score(alternativesComparison.getY(), alternativesComparison.getWeightReference())) {
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

			Map<Criterion, Double> wcomposed = Maps.newHashMap(alternativesComparison.getWeightReference());
			int p = keys.size() - 1;

			do {
				wcomposed.put(temp.get(keys.get(p)),
						this.alternativesComparison.getWeight().get((temp.get(keys.get(p)))));
				setC.add(temp.get(keys.get(p)));
				p--;
			} while (Tools.score(this.alternativesComparison.getX(), wcomposed) < Tools
					.score(this.alternativesComparison.getY(), wcomposed));

			labreucheOutput = new NOAOutput(this.alternativesComparison, setC);

			return true;

		case IVT:
			int size = 2;
			List<List<Criterion>> subsets = new ArrayList<>();
			List<List<Criterion>> big_a = new ArrayList<>(); // -> first variable for algo_eu calls
			List<List<Criterion>> big_b = new ArrayList<>(); // -> second variable for algo_eu calls
			List<List<Criterion>> big_c = new ArrayList<>(); // result of algo_eu
			Double d_eu = null;
			List<Criterion> pi = new ArrayList<>();

			// logger.info("Delta " + this.alternativesComparison.getX().getName() +
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

				// logger.info("setCIVT : ");
				// for(List<Criterion> l : this.setCIVT)
				// logger.info(Tools.showCriteria(l)); // + " " + this.d_eu(l,0) + " " +
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

			logger.info("Minimal permutation : " + Tools.showSet(big_c) + "\n");

			for (List<Criterion> l : big_c) {
				r_s = Tools.couples_of(l, this.alternativesComparison.getWeight(),
						this.alternativesComparison.getDelta());

				for (Couple<Criterion, Criterion> c : r_s) {
					if (!cpls.contains(c)) {
						cpls.add(c);
					}
				}

				r_s.clear();
			}

			ImmutableGraph<Criterion> rStar = Tools.buildRStar(cpls);

			// R* determined

			labreucheOutput = new IVTOutput(this.alternativesComparison, rStar, this.epsilon);

			return true;

		case RMGAVG:
			double max_w = Double.MIN_VALUE;

			for (Map.Entry<Criterion, Double> c : this.alternativesComparison.getWeight().entrySet()) {
				double v = Math.abs(c.getValue() - (1.0 / this.alternativesComparison.getCriteria().size()));

				if (v > max_w) {
					max_w = v;
				}
			}

			if (max_w <= this.epsilon) {
				labreucheOutput = new RMGAVGOutput(this.alternativesComparison);

				return true;
			}

			return false;

		case RMGCOMP:
			double max_w1 = Double.MIN_VALUE;

			for (Map.Entry<Criterion, Double> c : this.alternativesComparison.getWeight().entrySet()) {
				double v = Math.abs(c.getValue() - (1.0 / this.alternativesComparison.getCriteria().size()));

				if (v > max_w1) {
					max_w1 = v;
				}
			}

			if (max_w1 > this.epsilon) {
				labreucheOutput = new RMGCOMPOutput(this.alternativesComparison, this.epsilon);

				return true;
			}

			return false;

		default:
			logger.info("Anchor given unknown");
			return false;
		}
	}

	/**
	 * @return elements of argumentation if the anchor ALL is applicable or throw
	 *         IllegalStateException with a the message mess otherwise.
	 */
	public ALLOutput getALLExplanation() {
		String mess = "This anchor is not the one applicable";

		if (labreucheOutput != null) {
			if (labreucheOutput.getAnchor() == Anchor.ALL) {
				return (ALLOutput) labreucheOutput;
			}
			Preconditions.checkState(labreucheOutput.getAnchor() == Anchor.ALL, mess);
		}

		Preconditions.checkState(isApplicable(Anchor.ALL), mess);
		return (ALLOutput) labreucheOutput;
	}

	/**
	 * @return elements of argumentation if the anchor NOA is applicable or throw
	 *         IllegalStateException with a the message mess otherwise.
	 */
	public NOAOutput getNOAExplanation() {
		String message = "This anchor is not the one applicable";

		if (labreucheOutput != null) {
			if (labreucheOutput.getAnchor() == Anchor.NOA) {
				return (NOAOutput) labreucheOutput;
			}
			Preconditions.checkState(labreucheOutput.getAnchor() == Anchor.ALL, message);
		}

		Preconditions.checkState(isApplicable(Anchor.NOA), message);
		return (NOAOutput) labreucheOutput;
	}

	/**
	 * @return elements of argumentation if the anchor IVT is applicable or throw
	 *         IllegalStateException with a the message mess otherwise.
	 */
	public IVTOutput getIVTExplanation() {
		String message = "This anchor is not the one applicable";

		if (labreucheOutput != null) {
			if (labreucheOutput.getAnchor() == Anchor.IVT) {
				return (IVTOutput) labreucheOutput;
			}
			Preconditions.checkState(labreucheOutput.getAnchor() == Anchor.ALL, message);
		}

		Preconditions.checkState(isApplicable(Anchor.IVT), message);
		return (IVTOutput) labreucheOutput;
	}

	/**
	 * @return elements of argumentation if the anchor RMGAVG is applicable or throw
	 *         IllegalStateException with a the message mess otherwise.
	 */
	public RMGAVGOutput getRMGAVGExplanation() {
		String message = "This anchor is not the one applicable";

		if (labreucheOutput != null) {
			if (labreucheOutput.getAnchor() == Anchor.RMGAVG) {
				return (RMGAVGOutput) labreucheOutput;
			}
			Preconditions.checkState(labreucheOutput.getAnchor() == Anchor.ALL, message);
		}

		Preconditions.checkState(isApplicable(Anchor.RMGAVG), message);
		return (RMGAVGOutput) labreucheOutput;
	}

	/**
	 * @return elements of argumentation if the anchor RMGCOMP is applicable or
	 *         throw IllegalStateException with a the message mess otherwise.
	 */
	public RMGCOMPOutput getRMGCOMPExplanation() {
		String message = "This anchor is not the one applicable";

		if (labreucheOutput != null) {
			if (labreucheOutput.getAnchor() == Anchor.RMGCOMP) {
				return (RMGCOMPOutput) labreucheOutput;
			}
			Preconditions.checkState(labreucheOutput.getAnchor() == Anchor.ALL, message);
		}

		Preconditions.checkState(isApplicable(Anchor.RMGCOMP), message);
		return (RMGCOMPOutput) labreucheOutput;
	}

	public String arguer() {
		String explanation = "";

		switch (labreucheOutput.getAnchor()) {
		case ALL:
			ALLOutput phiALL = (ALLOutput) labreucheOutput;

			AlternativesComparison alcoALL = phiALL.getAlternativesComparison();

			explanation = alcoALL.getX().getName() + " is preferred to " + alcoALL.getY().getName() + " since "
					+ alcoALL.getX().getName() + " is better then " + alcoALL.getY().getName() + " on ALL criteria.";

			return explanation;

		case NOA:
			NOAOutput phiNOA = (NOAOutput) labreucheOutput;

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
			IVTOutput phiIVT = (IVTOutput) labreucheOutput;

			AlternativesComparison alcoIVT = phiIVT.getAlternativesComparison();

			ImmutableSet<Criterion> k_nrw = phiIVT.getNegativeRelativelyWeak();
			ImmutableSet<Criterion> k_nw = phiIVT.getNegativeWeak();
			ImmutableSet<Criterion> k_prs = phiIVT.getPositiveRelativelyStrong();
			ImmutableSet<Criterion> k_ps = phiIVT.getPositiveStrong();
			ImmutableGraph<Criterion> k_pn = phiIVT.getPositiveNegative();

			explanation = alcoIVT.getX().getName() + " is preferred to " + alcoIVT.getY().getName() + " since "
					+ alcoIVT.getX().getName() + " is better than " + alcoIVT.getY().getName() + " on the criteria "
					+ Tools.showCriteria(k_ps) + " that are important " + "\n" + "and on the criteria "
					+ Tools.showCriteria(k_prs) + " that are relativily important, " + alcoIVT.getY().getName()
					+ " is better than " + alcoIVT.getX().getName() + " on the criteria " + Tools.showCriteria(k_nw)
					+ "\n" + "that are not important and on the criteria " + Tools.showCriteria(k_nrw)
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
			RMGAVGOutput phiRMGAVG = (RMGAVGOutput) labreucheOutput;

			AlternativesComparison alcoRMGAVG = phiRMGAVG.getAlternativesComparison();

			explanation = alcoRMGAVG.getX().getName() + " is preferred to " + alcoRMGAVG.getY().getName() + " since "
					+ alcoRMGAVG.getX().getName() + " is on average better than " + alcoRMGAVG.getY().getName() + "\n"
					+ " and all the criteria have almost the same weights.";

			return explanation;

		case RMGCOMP:
			RMGCOMPOutput phiRMGCOMP = (RMGCOMPOutput) labreucheOutput;

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