package io.github.oliviercailloux.decision.arguer.labreuche;

import static java.util.Objects.requireNonNull;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import com.google.common.graph.MutableGraph;

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
	 * @param labreuchOutput
	 *            equal null iff it is not initialized.
	 */

	private AlternativesComparison alternativesComparison;
	private double epsilon;
	private List<List<Criterion>> setCIVT;
	private LabreucheOutput labreucheOutput;
	private static final Logger logger = LoggerFactory.getLogger(LabreucheModel.class);

	public LabreucheModel(AlternativesComparison alternativesComparaison) {
		this.alternativesComparison = requireNonNull(alternativesComparaison);

		this.setCIVT = new ArrayList<>();
		this.epsilon = 0.2 / this.alternativesComparison.getWeight().keySet().size();
		this.labreucheOutput = null;
	}

	public LabreucheModel(AlternativesComparison alternativesComparaison, double epsilon) {
		this.alternativesComparison = requireNonNull(alternativesComparaison);
		this.epsilon = requireNonNull(epsilon);

		this.setCIVT = new ArrayList<>();
		this.labreucheOutput = null;
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

	public LabreucheOutput getLabreucheOutput() {
		return this.labreucheOutput;
	}

	/****************************************************************************************
	 * * METHODS * *
	 ****************************************************************************************/

	/**
	 * @return the minimal set of permutation that inverse the decision between x
	 *         and y.
	 */

	private List<List<Criterion>> algoEU(List<List<Criterion>> a, List<List<Criterion>> b, int k) {

		logger.debug(" \n #### Calling ALGO_EU : " + Tools.showSet(a) + " and k = " + k);

		List<List<Criterion>> a_copy = new ArrayList<>(a);
		List<List<Criterion>> b_copy = new ArrayList<>(b);
		List<List<Criterion>> f = new ArrayList<>();

		for (int i = k; i < setCIVT.size(); i++) { // L1

			logger.debug(k + " " + i + " T_i = " + Tools.showCriteria(setCIVT.get(i)) + " i = " + i);
			logger.debug(k + " " + i + " Is " + Tools.showSet(a) + " CAP " + Tools.showCriteria(setCIVT.get(i))
					+ " empty  : " + Tools.isCapEmpty(a, setCIVT.get(i)));

			if (Tools.isCapEmpty(a, setCIVT.get(i))) { // L2
				Double sum = 0.0;

				if (!a.isEmpty()) {
					for (List<Criterion> l : a) {
						sum += Tools.d_eu(l, alternativesComparison.getWeight(), alternativesComparison.getDelta())
								.getLeft();
					}
				}
				sum += Tools
						.d_eu(setCIVT.get(i), alternativesComparison.getWeight(), alternativesComparison.getDelta())
						.getLeft();

				Double hache = Tools.score(alternativesComparison.getX(), alternativesComparison.getWeight())
						- Tools.score(alternativesComparison.getY(), alternativesComparison.getWeight());

				logger.debug(k + " " + i + " sum better than hache? " + sum + " >= " + hache);

				if (sum >= hache) { // L3
					logger.debug(k + " " + i + " Adding to F" + Tools.showCriteria(setCIVT.get(i)));
					a_copy.add(setCIVT.get(i));
					f = new ArrayList<>(a_copy); // L4
				} else { // L5
					a_copy.add(setCIVT.get(i));
					logger.debug(k + " " + i + " Calling algo_eu" + Tools.showSet(a_copy) + " " + Tools.showSet(b)
							+ " , " + (i + 1));
					f = algoEU(a_copy, b_copy, (i + 1)); // L6

				}

				logger.debug(k + " " + i + " Test for update :" + !f.isEmpty() + " and [ " + b_copy.isEmpty() + " or "
						+ Tools.includeDiscri(f, b, alternativesComparison.getWeight(),
								alternativesComparison.getDelta())
						+ " ]");

				if (!f.isEmpty() && (b_copy.isEmpty() || Tools.includeDiscri(f, b_copy,
						alternativesComparison.getWeight(), alternativesComparison.getDelta()))) { // L8
					logger.debug(k + " " + i + " UPDATE");
					b_copy = new ArrayList<>(f); // L8
				}

				logger.debug(k + " " + i + " A" + Tools.showSet(a));
				logger.debug(k + " " + i + " B" + Tools.showSet(b));
				logger.debug(
						k + " " + i + " Test for return B :" + !b.isEmpty() + " and " + !Tools.includeDiscri(a_copy, b,
								alternativesComparison.getWeight(), alternativesComparison.getDelta()));

				if (!b_copy.isEmpty() && !Tools.includeDiscri(a_copy, b_copy, alternativesComparison.getWeight(),
						alternativesComparison.getDelta())) { // L10
					return b_copy;
				}
			}

			a_copy.clear();
		}
		List<List<Criterion>> empty = new ArrayList<>();

		logger.debug("#### END ALGO EU : k = " + k);

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
		return computeExplanation();
	}

	/**
	 * @return the right explanation of the problem given in alternativesComparison.
	 * @throws IllegalException
	 *             if none anchors applicable This function can be called only if
	 *             labreucheOutput is null.
	 */
	private LabreucheOutput computeExplanation() {
		Preconditions.checkState(labreucheOutput == null);

		if (tryALL()) {
			return labreucheOutput;
		}
		if (tryNOA()) {
			return labreucheOutput;
		}
		if (tryIVT()) {
			return labreucheOutput;
		}
		if (tryRMGAVG()) {
			return labreucheOutput;
		}
		if (tryRMGCOMP()) {
			return labreucheOutput;
		}
		throw new IllegalStateException();
	}

	private boolean tryALL() {
		for (ImmutableMap.Entry<Criterion, Double> d : alternativesComparison.getDelta().entrySet()) {
			if (d.getValue() < 0.0) {
				logger.info("ALL false");
				return false;
			}
		}

		logger.info("ALL true");
		labreucheOutput = new ALLOutput(alternativesComparison);

		return true;
	}

	private boolean tryNOA() {
		if (Tools.score(alternativesComparison.getX(), alternativesComparison.getWeightReference()) >= Tools
				.score(alternativesComparison.getY(), alternativesComparison.getWeightReference())) {
			logger.info("NOA false");
			return false;
		}

		Map<Double, Criterion> temp = new HashMap<>();
		Set<Criterion> setC = new LinkedHashSet<>();

		for (Map.Entry<Criterion, Double> w_i : alternativesComparison.getWeight().entrySet()) {
			temp.put((w_i.getValue() - 1.0 / alternativesComparison.getCriteria().size())
					* alternativesComparison.getDelta().get(w_i.getKey()), w_i.getKey());
		}

		ArrayList<Double> keys = new ArrayList<>(temp.keySet());
		Collections.sort(keys);

		Map<Criterion, Double> wcomposed = Maps.newHashMap(alternativesComparison.getWeightReference());
		int p = keys.size() - 1;
		double scoreX = 0.0;
		double scoreY = 0.0;

		do {
			wcomposed.put(temp.get(keys.get(p)), alternativesComparison.getWeight().get((temp.get(keys.get(p)))));
			setC.add(temp.get(keys.get(p)));

			scoreX = Tools.score(alternativesComparison.getX(), wcomposed);
			scoreY = Tools.score(alternativesComparison.getY(), wcomposed);

			p--;
		} while (scoreX < scoreY);

		labreucheOutput = new NOAOutput(alternativesComparison, setC);

		return true;

	}

	private boolean tryIVT() {
		int size = 2;
		List<List<Criterion>> subsets = new ArrayList<>();
		List<List<Criterion>> big_a = new ArrayList<>(); // -> first variable for algo_eu calls
		List<List<Criterion>> big_b = new ArrayList<>(); // -> second variable for algo_eu calls
		List<List<Criterion>> big_c = new ArrayList<>(); // result of algo_eu
		Double d_eu = null;
		List<Criterion> pi = new ArrayList<>();

		logger.debug(
				"Delta " + alternativesComparison.getX().getName() + " > " + alternativesComparison.getY().getName()
						+ " : " + Tools.showVector(alternativesComparison.getDelta().values()) + "\n");

		do {
			d_eu = null;
			pi.clear();
			big_a.clear();
			big_b.clear();
			big_c.clear();
			subsets.clear();
			setCIVT.clear();

			subsets = Tools.allSubset(new ArrayList<>(alternativesComparison.getCriteria()));

			for (List<Criterion> subset : subsets) {
				d_eu = Tools.d_eu(subset, alternativesComparison.getWeight(), alternativesComparison.getDelta())
						.getLeft();
				pi = Tools.pi_min(subset, alternativesComparison.getWeight(), alternativesComparison.getDelta());
				if (d_eu > 0 && pi.containsAll(subset) && subset.containsAll(pi) && pi.containsAll(subset)) {
					setCIVT.add(subset);
				}

				pi.clear();
			}

			setCIVT = Tools.sortLexi(setCIVT, alternativesComparison.getWeight(), alternativesComparison.getDelta());

			logger.info("setCIVT : ");
			for (List<Criterion> l : setCIVT)
				logger.debug(Tools.showCriteria(l) + " "
						+ Tools.d_eu(l, alternativesComparison.getWeight(), alternativesComparison.getDelta()) + " "
						+ Tools.showCriteria(Tools.pi_min(l, alternativesComparison.getWeight(),
								alternativesComparison.getDelta())));

			big_c = algoEU(big_a, big_b, 0);
			size++;

		} while (big_c.isEmpty() && size <= alternativesComparison.getCriteria().size());

		if (big_c.isEmpty()) {
			logger.info("IVT false");
			return false;
		}

		// Start to determine R*

		List<Couple<Criterion, Criterion>> cpls = new ArrayList<>();
		List<Couple<Criterion, Criterion>> r_s;

		logger.info("Minimal permutation : " + Tools.showSet(big_c) + "\n");

		for (List<Criterion> l : big_c) {
			r_s = Tools.couples_of(l, alternativesComparison.getWeight(), alternativesComparison.getDelta());

			for (Couple<Criterion, Criterion> cp : r_s) {
				if (!cpls.contains(cp)) {
					cpls.add(cp);
				}
			}

			r_s = null;
		}

		ImmutableGraph<Criterion> rStar = Tools.buildRStar(cpls);

		// R* determined

		labreucheOutput = new IVTOutput(alternativesComparison, rStar, epsilon);

		return true;
	}

	private boolean tryRMGAVG() {
		double max_w = Double.MIN_VALUE;

		for (Map.Entry<Criterion, Double> c : alternativesComparison.getWeight().entrySet()) {
			double v = Math.abs(c.getValue() - (1.0 / alternativesComparison.getCriteria().size()));

			if (v > max_w) {
				max_w = v;
			}
		}

		if (max_w <= epsilon) {
			labreucheOutput = new RMGAVGOutput(alternativesComparison);

			return true;
		}

		logger.info("RMGAVG false");

		return false;
	}

	private boolean tryRMGCOMP() {
		double max_w1 = Double.MIN_VALUE;

		for (Map.Entry<Criterion, Double> c : alternativesComparison.getWeight().entrySet()) {
			double v = Math.abs(c.getValue() - (1.0 / alternativesComparison.getCriteria().size()));

			if (v > max_w1) {
				max_w1 = v;
			}
		}

		if (max_w1 > epsilon) {
			labreucheOutput = new RMGCOMPOutput(alternativesComparison, epsilon);

			return true;
		}

		logger.info("RMGCOMP false");

		return false;
	}

	public boolean isApplicable(Anchor anchor) {
		getExplanation();

		if (labreucheOutput.getAnchor() == anchor) {
			return true;
		}
		return false;
	}

	/**
	 * @param anchor
	 *            : the type of Anchor explanation claimed
	 * @throws IllegalArgumentException
	 *             if anchor type is not the same a labreucheOutput value.
	 */
	private LabreucheOutput getCheckExplanation(Anchor ancrage) {
		getExplanation();
		Preconditions.checkState(labreucheOutput.getAnchor() == ancrage, "This anchor is not the one applicable");
		return labreucheOutput;
	}

	public ALLOutput getALLExplanation() {
		return (ALLOutput) getCheckExplanation(Anchor.ALL);
	}

	public NOAOutput getNOAExplanation() {
		return (NOAOutput) getCheckExplanation(Anchor.NOA);
	}

	public IVTOutput getIVTExplanation() {
		return (IVTOutput) getCheckExplanation(Anchor.IVT);
	}

	public RMGAVGOutput getRMGAVGExplanation() {
		return (RMGAVGOutput) getCheckExplanation(Anchor.RMGAVG);
	}

	public RMGCOMPOutput getRMGCOMPExplanation() {
		return (RMGCOMPOutput) getCheckExplanation(Anchor.RMGCOMP);
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

	public void showProblem() {

		String display = "****************************************************************";
		display += "\n" + "*                                                              *";
		display += "\n" + "*         Recommender system based on Labreuche Model          *";
		display += "\n" + "*                                                              *";
		display += "\n" + "****************************************************************" + "\n";

		display += "\n    Criteria    <-   Weight : \n";

		for (Criterion c : alternativesComparison.getWeight().keySet())
			display += "\n" + "	" + c.getName() + "  <-  w_" + c.getId() + " = "
					+ alternativesComparison.getWeight().get(c);

		display += "\n \n Alternatives : ";

		display += "\n" + "	" + alternativesComparison.getX().getName() + " " + " : "
				+ Tools.showVector(alternativesComparison.getX().getEvaluations().values());
		display += "\n" + "	" + alternativesComparison.getY().getName() + " " + " : "
				+ Tools.showVector(alternativesComparison.getY().getEvaluations().values());

		display += "\n" + "			Alternatives ranked";
		display += "\n" + alternativesComparison.getX().getName() + " = "
				+ Tools.score(alternativesComparison.getX(), alternativesComparison.getWeight());
		display += "\n" + alternativesComparison.getY().getName() + " = "
				+ Tools.score(alternativesComparison.getY(), alternativesComparison.getWeight());

		display = "Explanation why " + alternativesComparison.getX().getName() + " is better than "
				+ alternativesComparison.getY().getName() + " :";

		logger.info(display);
	}

	public void solvesProblem() {
		showProblem();

		@SuppressWarnings("unused")
		LabreucheOutput lo = getExplanation();

		String c = arguer();

		logger.info(c);
	}

}