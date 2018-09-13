package io.github.oliviercailloux.decision.arguer.labreuche;

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
import com.google.common.collect.Maps;
import com.google.common.graph.EndpointPair;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.ImmutableGraph;
import com.google.common.graph.MutableGraph;

import io.github.oliviercailloux.decision.Utils;
import io.github.oliviercailloux.decision.arguer.Arguer;
import io.github.oliviercailloux.decision.arguer.labreuche.output.ALLOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.Anchor;
import io.github.oliviercailloux.decision.arguer.labreuche.output.IVTOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.LabreucheOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.NOAOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.RMGAVGOutput;
import io.github.oliviercailloux.decision.arguer.labreuche.output.RMGCOMPOutput;
import io.github.oliviercailloux.uta_calculator.model.Criterion;

public class LabreucheModel {

	/**
	 * @param labreuchOutput
	 *            equal null iff it is not initialized.
	 */

	private AlternativesComparison alternativesComparison;
	private double epsilon;
	private List<List<Criterion>> ivtPermutation;
	private List<List<Criterion>> setCIVT;
	private LabreucheOutput labreucheOutput;
	private static final Logger LOGGER = LoggerFactory.getLogger(LabreucheModel.class);

	public LabreucheModel(AlternativesComparison alternativesComparaison) {
		this.alternativesComparison = requireNonNull(alternativesComparaison);

		this.ivtPermutation = new ArrayList<>();
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

	public List<List<Criterion>> getIVTPermutation() {
		return this.ivtPermutation;
	}

	/****************************************************************************************
	 * * METHODS * *
	 ****************************************************************************************/

	/**
	 * @return the minimal set of permutation that inverse the decision between x
	 *         and y.
	 */
	private List<List<Criterion>> algo(List<List<Criterion>> c, List<List<Criterion>> b, int k) {
		LOGGER.debug("CALLING ALGO( " + Utils.showSet(c) + " , " + Utils.showSet(b) + " , " + k + " )");

		List<List<Criterion>> c_copy = new ArrayList<>(c);
		List<List<Criterion>> b_copy;

		if (b == null) {
			b_copy = null;
		} else {
			b_copy = new ArrayList<>(b);
		}

		List<List<Criterion>> cPrime = new ArrayList<>();
		List<Criterion> currentPerm = new ArrayList<>();

		for (int i = k; i < setCIVT.size(); i++) {
			currentPerm = setCIVT.get(i);

			LOGGER.debug(k + " " + i + " Current permutation : " + Utils.showCriteria(currentPerm) + " current c = "
					+ Utils.showSet(c) + " cap = " + Tools.isCapEmpty(c, currentPerm));

			if (Tools.isCapEmpty(c, currentPerm)) {
				cPrime = new ArrayList<>(c_copy);
				cPrime.add(currentPerm);

				double sum = 0.0;

				if (!cPrime.isEmpty()) {
					for (List<Criterion> perm : cPrime) {
						sum += Tools.d_eu(perm, alternativesComparison.getWeight(), alternativesComparison.getDelta())
								.getLeft();
					}
				}

				double vXminusVY = Tools.score(alternativesComparison.getX(), alternativesComparison.getWeight())
						- Tools.score(alternativesComparison.getY(), alternativesComparison.getWeight());

				c_copy.add(currentPerm);

				if (sum < vXminusVY) {
					LOGGER.debug("START BRANCHING : c = " + Utils.showSet(c_copy) + " b = " + Utils.showSet(b_copy)
							+ " " + (i + 1));

					if (b == null) {
						cPrime = algo(c_copy, null, i + 1);
					} else {
						cPrime = algo(c_copy, b_copy, i + 1);
					}

					LOGGER.debug("END BRANCHING : c = " + Utils.showSet(c_copy) + " b = " + Utils.showSet(b_copy) + " "
							+ (i + 1));
				}

				LOGGER.debug(Utils.showSet(cPrime) + " inclu_discri " + Utils.showSet(b_copy));
				if (Tools.includeDiscri(cPrime, b_copy, alternativesComparison.getWeight(),
						alternativesComparison.getDelta())) {
					LOGGER.debug("UPDATE B!");
					b_copy = new ArrayList<>(cPrime);
				}

				// c_copy.add(currentPerm);

				LOGGER.debug(Utils.showSet(c_copy) + " incluDiscri " + Utils.showSet(b_copy));

				if (!Tools.includeDiscri(c_copy, b_copy, alternativesComparison.getWeight(),
						alternativesComparison.getDelta())) {
					LOGGER.debug("RETURN B");
					return b_copy;
				}
			}
			c_copy = new ArrayList<>(c);
		}

		LOGGER.debug("RETURN INVALID");
		return null;
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
	 *             if none anchors applicable.
	 * @precondition : labreucheOutput == null.
	 */
	private LabreucheOutput computeExplanation() {
		Preconditions.checkState(labreucheOutput == null);

		if (tryALL()) {
			assert labreucheOutput != null;
			return labreucheOutput;
		}
		if (tryNOA()) {
			assert labreucheOutput != null;
			return labreucheOutput;
		}
		if (tryIVT()) {
			assert labreucheOutput != null;
			return labreucheOutput;
		}
		if (tryRMGAVG()) {
			assert labreucheOutput != null;
			return labreucheOutput;
		}
		if (tryRMGCOMP()) {
			assert labreucheOutput != null;
			return labreucheOutput;
		}
		throw new IllegalStateException();
	}

	private boolean tryALL() {
		Preconditions.checkState(labreucheOutput == null);

		for (Double v : alternativesComparison.getDelta().values()) {
			if (v < 0.0) {
				LOGGER.info("ALL false");
				return false;
			}
		}

		LOGGER.info("ALL true");
		labreucheOutput = new ALLOutput(alternativesComparison);

		return true;
	}

	private boolean tryNOA() {
		Preconditions.checkState(labreucheOutput == null);

		if (Tools.score(alternativesComparison.getX(), alternativesComparison.getWeightReference()) >= Tools
				.score(alternativesComparison.getY(), alternativesComparison.getWeightReference())) {
			LOGGER.info("NOA false");
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

		LOGGER.info("NOA true");
		labreucheOutput = new NOAOutput(alternativesComparison, setC);

		return true;

	}

	private boolean tryIVT() {
		Preconditions.checkState(labreucheOutput == null);

		int size = 2;
		List<List<Criterion>> subsets = new ArrayList<>();
		List<List<Criterion>> big_a = new ArrayList<>(); // -> first variable for algo() calls
		List<List<Criterion>> big_b = new ArrayList<>(); // -> second variable for algo() calls
		List<List<Criterion>> big_c; // result of algo()
		Double d_eu = null;
		List<Criterion> pi = new ArrayList<>();

		LOGGER.debug(
				"Delta " + alternativesComparison.getX().getName() + " > " + alternativesComparison.getY().getName()
						+ " : " + Utils.showVector(alternativesComparison.getDelta().values()) + "\n");

		do {
			d_eu = null;
			pi.clear();
			big_a.clear();
			big_b.clear();
			subsets.clear();
			setCIVT.clear();

			subsets = Tools.allSubset(new ArrayList<>(alternativesComparison.getCriteria()));

			for (List<Criterion> subset : subsets) {
				Couple<Double, List<Criterion>> res = Tools.d_eu(subset, alternativesComparison.getWeight(),
						alternativesComparison.getDelta());
				d_eu = res.getLeft();
				pi = res.getRight();

				if (d_eu > 0 && pi.containsAll(subset) && subset.containsAll(pi)) {
					setCIVT.add(subset);
				}

				pi.clear();
			}

			setCIVT = Tools.sortLexi(setCIVT, alternativesComparison.getWeight(), alternativesComparison.getDelta());

			LOGGER.debug("setCIVT : ");
			for (List<Criterion> l : setCIVT)
				LOGGER.debug(Utils.showCriteria(l) + " "
						+ Tools.d_eu(l, alternativesComparison.getWeight(), alternativesComparison.getDelta()) + " "
						+ Utils.showCriteria(Tools.pi_min(l, alternativesComparison.getWeight(),
								alternativesComparison.getDelta())));

			big_c = algo(big_a, null, 0);
			size++;

		} while (big_c == null && size <= alternativesComparison.getCriteria().size());

		ivtPermutation = big_c;

		if (ivtPermutation == null) {
			return false;
		}

		// Start to determine R*

		MutableGraph<Criterion> cpls = GraphBuilder.directed().build();
		ImmutableGraph<Criterion> r_s;

		LOGGER.debug("Minimal permutation : " + Utils.showSet(ivtPermutation) + "\n");

		for (List<Criterion> l : ivtPermutation) {

			r_s = Tools.couples_ofG(l, alternativesComparison.getWeight(), alternativesComparison.getDelta());

			for (EndpointPair<Criterion> cp : r_s.edges()) {
				cpls.putEdge(cp.nodeU(), cp.nodeV());
			}

			r_s = null;
		}

		ImmutableGraph<Criterion> rStar = Tools.buildRStarG(ImmutableGraph.copyOf(cpls));

		// R* determined

		LOGGER.info("IVT true");
		labreucheOutput = new IVTOutput(alternativesComparison, rStar, epsilon);

		return true;
	}

	private boolean tryRMGAVG() {
		Preconditions.checkState(labreucheOutput == null);

		double max_w = getMaxW();

		if (max_w <= epsilon) {
			LOGGER.info("RMGAVG true");

			labreucheOutput = new RMGAVGOutput(alternativesComparison);

			return true;
		}

		LOGGER.info("RMGAVG false");

		return false;
	}

	private double getMaxW() {
		double max_w = Double.MIN_VALUE;

		for (Double v : alternativesComparison.getWeight().values()) {
			double value = Math.abs(v - (1.0 / alternativesComparison.getCriteria().size()));

			if (value > max_w) {
				max_w = value;
			}
		}
		return max_w;
	}

	private boolean tryRMGCOMP() {
		Preconditions.checkState(labreucheOutput == null);

		double max_w1 = getMaxW();

		if (max_w1 > epsilon) {
			LOGGER.info("RMGCOMP true");

			labreucheOutput = new RMGCOMPOutput(alternativesComparison, epsilon);

			return true;
		}

		LOGGER.info("RMGCOMP false");

		return false;
	}

	public boolean isApplicable(Anchor anchor) {
		getExplanation();

		return labreucheOutput.getAnchor() == anchor;
	}

	/**
	 * @param anchor
	 *            : the type of Anchor explanation claimed
	 * @throws IllegalArgumentException
	 *             if anchor type is not the same a labreucheOutput value.
	 */
	private LabreucheOutput getCheckedExplanation(Anchor ancrage) {
		getExplanation();
		Preconditions.checkState(labreucheOutput.getAnchor() == ancrage, "This anchor is not the one applicable");
		return labreucheOutput;
	}

	public ALLOutput getALLExplanation() {
		return (ALLOutput) getCheckedExplanation(Anchor.ALL);
	}

	public NOAOutput getNOAExplanation() {
		return (NOAOutput) getCheckedExplanation(Anchor.NOA);
	}

	public IVTOutput getIVTExplanation() {
		return (IVTOutput) getCheckedExplanation(Anchor.IVT);
	}

	public RMGAVGOutput getRMGAVGExplanation() {
		return (RMGAVGOutput) getCheckedExplanation(Anchor.RMGAVG);
	}

	public RMGCOMPOutput getRMGCOMPExplanation() {
		return (RMGCOMPOutput) getCheckedExplanation(Anchor.RMGCOMP);
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
				+ Utils.showVector(alternativesComparison.getX().getEvaluations().values());
		display += "\n" + "	" + alternativesComparison.getY().getName() + " " + " : "
				+ Utils.showVector(alternativesComparison.getY().getEvaluations().values());

		display += "\n" + "			Alternatives ranked";
		display += "\n" + alternativesComparison.getX().getName() + " = "
				+ Tools.score(alternativesComparison.getX(), alternativesComparison.getWeight());
		display += "\n" + alternativesComparison.getY().getName() + " = "
				+ Tools.score(alternativesComparison.getY(), alternativesComparison.getWeight());

		display = "Explanation why " + alternativesComparison.getX().getName() + " is better than "
				+ alternativesComparison.getY().getName() + " :";

		LOGGER.info(display);
	}

	public void solvesProblem() {
		showProblem();

		LabreucheOutput lo = getExplanation();

		Arguer arg = new Arguer();
		String explanation = arg.argue(lo);

		LOGGER.info(explanation);
	}
}